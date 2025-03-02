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
package com.sleektiv.mes.techSubcontrForOperTasks.aop;

import com.sleektiv.mes.orders.OperationalTasksService;
import com.sleektiv.mes.orders.constants.OperationalTaskFields;
import com.sleektiv.mes.techSubcontracting.constants.TechnologyInstanceOperCompFieldsTS;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperationalTaskHooksOTFOOverrideUtil {

    @Autowired
    private OperationalTasksService operationalTasksService;

    public void onSaveForSubcontracted(final DataDefinition operationalTaskDD, final Entity operationalTask) {
        String type = operationalTask.getStringField(OperationalTaskFields.TYPE);

        if (operationalTasksService.isOperationalTaskTypeExecutionOperationInOrder(type)) {
            Entity order = operationalTask.getBelongsToField(OperationalTaskFields.ORDER);

            Entity technologyOperationComponent = operationalTask
                    .getBelongsToField(OperationalTaskFields.TECHNOLOGY_OPERATION_COMPONENT);

            if ((order == null) || (technologyOperationComponent == null) || isSubcontracting(technologyOperationComponent)) {
                operationalTask.setField(OperationalTaskFields.DESCRIPTION, null);
            }
        }
    }

    private boolean isSubcontracting(final Entity technologyOperationComponent) {
        return ((technologyOperationComponent != null)
                && technologyOperationComponent.getBooleanField(TechnologyInstanceOperCompFieldsTS.IS_SUBCONTRACTING));
    }

}
