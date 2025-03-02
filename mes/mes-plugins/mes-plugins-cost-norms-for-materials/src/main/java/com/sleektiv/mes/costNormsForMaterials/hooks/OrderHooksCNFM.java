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
package com.sleektiv.mes.costNormsForMaterials.hooks;

import com.sleektiv.mes.costNormsForMaterials.constants.OrderFieldsCNFM;
import com.sleektiv.mes.costNormsForMaterials.orderRawMaterialCosts.OrderMaterialsCostDataGenerator;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderHooksCNFM {

    @Autowired
    private OrderMaterialsCostDataGenerator orderMaterialsCostDataGenerator;

    public void fillOrderOperationProductsInComponents(final DataDefinition orderDD, final Entity order) {
        Entity technology = order.getBelongsToField(OrderFields.TECHNOLOGY);
        if (technology != null) {
            boolean shouldUpdate;
            if (order.getId() != null) {
                Entity orderFromDb = orderDD.get(order.getId());
                shouldUpdate = orderFromDb.getBelongsToField(OrderFields.TECHNOLOGY) == null;
                if(orderFromDb.getHasManyField(OrderFieldsCNFM.TECHNOLOGY_INST_OPER_PRODUCT_IN_COMPS).isEmpty()) {
                    shouldUpdate = true;
                }
            } else {
                shouldUpdate = false;
            }
            if (shouldUpdate) {
                List<Entity> orderMaterialsCosts = orderMaterialsCostDataGenerator.generateUpdatedMaterialsListFor(order);
                order.setField(OrderFieldsCNFM.TECHNOLOGY_INST_OPER_PRODUCT_IN_COMPS, orderMaterialsCosts);
            }
        }
    }
}
