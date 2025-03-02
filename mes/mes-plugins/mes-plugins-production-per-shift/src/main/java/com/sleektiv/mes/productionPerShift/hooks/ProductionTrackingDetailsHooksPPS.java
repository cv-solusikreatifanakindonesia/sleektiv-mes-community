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
package com.sleektiv.mes.productionPerShift.hooks;

import com.sleektiv.mes.lineChangeoverNorms.constants.LineChangeoverNormsFields;
import com.sleektiv.mes.lineChangeoverNormsForOrders.LineChangeoverNormsForOrdersService;
import com.sleektiv.mes.lineChangeoverNormsForOrders.constants.OrderFieldsLCNFO;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.productionCounting.constants.ProductionTrackingFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.Ribbon;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ProductionTrackingDetailsHooksPPS {

    private static final String L_CHANGEOVER = "changeover";

    private static final String L_SHOW_CHANGEOVER = "showChangeover";

    private static final String L_CHANGEOVER_DURATION = "changeoverDuration";

    @Autowired
    private LineChangeoverNormsForOrdersService lineChangeoverNormsForOrdersService;

    public void onBeforeRender(final ViewDefinitionState view) {
        updateButtonsState(view);
        setChangeoverTime(view);
    }

    public void updateButtonsState(final ViewDefinitionState view) {
        FormComponent productionTrackingForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
        Ribbon ribbon = window.getRibbon();

        RibbonGroup changeoverRibbonGroup = ribbon.getGroupByName(L_CHANGEOVER);

        RibbonActionItem showChangeoverRibbonActionItem = changeoverRibbonGroup.getItemByName(L_SHOW_CHANGEOVER);

        Long productionTrackingId = productionTrackingForm.getEntityId();

        boolean isSaved = Objects.nonNull(productionTrackingId);

        showChangeoverRibbonActionItem.setEnabled(isSaved);
        showChangeoverRibbonActionItem.requestUpdate(true);
    }

    private void setChangeoverTime(final ViewDefinitionState view) {
        FormComponent productionTrackingForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        FieldComponent changeoverTimeField = (FieldComponent) view.getComponentByReference(L_CHANGEOVER_DURATION);

        Long productionTrackingId = productionTrackingForm.getEntityId();

        Integer duration = null;

        if (Objects.nonNull(productionTrackingId)) {
            Entity productionTracking = productionTrackingForm.getEntity();

            Entity order = productionTracking.getBelongsToField(ProductionTrackingFields.ORDER);

            if (Objects.nonNull(order)) {
                Entity previousOrder = lineChangeoverNormsForOrdersService.getPreviousOrderFromDB(order);

                if (Objects.nonNull(previousOrder)) {
                    Entity technology = order.getBelongsToField(OrderFields.TECHNOLOGY);
                    Entity productionLine = order.getBelongsToField(OrderFields.PRODUCTION_LINE);

                    Entity changeOver = lineChangeoverNormsForOrdersService.getChangeover(previousOrder, technology, productionLine);

                    if (Objects.nonNull(changeOver)) {
                        duration = changeOver.getIntegerField(LineChangeoverNormsFields.DURATION);

                        if (order.getBooleanField(OrderFieldsLCNFO.OWN_LINE_CHANGEOVER)) {
                            duration = order.getIntegerField(OrderFieldsLCNFO.OWN_LINE_CHANGEOVER_DURATION);
                        }
                    }
                }
            }
        }

        changeoverTimeField.setFieldValue(duration);
        changeoverTimeField.requestComponentUpdateState();
    }

}
