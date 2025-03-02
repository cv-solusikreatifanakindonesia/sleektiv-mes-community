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
package com.sleektiv.mes.masterOrders.hooks;

import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.Ribbon;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

@Service
public class ProductsListHooksMO {

    private static final String L_ORDERS = "orders";

    private static final String L_OUTSOURCE_PROCESSING_COMPONENT = "outsourceProcessingComponent";

    public void onBeforeRender(final ViewDefinitionState view) {
        setRibbonEnabled(view);
    }

    private void setRibbonEnabled(final ViewDefinitionState view) {
        GridComponent productsGrid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
        Ribbon ribbon = window.getRibbon();

        RibbonGroup ordersGroup = ribbon.getGroupByName(L_ORDERS);

        RibbonActionItem outsourceProcessingComponentRibbonActionItem = ordersGroup.getItemByName(L_OUTSOURCE_PROCESSING_COMPONENT);

        outsourceProcessingComponentRibbonActionItem.setMessage("basic.productsList.window.ribbon.orders.outsourceProcessingComponent.message");
        boolean isOneProductSelected = productsGrid.getSelectedEntities().size() == 1;

        outsourceProcessingComponentRibbonActionItem.setEnabled(isOneProductSelected);
        outsourceProcessingComponentRibbonActionItem.requestUpdate(true);
    }

}
