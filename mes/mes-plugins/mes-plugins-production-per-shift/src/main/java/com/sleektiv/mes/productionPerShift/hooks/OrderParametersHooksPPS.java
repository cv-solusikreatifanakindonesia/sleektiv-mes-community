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
package com.sleektiv.mes.productionPerShift.hooks;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.orders.constants.ParameterFieldsO;
import com.sleektiv.mes.productionPerShift.constants.ParameterFieldsPPS;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class OrderParametersHooksPPS {
    
    public void onBeforeRender(final ViewDefinitionState view) {
        getParametersAndTogglePpsAlgorithm(view);
    }
    
    public void onChangePpsIsAutomatic(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        getParametersAndTogglePpsAlgorithm(view);
    }
    
    private void getParametersAndTogglePpsAlgorithm(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity parameters = form.getPersistedEntityWithIncludedFormValues();
        
        togglePpsAlgorithm(view, parameters);
    }
    
    private void togglePpsAlgorithm(final ViewDefinitionState view, Entity parameters) {
        boolean isPpsAutomatic = parameters.getBooleanField(ParameterFieldsO.PPS_IS_AUTOMATIC);
        FieldComponent ppsAlgorithmComponent = (FieldComponent) view.getComponentByReference(ParameterFieldsPPS.PPS_ALGORITHM);
        
        if (!isPpsAutomatic) {
            ppsAlgorithmComponent.setFieldValue(null);
        }
        ppsAlgorithmComponent.setEnabled(isPpsAutomatic);
        ppsAlgorithmComponent.requestComponentUpdateState();
    }
    
}
