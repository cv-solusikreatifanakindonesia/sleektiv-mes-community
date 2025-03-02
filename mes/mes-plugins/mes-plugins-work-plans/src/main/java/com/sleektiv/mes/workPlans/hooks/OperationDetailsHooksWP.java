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
package com.sleektiv.mes.workPlans.hooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.workPlans.constants.ParameterFieldsWP;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class OperationDetailsHooksWP {

    

    @Autowired
    private ParameterService parameterService;

    public final void setOperationDefaultValues(final ViewDefinitionState view) {
        FormComponent operationForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        if (operationForm.getEntityId() == null) {
            for (String fieldName : Lists.newArrayList(ParameterFieldsWP.IMAGE_URL_IN_WORK_PLAN)) {
                FieldComponent fieldComponent = (FieldComponent) view.getComponentByReference(fieldName);
                fieldComponent.setFieldValue(getParameterField(fieldName));
            }
        }
    }

    private Object getParameterField(final String fieldName) {
        Entity parameter = parameterService.getParameter();

        return parameter.getField(fieldName);
    }

}
