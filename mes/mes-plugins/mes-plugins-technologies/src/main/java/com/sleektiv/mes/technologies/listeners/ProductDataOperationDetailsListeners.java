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
package com.sleektiv.mes.technologies.listeners;

import com.sleektiv.mes.technologies.constants.ProductDataOperationFields;
import com.sleektiv.mes.technologies.constants.TechnologiesConstants;
import com.sleektiv.mes.technologies.services.ProductDataService;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductDataOperationDetailsListeners {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private ProductDataService productDataService;

    public void createProductDataOperations(final ViewDefinitionState view, final ComponentState state, final String args[]) {
        FormComponent productDataOperationForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        GridComponent technologyOperationComponentDtosGrid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

        Entity productDataOperation = productDataOperationForm.getEntity();
        List<Entity> technologyOperationComponentDtos = technologyOperationComponentDtosGrid.getSelectedEntities();

        Entity productData = productDataOperation.getBelongsToField(ProductDataOperationFields.PRODUCT_DATA);
        List<Entity> technologyOperationComponents = getTechnologyOperationComponents(technologyOperationComponentDtos);

        productDataService.createProductDataOperations(productData, technologyOperationComponents);
    }

    private List<Entity> getTechnologyOperationComponents(final List<Entity> technologyOperationComponentDtos) {
        List<Long> ids = technologyOperationComponentDtos.stream().map(Entity::getId).collect(Collectors.toList());

        return getTechnologyOperationComponentDD().find().add(SearchRestrictions.in("id", ids)).list().getEntities();
    }

    private DataDefinition getTechnologyOperationComponentDD() {
        return dataDefinitionService.get(TechnologiesConstants.PLUGIN_IDENTIFIER, TechnologiesConstants.MODEL_TECHNOLOGY_OPERATION_COMPONENT);
    }

}
