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

import com.sleektiv.mes.cmmsMachineParts.constants.PlannedEventRealizationFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service public class PlannedEventRealizationDetailsHooks {

    public static final String L_EVENT = "plannedEvent";

    public void onBeforeRender(final ViewDefinitionState view) {
        setFilterValues(view);
    }

    private void setFilterValues(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity plannedEventRealization = form.getPersistedEntityWithIncludedFormValues();
        if (plannedEventRealization != null) {
            Long plannedEventId = plannedEventRealization.getBelongsToField(PlannedEventRealizationFields.PLANNED_EVENT).getId();
            LookupComponent actionsLookup = (LookupComponent) view.getComponentByReference("action");

            FilterValueHolder actionsFVH = actionsLookup.getFilterValue();

            actionsFVH.put(L_EVENT, plannedEventId);
            actionsLookup.setFilterValue(actionsFVH);
            actionsLookup.requestComponentUpdateState();
        }
    }
}
