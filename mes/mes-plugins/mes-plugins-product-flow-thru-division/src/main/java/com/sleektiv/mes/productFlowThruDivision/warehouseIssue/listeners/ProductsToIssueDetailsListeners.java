package com.sleektiv.mes.productFlowThruDivision.warehouseIssue.listeners;

import com.google.common.collect.Lists;
import com.sleektiv.mes.basic.CalculationQuantityService;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.basic.constants.UnitConversionItemFieldsB;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.constans.ProductsToIssueFields;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.hooks.ProductToIssueDetailsHooks;
import com.sleektiv.model.api.BigDecimalUtils;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.NumberService;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.model.api.units.PossibleUnitConversions;
import com.sleektiv.model.api.units.UnitConversionService;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProductsToIssueDetailsListeners {

    @Autowired
    private NumberService numberService;

    @Autowired
    private UnitConversionService unitConversionService;

    @Autowired
    private ProductToIssueDetailsHooks productToIssueDetailsHooks;

    @Autowired
    private CalculationQuantityService calculationQuantityService;

    public void onProductSelect(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        productToIssueDetailsHooks.onBeforeRender(view);
        fillAdditionalUnit(view);
        onDemandQuantityChange(view, state, args);
    }

    public void onLocationSelect(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        productToIssueDetailsHooks.onBeforeRender(view);
    }

    public void onDemandQuantityChange(final ViewDefinitionState view, final ComponentState state,
                                       final String[] args) {
        FormComponent productToIssueForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Entity productToIssue = productToIssueForm.getPersistedEntityWithIncludedFormValues();

        if (!checkIfDecimalFieldsCorrect(productToIssue, productToIssueForm)) {
            return;
        }

        BigDecimal conversion = productToIssue.getDecimalField(ProductsToIssueFields.CONVERSION);
        BigDecimal demandQuantity = productToIssue.getDecimalField(ProductsToIssueFields.DEMAND_QUANTITY);

        if (Objects.nonNull(conversion) && Objects.nonNull(demandQuantity)) {
            FieldComponent additionalDemandQuantity = (FieldComponent) view
                    .getComponentByReference(ProductsToIssueFields.ADDITIONAL_DEMAND_QUANTITY);

            Entity product = productToIssue.getBelongsToField(ProductsToIssueFields.PRODUCT);
            BigDecimal newAdditionalQuantity = calculationQuantityService.calculateAdditionalQuantity(demandQuantity, conversion,
                    Optional.ofNullable(product.getStringField(ProductFields.ADDITIONAL_UNIT)).orElse(product.getStringField(ProductFields.UNIT)));

            additionalDemandQuantity.setFieldValue(numberService.formatWithMinimumFractionDigits(newAdditionalQuantity, 0));
            additionalDemandQuantity.requestComponentUpdateState();
        }
    }

    public void onAdditionalDemandQuantityChange(final ViewDefinitionState view, final ComponentState state,
                                                 final String[] args) {
        FormComponent productToIssueForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Entity productToIssue = productToIssueForm.getPersistedEntityWithIncludedFormValues();

        if (!checkIfDecimalFieldsCorrect(productToIssue, productToIssueForm)) {
            return;
        }

        BigDecimal conversion = productToIssue.getDecimalField(ProductsToIssueFields.CONVERSION);
        BigDecimal additionalDemandQuantity = productToIssue.getDecimalField(ProductsToIssueFields.ADDITIONAL_DEMAND_QUANTITY);

        if (Objects.nonNull(conversion) && Objects.nonNull(additionalDemandQuantity)) {
            FieldComponent demandQuantity = (FieldComponent) view.getComponentByReference(ProductsToIssueFields.DEMAND_QUANTITY);
            Entity product = productToIssue.getBelongsToField(ProductsToIssueFields.PRODUCT);

            BigDecimal newDemandQuantity = calculationQuantityService.calculateQuantity(additionalDemandQuantity, conversion,
                    product.getStringField(ProductFields.UNIT));

            demandQuantity.setFieldValue(numberService.formatWithMinimumFractionDigits(newDemandQuantity, 0));
            demandQuantity.requestComponentUpdateState();
        }
    }

    public void onConversionChange(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        onDemandQuantityChange(view, state, args);
    }

    private boolean checkIfDecimalFieldsCorrect(final Entity productToIssue, final FormComponent formComponent) {
        boolean isCorrectValue = true;

        List<String> fields = Lists.newArrayList(ProductsToIssueFields.DEMAND_QUANTITY,
                ProductsToIssueFields.ADDITIONAL_DEMAND_QUANTITY, ProductsToIssueFields.CONVERSION);

        for (String field : fields) {
            if (!BigDecimalUtils.checkIfCorrectDecimalValue(productToIssue, field)) {
                formComponent.findFieldComponentByName(field).addMessage("sleektivView.validate.field.error.invalidNumericFormat",
                        ComponentState.MessageType.FAILURE);

                isCorrectValue = false;
            }
        }

        return isCorrectValue;
    }

    private BigDecimal getConversion(final Entity product, final String unit, final String additionalUnit) {
        PossibleUnitConversions unitConversions = unitConversionService.getPossibleConversions(unit,
                searchCriteriaBuilder -> searchCriteriaBuilder.add(SearchRestrictions.belongsTo(
                        UnitConversionItemFieldsB.PRODUCT, product)));

        if (unitConversions.isDefinedFor(additionalUnit)) {
            return unitConversions.asUnitToConversionMap().get(additionalUnit);
        } else {
            return BigDecimal.ZERO;
        }
    }

    private void fillAdditionalUnit(final ViewDefinitionState view) {
        LookupComponent productLookup = (LookupComponent) view.getComponentByReference(ProductsToIssueFields.PRODUCT);
        FieldComponent conversionField = (FieldComponent) view.getComponentByReference(ProductsToIssueFields.CONVERSION);

        Entity product = productLookup.getEntity();

        if (Objects.nonNull(product)) {
            String unit = product.getStringField(ProductFields.UNIT);
            String additionalUnit = product.getStringField(ProductFields.ADDITIONAL_UNIT);

            if (!StringUtils.isEmpty(additionalUnit)) {
                String conversion = numberService
                        .formatWithMinimumFractionDigits(getConversion(product, unit, additionalUnit), 0);

                conversionField.setFieldValue(conversion);
                conversionField.setEnabled(true);
                conversionField.requestComponentUpdateState();
            }
        }
    }

}
