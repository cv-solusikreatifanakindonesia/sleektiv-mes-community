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
package com.sleektiv.mes.masterOrders.listeners;

import com.google.common.collect.Maps;
import com.sleektiv.mes.masterOrders.constants.MasterOrderFields;
import com.sleektiv.mes.masterOrders.constants.MasterOrderState;
import com.sleektiv.mes.masterOrders.constants.MasterOrdersConstants;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MasterOrdersListListeners {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void changeState(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent gridComponent = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
        for (Long masterOrderId : gridComponent.getSelectedEntitiesIds()) {
            Entity masterOrderDB = getMasterOrderDD().get(masterOrderId);
            String status = args[0];

            if(status.equals(MasterOrderState.COMPLETED.getStringValue())) {
                masterOrderDB.setField(MasterOrderFields.STATE, MasterOrderState.COMPLETED.getStringValue());
                masterOrderDB.getDataDefinition().save(masterOrderDB);
            } else if(status.equals(MasterOrderState.DECLINED.getStringValue())){
                masterOrderDB.setField(MasterOrderFields.STATE, MasterOrderState.DECLINED.getStringValue());
                masterOrderDB.getDataDefinition().save(masterOrderDB);
            }
        }
    }

    public void setDeadline(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

        Set<Long> ids = grid.getSelectedEntitiesIds();

        if (ids.isEmpty()) {
            view.addMessage("masterOrders.masterOrder.notSelected", ComponentState.MessageType.INFO);
        } else {
            if (!state.isHasError()) {
                Map<String, Object> parameters = Maps.newHashMap();
                parameters.put("form.masterOrderIds", ids.stream().map(String::valueOf).collect(Collectors.joining(",")));

                JSONObject context = new JSONObject(parameters);
                StringBuilder url = new StringBuilder(MasterOrdersConstants.PLUGIN_IDENTIFIER + "/deadlineHelperDetails.html");
                url.append("?context=");
                url.append(context);

                view.openModal(url.toString());
            }
        }
    }

    private DataDefinition getMasterOrderDD() {
        return dataDefinitionService.get(MasterOrdersConstants.PLUGIN_IDENTIFIER, MasterOrdersConstants.MODEL_MASTER_ORDER);
    }
}
