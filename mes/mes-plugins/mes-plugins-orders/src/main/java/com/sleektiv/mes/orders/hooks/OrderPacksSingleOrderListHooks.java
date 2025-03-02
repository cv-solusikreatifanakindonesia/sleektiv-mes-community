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
package com.sleektiv.mes.orders.hooks;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.mes.orders.states.constants.OrderState;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.Ribbon;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class OrderPacksSingleOrderListHooks {

    private static final String ACTIONS = "actions";

    public final void onBeforeRender(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(OrdersConstants.MODEL_ORDER);
        Entity order = form.getPersistedEntityWithIncludedFormValues();
        String orderState = order.getStringField(OrderFields.STATE);
        if (OrderState.COMPLETED.getStringValue().equals(orderState) || OrderState.DECLINED.getStringValue().equals(orderState)
                || OrderState.ABANDONED.getStringValue().equals(orderState)
                || OrderState.PENDING.getStringValue().equals(orderState)) {
            GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
            grid.setEditable(false);
            grid.setEnabled(false);
            WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
            Ribbon ribbon = window.getRibbon();
            RibbonActionItem generateOrderPacks = ribbon.getGroupByName("orderPacks").getItemByName("generateOrderPacks");
            RibbonActionItem actionsNew = ribbon.getGroupByName(ACTIONS).getItemByName("new");
            generateOrderPacks.setEnabled(false);
            generateOrderPacks.requestUpdate(true);
            actionsNew.setEnabled(false);
            actionsNew.requestUpdate(true);
        }
    }
}
