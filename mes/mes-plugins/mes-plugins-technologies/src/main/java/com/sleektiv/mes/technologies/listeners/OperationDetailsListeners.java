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
package com.sleektiv.mes.technologies.listeners;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.sleektiv.mes.technologies.constants.AssignedToOperation;
import com.sleektiv.mes.technologies.constants.OperationFields;
import com.sleektiv.mes.technologies.hooks.OperationDetailsHooks;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class OperationDetailsListeners {

    @Autowired
    private OperationDetailsHooks operationDetailsHooks;

    public void setWorkstationsTabFields(final ViewDefinitionState view, final ComponentState componentState,
            final String[] args) {
        operationDetailsHooks.setWorkstationsTabFields(view);

        FieldComponent assignedToOperation = (FieldComponent) view.getComponentByReference(OperationFields.ASSIGNED_TO_OPERATION);
        String assignedToOperationValue = (String) assignedToOperation.getFieldValue();

        if (AssignedToOperation.WORKSTATIONS.getStringValue().equals(assignedToOperationValue)) {
            operationDetailsHooks.clearLookupField(view, OperationFields.WORKSTATION_TYPE);
            operationDetailsHooks.clearLookupField(view, OperationFields.DIVISION);
        } else if (AssignedToOperation.WORKSTATIONS_TYPE.getStringValue().equals(assignedToOperationValue)) {
            operationDetailsHooks.clearWorkstationsField(view);
            operationDetailsHooks.clearLookupField(view, OperationFields.DIVISION);
        }
    }

    public void addUpTheNumberOfWorkstations(final ViewDefinitionState view, final ComponentState componentState,
            final String[] args) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        int size = form.getPersistedEntityWithIncludedFormValues().getHasManyField(OperationFields.WORKSTATIONS).size();

        FieldComponent quantityOfWorkstations = (FieldComponent) view
                .getComponentByReference(OperationFields.QUANTITY_OF_WORKSTATIONS);

        quantityOfWorkstations.setFieldValue(size);
        quantityOfWorkstations.requestComponentUpdateState();
    }

    public final void showTechnologiesWithUsingOperation(final ViewDefinitionState view, final ComponentState state,
            final String[] args) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity operation = form.getEntity();

        if (operation.getId() == null) {
            return;
        }

        Map<String, Object> parameters = Maps.newHashMap();

        parameters.put("form.id", operation.getId());

        String url = "../page/technologies/technologiesWithUsingOperationList.html";
        view.redirectTo(url, false, true, parameters);
    }

}
