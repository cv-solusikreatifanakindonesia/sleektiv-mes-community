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
package com.sleektiv.mes.technologies.hooks;

import com.sleektiv.mes.technologies.constants.ProductDataFields;
import com.sleektiv.mes.technologies.constants.ProductDataInputFields;
import com.sleektiv.mes.technologies.criteriaModifiers.ProductDataCriteriaModifiers;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductDataInputDetailsHooks {

    public void onBeforeRender(final ViewDefinitionState view) {
        setCriteriaModifierParameters(view);
    }

    public void setCriteriaModifierParameters(final ViewDefinitionState view) {
        FormComponent productDataInputForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        GridComponent operationProductInComponentDtosGrid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

        Entity productDataInput = productDataInputForm.getEntity();

        Entity productData = productDataInput.getBelongsToField(ProductDataInputFields.PRODUCT_DATA);
        Entity technology = productData.getBelongsToField(ProductDataFields.TECHNOLOGY);
        List<Entity> productDataInputs = productData.getHasManyField(ProductDataFields.PRODUCT_DATA_INPUTS);

        List<Long> operationProductDataInComponents = getOperationProductDataInComponents(productDataInputs);

        FilterValueHolder filterValueHolder = operationProductInComponentDtosGrid.getFilterValue();

        if (Objects.isNull(technology)) {
            filterValueHolder.remove(ProductDataCriteriaModifiers.TECHNOLOGY_ID);
        } else {
            filterValueHolder.put(ProductDataCriteriaModifiers.TECHNOLOGY_ID, technology.getId());
        }
        if (operationProductDataInComponents.isEmpty()) {
            filterValueHolder.remove(ProductDataCriteriaModifiers.OPERATION_PRODUCT_IN_COMPONENT_IDS);
        } else {
            filterValueHolder.put(ProductDataCriteriaModifiers.OPERATION_PRODUCT_IN_COMPONENT_IDS, operationProductDataInComponents);
        }

        operationProductInComponentDtosGrid.setFilterValue(filterValueHolder);
    }

    private List<Long> getOperationProductDataInComponents(final List<Entity> productDataInputs) {
        return productDataInputs.stream().map(productDataInput -> productDataInput.getBelongsToField(ProductDataInputFields.OPERATION_PRODUCT_IN_COMPONENT).getId()).collect(Collectors.toList());
    }

}
