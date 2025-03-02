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
package com.sleektiv.mes.cmmsMachineParts.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.cmmsMachineParts.states.MaintenanceEventStateChangeViewClient;
import com.sleektiv.mes.cmmsMachineParts.states.aop.MaintenanceEventStateChangeAspect;
import com.sleektiv.mes.cmmsMachineParts.states.constants.MaintenanceEventStateChangeFields;
import com.sleektiv.mes.cmmsMachineParts.validators.MaintenanceEventStateChangeValidators;
import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.mes.states.constants.StateChangeStatus;
import com.sleektiv.mes.states.service.StateChangeContextBuilder;
import com.sleektiv.mes.states.service.client.util.ViewContextHolder;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;

@Service
public class MaintenanceEventReasonViewListeners {

    @Autowired
    private MaintenanceEventStateChangeAspect maintenanceEventStateChangeAspect;

    @Autowired
    private StateChangeContextBuilder stateChangeContextBuilder;

    @Autowired
    private MaintenanceEventStateChangeViewClient maintenanceEventStateChangeViewClient;

    @Autowired
    private MaintenanceEventStateChangeValidators maintenanceEventStateChangeValidators;

    public void continueStateChange(final ViewDefinitionState view, final ComponentState component, final String[] args) {
        final FormComponent form = (FormComponent) component;
        Entity eventStateChange = form.getPersistedEntityWithIncludedFormValues();
        maintenanceEventStateChangeValidators.validate(eventStateChange.getDataDefinition(), eventStateChange);
        form.setEntity(eventStateChange);
        if (!eventStateChange.getErrors().isEmpty()) {
            return;
        }

        form.performEvent(view, "save");
        if (!form.isValid()) {
            return;
        }

        final Entity stateChangeEntity = ((FormComponent) form).getEntity();
        final StateChangeContext stateContext = stateChangeContextBuilder.build(
                maintenanceEventStateChangeAspect.getChangeEntityDescriber(), stateChangeEntity);

        stateContext.setStatus(StateChangeStatus.IN_PROGRESS);
        maintenanceEventStateChangeAspect.changeState(stateContext);

        maintenanceEventStateChangeViewClient.showMessages(new ViewContextHolder(view, form), stateContext);
    }

    public void cancelStateChange(final ViewDefinitionState view, final ComponentState form, final String[] args) {
        final Entity stateChangeEntity = ((FormComponent) form).getEntity();

        final StateChangeContext stateContext = stateChangeContextBuilder.build(
                maintenanceEventStateChangeAspect.getChangeEntityDescriber(), stateChangeEntity);
        stateContext.setStatus(StateChangeStatus.CANCELED);
        stateContext.save();

        maintenanceEventStateChangeViewClient.showMessages(new ViewContextHolder(view, form), stateContext);
    }

    public void beforeRenderDialog(final ViewDefinitionState view) {
        final FieldComponent commentField = (FieldComponent) view.getComponentByReference(MaintenanceEventStateChangeFields.COMMENT);
        commentField.setRequired(true);

        final FieldComponent commentRequiredField = (FieldComponent) view.getComponentByReference(MaintenanceEventStateChangeFields.COMMENT_REQUIRED);
        commentRequiredField.setFieldValue(true);
    }

}
