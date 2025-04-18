/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv MES
 * Version: 1.4
 * <p>
 * This file is part of Sleektiv.
 * <p>
 * Sleektiv is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.sleektiv.mes.technologies.hooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.MachineWorkingPeriodFields;
import com.sleektiv.mes.basic.constants.WorkstationFields;
import com.sleektiv.mes.basic.states.constants.WorkstationStateStringValues;
import com.sleektiv.mes.newstates.StateExecutorService;
import com.sleektiv.mes.technologies.states.WorkstationServiceMarker;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.security.api.SecurityService;

@Service
public class MachineWorkingPeriodHooksT {

    @Autowired
    private StateExecutorService stateExecutorService;

    @Autowired
    private SecurityService securityService;

    public void onSave(final DataDefinition dataDefinition, final Entity entity) {
        Long entityId = entity.getId();
        if (entityId != null) {
            Entity dbEntity = dataDefinition.get(entityId);
            if (dbEntity.getDateField(MachineWorkingPeriodFields.STOP_DATE) == null
                    && entity.getDateField(MachineWorkingPeriodFields.STOP_DATE) != null) {
                String userLogin = securityService.getCurrentUserName();
                Entity workstation = entity.getBelongsToField(MachineWorkingPeriodFields.WORKSTATION);
                workstation.setField(WorkstationFields.MANUAL_STATE_CHANGE, true);
                stateExecutorService.changeState(WorkstationServiceMarker.class, workstation, userLogin,
                        WorkstationStateStringValues.STOPPED);
            } else if (dbEntity.getDateField(MachineWorkingPeriodFields.STOP_DATE) != null
                    && entity.getDateField(MachineWorkingPeriodFields.STOP_DATE) == null) {
                String userLogin = securityService.getCurrentUserName();
                Entity workstation = entity.getBelongsToField(MachineWorkingPeriodFields.WORKSTATION);
                workstation.setField(WorkstationFields.MANUAL_STATE_CHANGE, true);
                stateExecutorService.changeState(WorkstationServiceMarker.class, workstation, userLogin,
                        WorkstationStateStringValues.LAUNCHED);
            }
        } else if (entity.getDateField(MachineWorkingPeriodFields.STOP_DATE) == null) {
            String userLogin = securityService.getCurrentUserName();
            Entity workstation = entity.getBelongsToField(MachineWorkingPeriodFields.WORKSTATION);
            workstation.setField(WorkstationFields.MANUAL_STATE_CHANGE, true);
            stateExecutorService.changeState(WorkstationServiceMarker.class, workstation, userLogin,
                    WorkstationStateStringValues.LAUNCHED);
        }
    }

    public boolean onDelete(final DataDefinition dataDefinition, final Entity entity) {
        if (entity.getDateField(MachineWorkingPeriodFields.STOP_DATE) == null) {
            String userLogin = securityService.getCurrentUserName();
            Entity workstation = entity.getBelongsToField(MachineWorkingPeriodFields.WORKSTATION);
            workstation.setField(WorkstationFields.MANUAL_STATE_CHANGE, true);
            stateExecutorService.changeState(WorkstationServiceMarker.class, workstation, userLogin,
                    WorkstationStateStringValues.STOPPED);
        }
        return true;
    }

}
