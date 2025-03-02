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
package com.sleektiv.mes.cmmsMachineParts.hooks;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.SubassemblyFields;
import com.sleektiv.mes.basic.constants.WorkstationFields;
import com.sleektiv.mes.cmmsMachineParts.constants.CmmsMachinePartsConstants;
import com.sleektiv.mes.cmmsMachineParts.constants.PlannedEventFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class AddActionsForPlannedEventHooks {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void onBeforeRender(final ViewDefinitionState view) throws JSONException {
        setCriteriaModifiers(view);
    }

    private void setCriteriaModifiers(final ViewDefinitionState view) throws JSONException {
        Long plannedEventId = Long.valueOf(view.getJsonContext().get("window.mainTab.plannedEvent").toString());

        if (plannedEventId != null) {
            Entity event = getPlannedEventDD().get(plannedEventId);
            Entity workstation = event.getBelongsToField(PlannedEventFields.WORKSTATION);
            Entity subassembly = event.getBelongsToField(PlannedEventFields.SUBASSEMBLY);
            if (workstation != null) {

                GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

                FilterValueHolder filter = grid.getFilterValue();
                filter.put(PlannedEventFields.WORKSTATION, workstation.getId());

                if (subassembly != null) {
                    Entity workstationType = subassembly.getBelongsToField(SubassemblyFields.WORKSTATION_TYPE);
                    filter.put(PlannedEventFields.SUBASSEMBLY, subassembly.getId());
                    filter.put(WorkstationFields.WORKSTATION_TYPE, workstationType.getId());
                } else {
                    Entity workstationType = workstation.getBelongsToField(WorkstationFields.WORKSTATION_TYPE);
                    filter.put(WorkstationFields.WORKSTATION_TYPE, workstationType.getId());
                }
                grid.setFilterValue(filter);
            }
        }
    }

    private DataDefinition getPlannedEventDD() {
        return dataDefinitionService.get(CmmsMachinePartsConstants.PLUGIN_IDENTIFIER, CmmsMachinePartsConstants.MODEL_PLANNED_EVENT);
    }

}
