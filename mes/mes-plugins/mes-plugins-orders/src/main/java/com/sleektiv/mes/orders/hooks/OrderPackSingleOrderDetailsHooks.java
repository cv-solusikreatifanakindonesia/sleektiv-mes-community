package com.sleektiv.mes.orders.hooks;

import com.google.common.base.Optional;
import com.sleektiv.commons.functional.Either;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.orders.OrderPackService;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.orders.constants.OrderPackFields;
import com.sleektiv.model.api.BigDecimalUtils;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.NumberService;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class OrderPackSingleOrderDetailsHooks {

    @Autowired
    private NumberService numberService;

    @Autowired
    private OrderPackService orderPackService;

    public final void onBeforeRender(final ViewDefinitionState view) throws JSONException {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        LookupComponent orderLookup = (LookupComponent) view.getComponentByReference(OrderPackFields.ORDER);

        if (Objects.isNull(form.getEntityId())) {
            JSONObject context = view.getJsonContext();
            Long orderId = context.getLong("window.mainTab.orderPack.order");
            orderLookup.setFieldValue(orderId);
            orderLookup.requestComponentUpdateState();
        }

        Entity order = orderLookup.getEntity();

        FieldComponent orderQuantity = (FieldComponent) view.getComponentByReference("orderQuantity");
        FieldComponent sumQuantityOrderPacksField = (FieldComponent) view.getComponentByReference("sumQuantityOrderPacks");
        FieldComponent orderQuantityUnit = (FieldComponent) view.getComponentByReference("orderQuantityUnit");
        FieldComponent sumQuantityOrderPacksUnit = (FieldComponent) view.getComponentByReference("sumQuantityOrderPacksUnit");
        FieldComponent quantityUnit = (FieldComponent) view.getComponentByReference("quantityUnit");
        if (order != null) {
            orderQuantity.setFieldValue(numberService.format(order.getField(OrderFields.PLANNED_QUANTITY)));
            FieldComponent quantityField = (FieldComponent) view.getComponentByReference(OrderPackFields.QUANTITY);
            BigDecimal sumQuantityOrderPacks = orderPackService.getSumQuantityOrderPacksForOrderWithoutPack(order,
                    form.getEntityId());
            Either<Exception, Optional<BigDecimal>> eitherNumber = BigDecimalUtils
                    .tryParseAndIgnoreSeparator((String) quantityField.getFieldValue(), LocaleContextHolder.getLocale());
            if (eitherNumber.isRight() && eitherNumber.getRight().isPresent()) {
                sumQuantityOrderPacks = sumQuantityOrderPacks.add(eitherNumber.getRight().get(), numberService.getMathContext());
            }
            sumQuantityOrderPacksField.setFieldValue(numberService.format(sumQuantityOrderPacks));
            String unit = order.getBelongsToField(OrderFields.PRODUCT).getStringField(ProductFields.UNIT);
            orderQuantityUnit.setFieldValue(unit);
            sumQuantityOrderPacksUnit.setFieldValue(unit);
            quantityUnit.setFieldValue(unit);
        } else {
            orderQuantity.setFieldValue(null);
            sumQuantityOrderPacksField.setFieldValue(null);
            orderQuantityUnit.setFieldValue(null);
            sumQuantityOrderPacksUnit.setFieldValue(null);
            quantityUnit.setFieldValue(null);
        }
    }
}
