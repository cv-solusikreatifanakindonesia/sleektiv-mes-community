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
package com.sleektiv.mes.technologies.hooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.BasicConstants;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class TechnologyGroupDetailsViewHooks {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void addTechnologyGroupToProduct(final ViewDefinitionState view, final ComponentState componentState,
            final String[] args) {
        FormComponent technologyGroupForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity technologyGroup = technologyGroupForm.getEntity();

        if (technologyGroup.getId() == null) {
            return;
        }

        FormComponent productForm = (FormComponent) view.getComponentByReference("product");
        Entity product = productForm.getEntity();

        if (product.getId() == null) {
            return;
        }

        product = getProductFromDB(product.getId());

        product.setField("technologyGroup", technologyGroup);
        product.getDataDefinition().save(product);
    }

    private Entity getProductFromDB(final Long productId) {
        return dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_PRODUCT).get(productId);
    }
}
