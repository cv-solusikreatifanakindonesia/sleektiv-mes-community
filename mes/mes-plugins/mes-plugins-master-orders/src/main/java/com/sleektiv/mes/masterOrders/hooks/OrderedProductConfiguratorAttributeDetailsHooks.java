/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv Framework
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
package com.sleektiv.mes.masterOrders.hooks;

import com.sleektiv.mes.masterOrders.constants.OrderedProductConfiguratorAttributeFields;
import com.sleektiv.mes.masterOrders.constants.OrderedProductConfiguratorFields;
import com.sleektiv.mes.masterOrders.criteriaModifier.OrderedProductConfiguratorCriteriaModifiers;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderedProductConfiguratorAttributeDetailsHooks {

    public void onBeforeRender(final ViewDefinitionState view) {
        setCriteriaModifierParameters(view);
    }

    public void setCriteriaModifierParameters(final ViewDefinitionState view) {
        FormComponent orderedProductConfiguratorAttributeForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        GridComponent attributesGrid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

        Entity orderedProductConfiguratorAttribute = orderedProductConfiguratorAttributeForm.getPersistedEntityWithIncludedFormValues();

        Entity orderedProductConfigurator = orderedProductConfiguratorAttribute.getBelongsToField(OrderedProductConfiguratorAttributeFields.ORDERED_PRODUCT_CONFIGURATOR);

        List<Entity> orderedProductConfiguratorAttributes = orderedProductConfigurator.getHasManyField(OrderedProductConfiguratorFields.ORDERED_PRODUCT_CONFIGURATOR_ATTRIBUTES);

        List<Long> attributeIds = getAttributesIds(orderedProductConfiguratorAttributes);

        FilterValueHolder filterValueHolder = attributesGrid.getFilterValue();

        if (attributeIds.isEmpty()) {
            filterValueHolder.remove(OrderedProductConfiguratorCriteriaModifiers.ATTRIBUTE_IDS);
        } else {
            filterValueHolder.put(OrderedProductConfiguratorCriteriaModifiers.ATTRIBUTE_IDS, attributeIds);
        }

        attributesGrid.setFilterValue(filterValueHolder);
    }

    private List<Long> getAttributesIds(final List<Entity> orderedProductConfiguratorAttributes) {
        return orderedProductConfiguratorAttributes.stream().map(orderedProductConfiguratorAttribute ->
                orderedProductConfiguratorAttribute.getBelongsToField(OrderedProductConfiguratorAttributeFields.ATTRIBUTE).getId()).collect(Collectors.toList());
    }

}
