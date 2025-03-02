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
package com.sleektiv.mes.basic.hooks;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.sleektiv.mes.basic.constants.FaultTypeAppliesTo;
import com.sleektiv.mes.basic.constants.FaultTypeFields;
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
public class FaultTypeDetailsHooks {



    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Entity faultType = form.getPersistedEntityWithIncludedFormValues();
        String appliesTo = faultType.getStringField(FaultTypeFields.APPLIES_TO);

        toggleGridsEnable(view, appliesTo, false);

        if (faultType.getBooleanField(FaultTypeFields.IS_DEFAULT)) {
            disableActionsWhenDefault(view);
        }
    }

    public void toggleGridsEnable(final ViewDefinitionState view, final String appliesTo, final boolean shouldClear) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        GridComponent workstationsGrid = (GridComponent) view.getComponentByReference(FaultTypeFields.WORKSTATIONS);
        GridComponent subassembliesGrid = (GridComponent) view.getComponentByReference(FaultTypeFields.SUBASSEMBLIES);
        GridComponent workstationTypesGrid = (GridComponent) view.getComponentByReference(FaultTypeFields.WORKSTATION_TYPES);

        boolean idNotNull = form.getEntityId() != null;

        if (FaultTypeAppliesTo.WORKSTATION_OR_SUBASSEMBLY.getStringValue().equals(appliesTo) && idNotNull) {
            workstationsGrid.setEnabled(true);
            subassembliesGrid.setEnabled(true);
            workstationTypesGrid.setEnabled(false);

            if (shouldClear) {
                workstationTypesGrid.setEntities(Lists.newArrayList());
            }
        } else if (FaultTypeAppliesTo.WORKSTATION_TYPE.getStringValue().equals(appliesTo) && idNotNull) {
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

        GridComponent workstationsGrid = (GridComponent) view.getComponentByReference(FaultTypeFields.WORKSTATIONS);
        GridComponent subassembliesGrid = (GridComponent) view.getComponentByReference(FaultTypeFields.SUBASSEMBLIES);
        GridComponent workstationTypesGrid = (GridComponent) view.getComponentByReference(FaultTypeFields.WORKSTATION_TYPES);

        workstationsGrid.setEnabled(false);
        subassembliesGrid.setEnabled(false);
        workstationTypesGrid.setEnabled(false);

        FieldComponent nameField = (FieldComponent) view.getComponentByReference(FaultTypeFields.NAME);
        FieldComponent appliesToField = (FieldComponent) view.getComponentByReference(FaultTypeFields.APPLIES_TO);

        nameField.setEnabled(false);
        appliesToField.setEnabled(false);
    }

}
