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

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.orders.OrderTechnologicalProcessWasteService;
import com.sleektiv.mes.orders.constants.OrderTechnologicalProcessWasteFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class OrderTechnologicalProcessWasteDetailsHooks {

    @Autowired
    private OrderTechnologicalProcessWasteService orderTechnologicalProcessWasteService;

    public final void onBeforeRender(final ViewDefinitionState view) {
        FormComponent orderTechnologicalProcessWasteForm = (FormComponent) view
                .getComponentByReference(SleektivViewConstants.L_FORM);

        Entity orderTechnologicalProcessWaste = orderTechnologicalProcessWasteForm.getEntity();
        Long orderTechnologicalProcessWasteId = orderTechnologicalProcessWaste.getId();

        if (Objects.nonNull(orderTechnologicalProcessWasteId)) {
            orderTechnologicalProcessWaste = orderTechnologicalProcessWaste.getDataDefinition()
                    .get(orderTechnologicalProcessWasteId);
        }

        Entity orderTechnologicalProcess = getOrderTechnologicalProcess(view, orderTechnologicalProcessWaste);

        orderTechnologicalProcessWasteService.setFormEnabled(orderTechnologicalProcessWasteForm, orderTechnologicalProcessWaste, orderTechnologicalProcess);

        orderTechnologicalProcessWasteService.fillOrderPack(view, orderTechnologicalProcess);
        orderTechnologicalProcessWasteService.fillTechnologicalProcessName(view, orderTechnologicalProcess);
        orderTechnologicalProcessWasteService.fillUnit(view, orderTechnologicalProcess);
        orderTechnologicalProcessWasteService.fillDateAndWorker(view);
    }

    private Entity getOrderTechnologicalProcess(final ViewDefinitionState view, final Entity orderTechnologicalProcessWaste) {
        Entity orderTechnologicalProcess;

        if (Objects.nonNull(orderTechnologicalProcessWaste.getId())) {
            orderTechnologicalProcess = orderTechnologicalProcessWaste
                    .getBelongsToField(OrderTechnologicalProcessWasteFields.ORDER_TECHNOLOGICAL_PROCESS);
        } else {
            LookupComponent orderTechnologicalProcessLookup = (LookupComponent) view
                    .getComponentByReference(OrderTechnologicalProcessWasteFields.ORDER_TECHNOLOGICAL_PROCESS);

            orderTechnologicalProcess = orderTechnologicalProcessLookup.getEntity();
        }

        return orderTechnologicalProcess;
    }

}
