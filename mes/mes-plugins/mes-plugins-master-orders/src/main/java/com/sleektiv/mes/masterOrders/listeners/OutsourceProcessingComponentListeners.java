package com.sleektiv.mes.masterOrders.listeners;

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.masterOrders.OrdersGenerationService;
import com.sleektiv.mes.masterOrders.constants.OutsourceProcessingComponentHelperFields;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.technologies.ProductQuantitiesService;
import com.sleektiv.mes.technologies.constants.TechnologyFields;
import com.sleektiv.mes.technologies.dto.OperationProductComponentHolder;
import com.sleektiv.model.api.DictionaryService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.NumberService;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.CheckBoxComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Service
public class OutsourceProcessingComponentListeners {

    @Autowired
    private ParameterService parameterService;

    @Autowired
    private NumberService numberService;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private OrdersGenerationService ordersGenerationService;

    @Autowired
    private ProductQuantitiesService productQuantitiesService;

    public void generateOrder(final ViewDefinitionState view, final ComponentState state, final String[] args)
            throws JSONException {
        FormComponent outsourceProcessingComponentForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        CheckBoxComponent generatedCheckBox = (CheckBoxComponent) view.getComponentByReference("generated");

        Entity outsourceProcessingComponent = outsourceProcessingComponentForm.getPersistedEntityWithIncludedFormValues();

        outsourceProcessingComponent = outsourceProcessingComponent.getDataDefinition().validate(outsourceProcessingComponent);

        if (!outsourceProcessingComponent.isValid()) {
            outsourceProcessingComponentForm.setEntity(outsourceProcessingComponent);

            return;
        }

        Entity order = generateOrder(outsourceProcessingComponent);

        if (order.isValid()) {
            view.addMessage("masterOrders.outsourceProcessingComponent.info.generatedOrder", ComponentState.MessageType.SUCCESS, order.getStringField(OrderFields.NUMBER));
        } else {
            order.getGlobalErrors().stream().filter(error ->
                    !error.getMessage().equals("sleektivView.validate.global.error.custom")).forEach(error ->
                    view.addMessage(error.getMessage(), ComponentState.MessageType.FAILURE, error.getVars())
            );

            order.getErrors().values().forEach(error ->
                    view.addMessage(error.getMessage(), ComponentState.MessageType.FAILURE, error.getVars())
            );

            view.addMessage("masterOrders.outsourceProcessingComponent.info.notGeneratedOrder", ComponentState.MessageType.FAILURE);
        }

        generatedCheckBox.setChecked(true);
    }

    private Entity generateOrder(final Entity outsourceProcessingComponent) {
        Entity product = outsourceProcessingComponent.getBelongsToField(OutsourceProcessingComponentHelperFields.PRODUCT);
        BigDecimal quantity = outsourceProcessingComponent.getDecimalField(OutsourceProcessingComponentHelperFields.QUANTITY);
        Entity technology = outsourceProcessingComponent.getBelongsToField(OutsourceProcessingComponentHelperFields.TECHNOLOGY);
        Date dateFrom = outsourceProcessingComponent.getDateField(OutsourceProcessingComponentHelperFields.DATE_FROM);
        Date dateTo = outsourceProcessingComponent.getDateField(OutsourceProcessingComponentHelperFields.DATE_TO);

        BigDecimal plannedQuantity = calculatePlannedQuantity(technology, product, quantity);

        return ordersGenerationService.createOrder(parameterService.getParameter(), technology, technology.getBelongsToField(TechnologyFields.PRODUCT), plannedQuantity, null, dateFrom, dateTo);
    }

    private BigDecimal calculatePlannedQuantity(final Entity technology, final Entity product, final BigDecimal quantity) {
        BigDecimal plannedQuantity = BigDecimal.ONE;

        Map<OperationProductComponentHolder, BigDecimal> neededQuantities = productQuantitiesService
                .getNeededProductQuantities(technology, technology.getBelongsToField(TechnologyFields.PRODUCT), BigDecimal.ONE);

        for (Map.Entry<OperationProductComponentHolder, BigDecimal> neededProductQuantity : neededQuantities.entrySet()) {
            Long productId = neededProductQuantity.getKey().getProductId();

            if (Objects.nonNull(productId) && productId.equals(product.getId())) {
                BigDecimal neededQuantity = neededProductQuantity.getValue();

                plannedQuantity = quantity.divide(neededQuantity, 0, RoundingMode.FLOOR);
            }
        }

        return numberService.setScaleWithDefaultMathContext(plannedQuantity);
    }

}
