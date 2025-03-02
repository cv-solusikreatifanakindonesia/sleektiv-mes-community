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

import static com.sleektiv.mes.orders.constants.OrderFields.TECHNOLOGY;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.mes.orders.states.OrderStateService;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class OrderDetailsHooksCNFM {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private OrderStateService orderStateService;

    public void updateViewCostsButtonState(final ViewDefinitionState view) {
        FormComponent orderForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
        RibbonGroup materials = window.getRibbon().getGroupByName("materials");
        RibbonActionItem viewCosts = materials.getItemByName("viewCosts");

        Long orderId = orderForm.getEntityId();

        if (orderId != null) {
            Entity order = dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_ORDER).get(orderId);

            if ((order != null) && orderStateService.isSynchronized(order)) {
                Entity technology = order.getBelongsToField(TECHNOLOGY);

                if ((technology != null)) {
                    viewCosts.setEnabled(true);
                    viewCosts.requestUpdate(true);

                    return;
                }
            }
        }

        viewCosts.setEnabled(false);
        viewCosts.requestUpdate(true);
    }

}
