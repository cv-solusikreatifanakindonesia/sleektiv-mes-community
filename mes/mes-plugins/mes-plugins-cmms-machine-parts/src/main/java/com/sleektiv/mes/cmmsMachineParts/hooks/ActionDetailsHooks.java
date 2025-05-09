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

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.sleektiv.mes.cmmsMachineParts.constants.ActionAppliesTo;
import com.sleektiv.mes.cmmsMachineParts.constants.ActionFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class ActionDetailsHooks {



    public void onBeforeRender(final ViewDefinitionState view) {

        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity action = form.getPersistedEntityWithIncludedFormValues();
        ActionAppliesTo appliesTo = ActionAppliesTo.from(action);
        toggleGridsEnable(view, appliesTo, false);
        if (action.getBooleanField(ActionFields.IS_DEFAULT)) {
            disableActionsWhenDefault(view);
        }
    }

    public void toggleGridsEnable(final ViewDefinitionState view, final ActionAppliesTo appliesTo, final boolean shouldClear) {
        GridComponent workstationsGrid = (GridComponent) view.getComponentByReference(ActionFields.WORKSTATIONS);
        GridComponent subassembliesGrid = (GridComponent) view.getComponentByReference(ActionFields.SUBASSEMBLIES);
        GridComponent workstationTypesGrid = (GridComponent) view.getComponentByReference(ActionFields.WORKSTATION_TYPES);

        if (appliesTo.compareTo(ActionAppliesTo.WORKSTATION_OR_SUBASSEMBLY) == 0) {
            workstationsGrid.setEnabled(true);
            subassembliesGrid.setEnabled(true);
            workstationTypesGrid.setEnabled(false);
            if (shouldClear) {
                workstationTypesGrid.setEntities(Lists.newArrayList());
            }
        } else if (appliesTo.compareTo(ActionAppliesTo.WORKSTATION_TYPE) == 0) {
            workstationsGrid.setEnabled(false);
            subassembliesGrid.setEnabled(false);
            workstationTypesGrid.setEnabled(true);
            if (shouldClear) {
                workstationsGrid.setEntities(Lists.newArrayList());
                subassembliesGrid.setEntities(Lists.newArrayList());
            }
        } else {
            workstationsGrid.setEnabled(false);
            subassembliesGrid.setEnabled(false);
            workstationTypesGrid.setEnabled(false);

            if (shouldClear) {
                workstationsGrid.setEntities(Lists.newArrayList());
                subassembliesGrid.setEntities(Lists.newArrayList());
                workstationTypesGrid.setEntities(Lists.newArrayList());
            }
        }
    }

    public void disableActionsWhenDefault(final ViewDefinitionState view) {
        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
        RibbonGroup actions = window.getRibbon().getGroupByName("actions");

        for (RibbonActionItem item : actions.getItems()) {
            item.setEnabled(false);
            item.requestUpdate(true);
        }

        GridComponent workstationsGrid = (GridComponent) view.getComponentByReference(ActionFields.WORKSTATIONS);
        GridComponent subassembliesGrid = (GridComponent) view.getComponentByReference(ActionFields.SUBASSEMBLIES);
        GridComponent workstationTypesGrid = (GridComponent) view.getComponentByReference(ActionFields.WORKSTATION_TYPES);

        workstationsGrid.setEnabled(false);
        subassembliesGrid.setEnabled(false);
        workstationTypesGrid.setEnabled(false);

        FieldComponent nameField = (FieldComponent) view.getComponentByReference(ActionFields.NAME);
        FieldComponent appliesToField = (FieldComponent) view.getComponentByReference(ActionFields.APPLIES_TO);

        nameField.setEnabled(false);
        appliesToField.setEnabled(false);
    }

}
