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
package com.sleektiv.mes.techSubcontracting.hooks;

import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.techSubcontracting.constants.OrderExternalServiceCostFields;
import com.sleektiv.mes.techSubcontracting.criteriaModifiers.TechnologyOperationComponentCriteriaModifiersTS;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class OrderExternalServiceCostDetailsHooks {

    public void onBeforeRender(final ViewDefinitionState view) {
        setFormEnabled(view);
        setTechnologyOperationComponentLookup(view);
        setQuantityUnit(view);
    }

    private void setFormEnabled(final ViewDefinitionState view) {
        FormComponent orderExternalServiceCostForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Entity orderExternalServiceCost = orderExternalServiceCostForm.getPersistedEntityWithIncludedFormValues();

        if (Objects.nonNull(orderExternalServiceCost)) {
            boolean isSaved = Objects.nonNull(orderExternalServiceCost.getId());
            boolean isAddedManually = orderExternalServiceCost.getBooleanField(OrderExternalServiceCostFields.IS_ADDED_MANUALLY);

            orderExternalServiceCostForm.setFormEnabled(!isSaved || isAddedManually);
        }
    }

    private void setTechnologyOperationComponentLookup(final ViewDefinitionState view) {
        FormComponent orderExternalServiceCostForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        LookupComponent technologyOperationComponentLookup = (LookupComponent) view.getComponentByReference(OrderExternalServiceCostFields.TECHNOLOGY_OPERATION_COMPONENT);

        Entity orderExternalServiceCost = orderExternalServiceCostForm.getEntity();

        if (Objects.nonNull(orderExternalServiceCost)) {
            Entity order = orderExternalServiceCost.getBelongsToField(OrderExternalServiceCostFields.ORDER);

            if (Objects.nonNull(order)) {
                FilterValueHolder filterValueHolder = technologyOperationComponentLookup.getFilterValue();

                Entity technology = order.getBelongsToField(OrderFields.TECHNOLOGY);

                if (Objects.nonNull(technology)) {
                    filterValueHolder.put(TechnologyOperationComponentCriteriaModifiersTS.L_TECHNOLOGY, technology.getId());
                } else {
                    if (filterValueHolder.has(TechnologyOperationComponentCriteriaModifiersTS.L_TECHNOLOGY)) {
                        filterValueHolder.remove(TechnologyOperationComponentCriteriaModifiersTS.L_TECHNOLOGY);
                    }
                }

                technologyOperationComponentLookup.setFilterValue(filterValueHolder);
            }
        }
    }

    private void setQuantityUnit(final ViewDefinitionState view) {
        LookupComponent productLookup = (LookupComponent) view.getComponentByReference(OrderExternalServiceCostFields.PRODUCT);
        FieldComponent quantityUnitField = (FieldComponent) view.getComponentByReference(OrderExternalServiceCostFields.QUANTITY_UNIT);

        Entity product = productLookup.getEntity();

        String unit = null;

        if (Objects.nonNull(product)) {
            unit = product.getStringField(ProductFields.UNIT);
        }

        quantityUnitField.setFieldValue(unit);
        quantityUnitField.requestComponentUpdateState();

    }

}
