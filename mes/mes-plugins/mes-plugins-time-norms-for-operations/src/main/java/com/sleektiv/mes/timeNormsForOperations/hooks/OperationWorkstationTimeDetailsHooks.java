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
package com.sleektiv.mes.timeNormsForOperations.hooks;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.timeNormsForOperations.constants.OperationWorkstationTimeFields;
import com.sleektiv.mes.timeNormsForOperations.criteriaModifiers.WorkstationCriteriaModifiersTNFO;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class OperationWorkstationTimeDetailsHooks {

    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        LookupComponent workstationLookup = (LookupComponent) view
                .getComponentByReference(OperationWorkstationTimeFields.WORKSTATION);

        Entity operationWorkstationTime = form.getEntity();
        Entity operation = operationWorkstationTime.getBelongsToField(OperationWorkstationTimeFields.OPERATION);
        Entity workstation = operationWorkstationTime.getBelongsToField(OperationWorkstationTimeFields.WORKSTATION);

        filterWorkstationLookup(workstationLookup, operation, workstation);
    }

    private void filterWorkstationLookup(final LookupComponent workstationLookup, final Entity operation, Entity workstation) {
        FilterValueHolder filterValueHolder = workstationLookup.getFilterValue();

        Long operationId = operation.getId();

        filterValueHolder.put(WorkstationCriteriaModifiersTNFO.L_OPERATION_ID, operationId);

        if (Objects.isNull(workstation)) {
            filterValueHolder.remove(WorkstationCriteriaModifiersTNFO.L_WORKSTATION_ID);
        } else {
            filterValueHolder.put(WorkstationCriteriaModifiersTNFO.L_WORKSTATION_ID, workstation.getId());
        }

        workstationLookup.setFilterValue(filterValueHolder);
    }

}
