package com.sleektiv.mes.materialFlowResources.hooks;

import com.google.common.base.Optional;
import com.sleektiv.commons.functional.Either;
import com.sleektiv.mes.advancedGenealogy.criteriaModifier.BatchCriteriaModifier;
import com.sleektiv.mes.basic.CalculationQuantityService;
import com.sleektiv.mes.basic.constants.PalletNumberFields;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.materialFlowResources.MaterialFlowResourcesService;
import com.sleektiv.mes.materialFlowResources.constants.ResourceFields;
import com.sleektiv.model.api.BigDecimalUtils;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.NumberService;
import com.sleektiv.security.api.SecurityService;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class ResourceDetailsHooks {

    private static final String L_PRICE_CURRENCY = "priceCurrency";

    private static final String L_ROLE_RESOURCE_PRICE = "ROLE_RESOURCE_PRICE";

    @Autowired
    private NumberService numberService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private CalculationQuantityService calculationQuantityService;

    @Autowired
    private BatchCriteriaModifier batchCriteriaModifier;

    @Autowired
    private MaterialFlowResourcesService materialFlowResourcesService;

    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent resourceForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Entity resource = resourceForm.getPersistedEntityWithIncludedFormValues();

        materialFlowResourcesService.fillUnitFieldValues(view);
        materialFlowResourcesService.fillCurrencyFieldValues(view);

        fillUnitField(view, resource);
        togglePriceFields(view);

        setStorageLocationLookupFilterValue(view, resource);
        setBatchLookupProductFilterValue(view, resource);
    }

    private void fillUnitField(final ViewDefinitionState view, final Entity resource) {
        FieldComponent givenUnitField = (FieldComponent) view.getComponentByReference(ResourceFields.GIVEN_UNIT);

        String givenUnit = resource.getStringField(ResourceFields.GIVEN_UNIT);

        givenUnitField.setFieldValue(givenUnit);
        givenUnitField.requestComponentUpdateState();
    }

    private void togglePriceFields(final ViewDefinitionState view) {
        FieldComponent priceField = (FieldComponent) view.getComponentByReference(ResourceFields.PRICE);
        FieldComponent priceCurrencyField = (FieldComponent) view.getComponentByReference(L_PRICE_CURRENCY);

        boolean hasCurrentUserRole = securityService.hasCurrentUserRole(L_ROLE_RESOURCE_PRICE);

        priceField.setVisible(hasCurrentUserRole);
        priceCurrencyField.setVisible(hasCurrentUserRole);
    }

    private void setStorageLocationLookupFilterValue(final ViewDefinitionState view, final Entity resource) {
        LookupComponent storageLocationLookup = (LookupComponent) view.getComponentByReference(ResourceFields.STORAGE_LOCATION);

        FilterValueHolder filter = storageLocationLookup.getFilterValue();

        Entity warehouse = resource.getBelongsToField(ResourceFields.LOCATION);

        if (Objects.nonNull(warehouse)) {
            filter.put(ResourceFields.LOCATION, warehouse.getId());
        }

        storageLocationLookup.setFilterValue(filter);
    }

    private void setBatchLookupProductFilterValue(final ViewDefinitionState view, final Entity resource) {
        LookupComponent batchLookup = (LookupComponent) view.getComponentByReference(ResourceFields.BATCH);

        Entity product = resource.getBelongsToField(ResourceFields.PRODUCT);

        if (Objects.nonNull(product)) {
            batchCriteriaModifier.putProductFilterValue(batchLookup, product);
        }
    }

    public void onConversionChange(final ViewDefinitionState viewDefinitionState, final ComponentState state, final String[] args) {
        FieldComponent conversionField = (FieldComponent) viewDefinitionState.getComponentByReference(ResourceFields.CONVERSION);
        FieldComponent quantityInAdditionalUnitField = (FieldComponent) viewDefinitionState
                .getComponentByReference(ResourceFields.QUANTITY_IN_ADDITIONAL_UNIT);
        FieldComponent additionalUnitField = (FieldComponent) viewDefinitionState
                .getComponentByReference(ResourceFields.GIVEN_UNIT);

        Either<Exception, Optional<BigDecimal>> maybeConversion = BigDecimalUtils
                .tryParseAndIgnoreSeparator((String) conversionField.getFieldValue(), viewDefinitionState.getLocale());

        if (maybeConversion.isRight() && maybeConversion.getRight().isPresent()) {
            FormComponent resourceForm = (FormComponent) viewDefinitionState.getComponentByReference(SleektivViewConstants.L_FORM);

            Entity resource = resourceForm.getPersistedEntityWithIncludedFormValues();

            BigDecimal newAdditionalQuantity = calculationQuantityService.calculateAdditionalQuantity(
                    resource.getDecimalField(ResourceFields.QUANTITY), maybeConversion.getRight().get(),
                    (String) additionalUnitField.getFieldValue());

            String quantityInAdditionalUnitFormatted = numberService.format(newAdditionalQuantity);

            quantityInAdditionalUnitField.setFieldValue(quantityInAdditionalUnitFormatted);
        }

    }

    public void onQuantityChange(final ViewDefinitionState viewDefinitionState, final ComponentState state, final String[] args) {
        FieldComponent quantityField = (FieldComponent) viewDefinitionState.getComponentByReference(ResourceFields.QUANTITY);
        FieldComponent quantityInAdditionalUnitField = (FieldComponent) viewDefinitionState
                .getComponentByReference(ResourceFields.QUANTITY_IN_ADDITIONAL_UNIT);
        FieldComponent additionalUnitField = (FieldComponent) viewDefinitionState
                .getComponentByReference(ResourceFields.GIVEN_UNIT);

        Either<Exception, Optional<BigDecimal>> maybeQuantity = BigDecimalUtils
                .tryParseAndIgnoreSeparator((String) quantityField.getFieldValue(), viewDefinitionState.getLocale());

        if (maybeQuantity.isRight() && maybeQuantity.getRight().isPresent()) {
            FormComponent resourceForm = (FormComponent) viewDefinitionState.getComponentByReference(SleektivViewConstants.L_FORM);

            Entity resource = resourceForm.getEntity();

            BigDecimal newAdditionalQuantity = calculationQuantityService.calculateAdditionalQuantity(
                    maybeQuantity.getRight().get(), resource.getDecimalField(ResourceFields.CONVERSION),
                    (String) additionalUnitField.getFieldValue());

            String quantityInAdditionalUnitFormatted = numberService.format(newAdditionalQuantity);

            quantityInAdditionalUnitField.setFieldValue(quantityInAdditionalUnitFormatted);
        } else {
            quantityInAdditionalUnitField.setFieldValue(null);
        }
    }

    public void onQuantityInAdditionalUnitChange(final ViewDefinitionState viewDefinitionState, final ComponentState state,
                                                 final String[] args) {
        FieldComponent quantityField = (FieldComponent) viewDefinitionState.getComponentByReference(ResourceFields.QUANTITY);
        FieldComponent quantityInAdditionalUnitField = (FieldComponent) viewDefinitionState
                .getComponentByReference(ResourceFields.QUANTITY_IN_ADDITIONAL_UNIT);

        Either<Exception, Optional<BigDecimal>> maybeQuantityInAdditionalUnit = BigDecimalUtils.tryParseAndIgnoreSeparator(
                (String) quantityInAdditionalUnitField.getFieldValue(), viewDefinitionState.getLocale());

        if (maybeQuantityInAdditionalUnit.isRight() && maybeQuantityInAdditionalUnit.getRight().isPresent()) {
            FormComponent resourceForm = (FormComponent) viewDefinitionState.getComponentByReference(SleektivViewConstants.L_FORM);

            Entity resource = resourceForm.getEntity();

            BigDecimal conversion = resource.getDecimalField(ResourceFields.CONVERSION);
            Entity product = resource.getBelongsToField(ResourceFields.PRODUCT);

            BigDecimal quantity = calculationQuantityService.calculateQuantity(maybeQuantityInAdditionalUnit.getRight().get(),
                    conversion, product.getStringField(ProductFields.UNIT));

            String quantityFormatted = numberService.format(numberService.setScaleWithDefaultMathContext(quantity));

            quantityField.setFieldValue(quantityFormatted);
        } else {
            quantityField.setFieldValue(null);
        }
    }

    public void fillTypeOfPalletField(final ViewDefinitionState view) {
        LookupComponent locationLookup = (LookupComponent) view.getComponentByReference(ResourceFields.LOCATION);
        LookupComponent palletNumberLookup = (LookupComponent) view.getComponentByReference(ResourceFields.PALLET_NUMBER);
        FieldComponent typeOfPalletField = (FieldComponent) view.getComponentByReference(ResourceFields.TYPE_OF_PALLET);

        Entity location = locationLookup.getEntity();
        Entity palletNumber = palletNumberLookup.getEntity();
        String typeOfPallet = null;

        if (Objects.nonNull(palletNumber)) {
            typeOfPallet = materialFlowResourcesService.getTypeOfPalletByPalletNumber(location.getId(), palletNumber.getStringField(PalletNumberFields.NUMBER));
        }

        typeOfPalletField.setFieldValue(typeOfPallet);
        typeOfPalletField.requestComponentUpdateState();
    }

}
