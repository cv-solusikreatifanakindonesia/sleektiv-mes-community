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

import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class ProductDetailsViewHooksO {

    

    public void updateRibbonState(final ViewDefinitionState view) {
        FormComponent productForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity product = productForm.getEntity();

        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
        RibbonGroup orders = (RibbonGroup) window.getRibbon().getGroupByName("orders");

        RibbonActionItem showOrdersWithProductMain = (RibbonActionItem) orders.getItemByName("showOrdersWithProductMain");
        RibbonActionItem showOrdersWithProductPlanned = (RibbonActionItem) orders.getItemByName("showOrdersWithProductPlanned");

        if (product.getId() != null) {
            updateButtonState(showOrdersWithProductMain, true);
            updateButtonState(showOrdersWithProductPlanned, true);

            return;
        }

        updateButtonState(showOrdersWithProductMain, false);
        updateButtonState(showOrdersWithProductPlanned, false);
    }

    private void updateButtonState(final RibbonActionItem ribbonActionItem, final boolean isEnabled) {
        ribbonActionItem.setEnabled(isEnabled);
        ribbonActionItem.requestUpdate(true);
    }

}
