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

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.cmmsMachineParts.constants.DocumentFieldsCMP;
import com.sleektiv.mes.materialFlowResources.constants.DocumentFields;
import com.sleektiv.mes.materialFlowResources.constants.DocumentState;
import com.sleektiv.mes.materialFlowResources.constants.DocumentType;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class DocumentDetailsHooksCMP {

    public void toggleEnabledForEventLookup(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity document = form.getPersistedEntityWithIncludedFormValues();
        LookupComponent eventLookup = (LookupComponent) view.getComponentByReference(DocumentFieldsCMP.MAINTENANCE_EVENT);
        LookupComponent plannedEventLookup = (LookupComponent) view.getComponentByReference(DocumentFieldsCMP.PLANNED_EVENT);

        String state = document.getStringField(DocumentFields.STATE);
        String type = document.getStringField(DocumentFields.TYPE);
        if (StringUtils.isEmpty(state) || StringUtils.isEmpty(type)) {
            eventLookup.setEnabled(false);
            plannedEventLookup.setEnabled(false);
        } else {
            if (state.equals(DocumentState.DRAFT.getStringValue()) && type
                    .equals(DocumentType.INTERNAL_OUTBOUND.getStringValue())) {
                eventLookup.setEnabled(true);
                plannedEventLookup.setEnabled(true);
            } else {
                eventLookup.setEnabled(false);
                plannedEventLookup.setEnabled(false);
            }
        }

    }
}
