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
package com.sleektiv.mes.productionCounting.hooks;

import org.springframework.stereotype.Service;

import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.Ribbon;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class ProductDetailsHooksPC {

    private static final String L_SHOW_PRODUCTION_TRACKINGS = "showProductionTrackings";

    private static final String L_SHOW_PRODUCTION_TRACKINGS_FOR_PRODUCT = "showProductionTrackingsForProduct";

    private static final String L_SHOW_PRODUCTION_TRACKINGS_FOR_PRODUCT_GROUPED = "showProductionTrackingsForProductGrouped";

    public void updateButtonsState(final ViewDefinitionState view) {
        FormComponent productForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
        Ribbon ribbon = window.getRibbon();

        RibbonGroup showProductionTrackingsRibbonGroup = ribbon.getGroupByName(L_SHOW_PRODUCTION_TRACKINGS);

        RibbonActionItem showProductionTrackingsForProductRibbonActionItem = showProductionTrackingsRibbonGroup
                .getItemByName(L_SHOW_PRODUCTION_TRACKINGS_FOR_PRODUCT);
        RibbonActionItem showProductionTrackingsForProductGroupedRibbonActionItem = showProductionTrackingsRibbonGroup
                .getItemByName(L_SHOW_PRODUCTION_TRACKINGS_FOR_PRODUCT_GROUPED);

        Long productionTrackingId = productForm.getEntityId();

        boolean isSaved = (productionTrackingId != null);

        showProductionTrackingsForProductRibbonActionItem.setEnabled(isSaved);
        showProductionTrackingsForProductGroupedRibbonActionItem.setEnabled(isSaved);

        showProductionTrackingsForProductRibbonActionItem.requestUpdate(true);
        showProductionTrackingsForProductGroupedRibbonActionItem.requestUpdate(true);
    }

}
