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
package com.sleektiv.mes.lineChangeoverNorms.hooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.lineChangeoverNorms.constants.LineChangeoverNormsConstants;
import com.sleektiv.mes.lineChangeoverNorms.listeners.MatchingChangeoverNormsDetailsListeners;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class MatchingChangeoverNormsDetailsHooks {

    

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private MatchingChangeoverNormsDetailsListeners listeners;

    public void setFieldsVisible(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        ComponentState matchingNorm = view.getComponentByReference("matchingNorm");
        ComponentState matchingNormNotFound = view.getComponentByReference("matchingNormNotFound");

        if (form.getEntityId() == null) {
            matchingNorm.setVisible(false);
            matchingNormNotFound.setVisible(true);
        } else {
            matchingNorm.setVisible(true);
            matchingNormNotFound.setVisible(false);
        }
    }

    public void fillOrCleanFields(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        if (form.getEntityId() == null) {
            listeners.clearField(view);
            listeners.changeStateEditButton(view, false);
        } else {
            Entity changeover = dataDefinitionService.get(LineChangeoverNormsConstants.PLUGIN_IDENTIFIER,
                    LineChangeoverNormsConstants.MODEL_LINE_CHANGEOVER_NORMS).get(form.getEntityId());
            listeners.fillField(view, changeover);
            listeners.changeStateEditButton(view, true);
        }
    }

}
