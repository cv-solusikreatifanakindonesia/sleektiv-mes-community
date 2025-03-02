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
package com.sleektiv.mes.timeNormsForOperations.listeners;

import com.sleektiv.mes.technologies.constants.TechnologyFields;
import com.sleektiv.mes.technologies.states.constants.TechnologyState;
import com.sleektiv.mes.timeNormsForOperations.NormService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ComponentState.MessageType;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TechnologyListenersTN {

    

    @Autowired
    private NormService normService;

    public void checkOperationOutputQuantities(final ViewDefinitionState view, final ComponentState componentState,
            final String[] args) {
        FormComponent technologyForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        if (technologyForm.getEntityId() == null) {
            return;
        }

        Entity technology = technologyForm.getEntity();

        if (!TechnologyState.DRAFT.getStringValue().equals(technology.getStringField(TechnologyFields.STATE))) {
            return;
        }

        technology = technology.getDataDefinition().get(technology.getId());

        List<String> messages = normService.checkOperationOutputQuantities(technology);

        if (!messages.isEmpty()) {
            StringBuilder builder = new StringBuilder();

            for (String message : messages) {
                builder.append(message);
                builder.append(", ");
            }

            technologyForm.addMessage("technologies.technology.validate.error.invalidQuantity", MessageType.INFO, false,
                    builder.toString());
        }
    }

}
