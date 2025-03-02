/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv MES
 * Version: 1.4
 *
 * This file is part of Sleektiv.
 *
 * Sleektiv is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.sleektiv.mes.deliveries.listeners;

import com.sleektiv.mes.basic.CalculationQuantityService;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.deliveries.DeliveriesService;
import com.sleektiv.mes.deliveries.constants.DeliveredProductFields;
import com.sleektiv.mes.deliveries.constants.DeliveryFields;
import com.sleektiv.mes.deliveries.hooks.DeliveredProductDetailsHooks;
import com.sleektiv.mes.materialFlowResources.MaterialFlowResourcesService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.NumberService;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ComponentState.MessageType;
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
import java.util.Optional;

@Service
public class DeliveredProductDetailsListeners {

    @Autowired
    private NumberService numberService;

    @Autowired
    private DeliveriesService deliveriesService;

    @Autowired
    private DeliveredProductDetailsHooks deliveredProductDetailsHooks;

    @Autowired
    private MaterialFlowResourcesService materialFlowResourcesService;

    @Autowired
    private CalculationQuantityService calculationQuantityService;

    public void onSelectedEntityChange(final ViewDefinitionState view, final ComponentState state,
                                       final String[] args) {
        fillConversion(view, state, args);
        quantityChange(view, state, args);
        fillOrderedQuantities(view, state, args);
        fillUnitFields(view, state, args);
        fillCurrencyFields(view, state, args);
        setBatchLookupProductFilterValue(view, state, args);
        setStorageLocationLookup(view);
    }

    public void fillConversion(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        deliveredProductDetailsHooks.fillConversion(view);
    }

    public void fillUnitFields(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        deliveredProductDetailsHooks.fillUnitFields(view);
    }

    public void fillCurrencyFields(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        deliveredProductDetailsHooks.fillCurrencyFields(view);
    }

    public void fillPalletTypeField(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        deliveredProductDetailsHooks.fillPalletTypeField(view);
    }

    public void fillOrderedQuantities(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        deliveredProductDetailsHooks.fillOrderedQuantities(view);
    }

    public void calculatePriceFromTotalPrice(final ViewDefinitionState view, final ComponentState state,
                                             final String[] args) {
        deliveriesService.recalculatePriceFromTotalPrice(view, DeliveredProductFields.DELIVERED_QUANTITY);
    }

    public void calculatePriceFromPricePerUnit(final ViewDefinitionState view, final ComponentState state,
                                               final String[] args) {
        deliveriesService.recalculatePriceFromPricePerUnit(view, DeliveredProductFields.DELIVERED_QUANTITY);
    }

    public void calculatePrice(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        deliveriesService.recalculatePrice(view, DeliveredProductFields.DELIVERED_QUANTITY);
    }

    public void quantityChange(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        FormComponent deliveredProductForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity deliveredProduct = deliveredProductForm.getEntity();
        Entity product = deliveredProduct.getBelongsToField(DeliveredProductFields.PRODUCT);

        if (decimalFieldsInvalid(deliveredProductForm) || Objects.isNull(product)) {
            return;
        }

        BigDecimal deliveredQuantity = deliveredProduct.getDecimalField(DeliveredProductFields.DELIVERED_QUANTITY);

        if (Objects.nonNull(deliveredQuantity)) {
            String unit = product.getStringField(ProductFields.UNIT);
            String additionalUnit = Optional.ofNullable(product.getStringField(ProductFields.ADDITIONAL_UNIT))
                    .orElse(unit);

            BigDecimal conversion = deliveriesService.getConversion(product, unit, additionalUnit, deliveredProduct.getDecimalField(DeliveredProductFields.CONVERSION));
            BigDecimal newAdditionalQuantity = calculationQuantityService.calculateAdditionalQuantity(deliveredQuantity,
                    conversion, additionalUnit);

            FieldComponent additionalQuantityField = (FieldComponent) view
                    .getComponentByReference(DeliveredProductFields.ADDITIONAL_QUANTITY);

            additionalQuantityField.setFieldValue(numberService.formatWithMinimumFractionDigits(newAdditionalQuantity, 0));
            additionalQuantityField.requestComponentUpdateState();
        }
    }

    private boolean decimalFieldsInvalid(final FormComponent formComponent) {
        String[] fieldNames = {DeliveredProductFields.ADDITIONAL_QUANTITY, DeliveredProductFields.CONVERSION,
                DeliveredProductFields.DELIVERED_QUANTITY};

        boolean valid = false;

        Entity entity = formComponent.getEntity();

        for (String fieldName : fieldNames) {
            try {
                entity.getDecimalField(fieldName);
            } catch (IllegalArgumentException e) {
                formComponent.findFieldComponentByName(fieldName)
                        .addMessage("sleektivView.validate.field.error.invalidNumericFormat", MessageType.FAILURE);

                valid = true;
            }
        }

        return valid;
    }

    public void additionalQuantityChange(final ViewDefinitionState view, final ComponentState state,
                                         final String[] args) {
        FormComponent deliveredProductForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity deliveredProduct = deliveredProductForm.getEntity();
        Entity product = deliveredProduct.getBelongsToField(DeliveredProductFields.PRODUCT);

        if (decimalFieldsInvalid(deliveredProductForm) || Objects.isNull(product)) {
            return;
        }

        BigDecimal additionalQuantity = deliveredProduct.getDecimalField(DeliveredProductFields.ADDITIONAL_QUANTITY);

        if (Objects.nonNull(additionalQuantity)) {
            String unit = product.getStringField(ProductFields.UNIT);
            String additionalUnit = Optional.ofNullable(product.getStringField(ProductFields.ADDITIONAL_UNIT))
                    .orElse(unit);

            BigDecimal conversion = deliveriesService.getConversion(product, unit, additionalUnit, deliveredProduct.getDecimalField(DeliveredProductFields.CONVERSION));
            BigDecimal newDeliveredQuantity = calculationQuantityService.calculateQuantity(additionalQuantity,
                    conversion, unit);

            FieldComponent deliveredQuantityField = (FieldComponent) view
                    .getComponentByReference(DeliveredProductFields.DELIVERED_QUANTITY);

            deliveredQuantityField.setFieldValue(numberService.formatWithMinimumFractionDigits(newDeliveredQuantity, 0));
            deliveredQuantityField.requestComponentUpdateState();
        }
    }

    public void setBatchLookupProductFilterValue(final ViewDefinitionState view, final ComponentState state,
                                                 final String[] args) {
        FormComponent deliveredProductForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Entity deliveredProduct = deliveredProductForm.getPersistedEntityWithIncludedFormValues();

        deliveredProductDetailsHooks.setBatchLookupProductFilterValue(view, deliveredProduct);
    }

    public void setStorageLocationLookup(final ViewDefinitionState view) {
        FormComponent deliveredProductForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        LookupComponent productLookup = (LookupComponent) view.getComponentByReference(DeliveredProductFields.PRODUCT);
        LookupComponent storageLocationLookup = (LookupComponent) view
                .getComponentByReference(DeliveredProductFields.STORAGE_LOCATION);

        Entity deliveredProduct = deliveredProductForm.getEntity();
        Entity delivery = deliveredProduct.getBelongsToField(DeliveredProductFields.DELIVERY);
        Entity location = delivery.getBelongsToField(DeliveryFields.LOCATION);
        Entity product = productLookup.getEntity();

        Long storageLocationId = null;

        FilterValueHolder filterValueHolder = storageLocationLookup.getFilterValue();

        boolean isProductSet = Objects.nonNull(product);

        if (isProductSet) {
            filterValueHolder.put(DeliveredProductFields.PRODUCT, product.getId());

            if (Objects.nonNull(location)) {
                Optional<Entity> mayBeStorageLocation = materialFlowResourcesService.findStorageLocationForProduct(location, product);

                if (mayBeStorageLocation.isPresent()) {
                    Entity storageLocation = mayBeStorageLocation.get();

                    storageLocationId = storageLocation.getId();
                }
            }
        } else {
            filterValueHolder.remove(DeliveredProductFields.PRODUCT);
        }

        storageLocationLookup.setFilterValue(filterValueHolder);
        storageLocationLookup.setFieldValue(storageLocationId);
        storageLocationLookup.setEnabled(isProductSet);
        storageLocationLookup.requestComponentUpdateState();
    }

}
