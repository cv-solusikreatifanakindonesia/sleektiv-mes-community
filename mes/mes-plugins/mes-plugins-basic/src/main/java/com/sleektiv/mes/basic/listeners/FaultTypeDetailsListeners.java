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
package com.sleektiv.mes.basic.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.FaultTypeFields;
import com.sleektiv.mes.basic.hooks.FaultTypeDetailsHooks;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class FaultTypeDetailsListeners {

    @Autowired
    private FaultTypeDetailsHooks faultTypeDetailsHooks;



    public void toggleAndClearGrids(final ViewDefinitionState view, final ComponentState state, final String args[]) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Entity faultType = form.getPersistedEntityWithIncludedFormValues();
        String appliesTo = faultType.getStringField(FaultTypeFields.APPLIES_TO);

        faultTypeDetailsHooks.toggleGridsEnable(view, appliesTo, true);
    }

}
