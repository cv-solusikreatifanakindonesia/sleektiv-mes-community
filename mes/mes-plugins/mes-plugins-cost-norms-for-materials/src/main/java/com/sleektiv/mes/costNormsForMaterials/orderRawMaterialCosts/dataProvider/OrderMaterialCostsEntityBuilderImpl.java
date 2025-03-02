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
package com.sleektiv.mes.costNormsForMaterials.orderRawMaterialCosts.dataProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.costNormsForMaterials.constants.CostNormsForMaterialsConstants;
import com.sleektiv.mes.costNormsForMaterials.constants.TechnologyInstOperProductInCompFields;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;

@Service
final class OrderMaterialCostsEntityBuilderImpl implements OrderMaterialCostsEntityBuilder {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Override
    public Entity create(Entity order, Entity product) {
        Entity orderMaterialCosts = dataDefinitionService.get(CostNormsForMaterialsConstants.PLUGIN_IDENTIFIER,
                CostNormsForMaterialsConstants.MODEL_TECHNOLOGY_INST_OPER_PRODUCT_IN_COMP).create();
        orderMaterialCosts.setField(TechnologyInstOperProductInCompFields.ORDER, order);
        orderMaterialCosts.setField(TechnologyInstOperProductInCompFields.PRODUCT, product);
        return orderMaterialCosts;
    }

}
