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
package com.sleektiv.mes.productFlowThruDivision.hooks;

import com.sleektiv.mes.materialFlow.constants.ParameterFieldsMF;
import com.sleektiv.mes.materialFlow.constants.WhatToShowOnDashboard;
import com.sleektiv.mes.technologies.constants.ParameterFieldsT;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DashboardParametersHooksPDTD {

    public void onBeforeRender(final ViewDefinitionState view) {
        setFieldsEnabledAndClear(view);
    }

    private void setFieldsEnabledAndClear(final ViewDefinitionState view) {
        FormComponent parameterForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        FieldComponent whatToShowOnDashboardField = (FieldComponent) view
                .getComponentByReference(ParameterFieldsMF.WHAT_TO_SHOW_ON_DASHBOARD);
        LookupComponent dashboardOperationLookup = (LookupComponent) view
                .getComponentByReference(ParameterFieldsT.DASHBOARD_OPERATION);

        Long parameterId = parameterForm.getEntityId();
        String whatToShowOnDashboard = (String) whatToShowOnDashboardField.getFieldValue();

        boolean isEnabled = Objects.nonNull(parameterId);
        boolean isOrders = WhatToShowOnDashboard.ORDERS.getStringValue().equals(whatToShowOnDashboard);

        if (!isOrders) {
            dashboardOperationLookup.setFieldValue(null);
        }

        dashboardOperationLookup.setEnabled(isEnabled && isOrders);
        dashboardOperationLookup.requestComponentUpdateState();
    }

}
