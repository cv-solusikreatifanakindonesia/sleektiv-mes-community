/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv Framework
 * Version: 1.4
 * <p>
 * This file is part of Sleektiv.
 * <p>
 * Sleektiv is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.sleektiv.mes.productFlowThruDivision.hooks;

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.mes.orders.states.constants.OrderState;
import com.sleektiv.mes.orders.util.OrderDetailsRibbonHelper;
import com.sleektiv.mes.productFlowThruDivision.constants.OrderFieldsPFTD;
import com.sleektiv.mes.productFlowThruDivision.constants.ParameterFieldsPFTD;
import com.sleektiv.mes.productFlowThruDivision.criteriaModifiers.StaffCriteriaModifierPFTD;
import com.sleektiv.mes.productionCounting.constants.OrderFieldsPC;
import com.sleektiv.mes.productionCounting.constants.TypeOfProductionRecording;
import com.sleektiv.mes.technologies.TechnologyService;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderDetailsHooksPFTD {

    @Autowired
    ParameterService parameterService;

    @Autowired
    private OrderDetailsRibbonHelper orderDetailsRibbonHelper;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private TechnologyService technologyService;

    public void onBeforeRender(final ViewDefinitionState view) {
        orderDetailsRibbonHelper.setButtonEnabled(view, "materialFlow", "warehouseIssues", OrderDetailsRibbonHelper.HAS_CHECKED_OR_ACCEPTED_TECHNOLOGY::test);

        orderDetailsRibbonHelper.setButtonEnabled(view, "materialFlow", "componentAvailability", OrderDetailsRibbonHelper.HAS_CHECKED_OR_ACCEPTED_TECHNOLOGY::test);

        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        ComponentState staffTab = view.getComponentByReference("staffTab");
        if (form.getEntityId() != null) {
            Entity order = dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_ORDER).get(form.getEntityId());
            staffTab.setVisible(TypeOfProductionRecording.CUMULATED.getStringValue()
                    .equals(order.getStringField(OrderFieldsPC.TYPE_OF_PRODUCTION_RECORDING)));
            LookupComponent technologyLookup = (LookupComponent) view
                    .getComponentByReference(OrderFields.TECHNOLOGY);
            LookupComponent productionLineLookup = (LookupComponent) view.getComponentByReference(OrderFields.PRODUCTION_LINE);
            Entity technology = technologyLookup.getEntity();
            Entity productionLine = productionLineLookup.getEntity();
            FieldComponent plannedStaffField = (FieldComponent) view.getComponentByReference("plannedStaff");
            if (technology != null && productionLine != null) {
                Optional<Integer> plannedStaff = technologyService.getPlannedStaff(technology, productionLine);
                if (plannedStaff.isPresent()) {
                    plannedStaffField.setFieldValue(plannedStaff.get());
                } else {
                    plannedStaffField.setFieldValue(null);
                }
            } else {
                plannedStaffField.setFieldValue(null);
            }
            FieldComponent actualStaff = (FieldComponent) view.getComponentByReference("actualStaff");
            actualStaff.setFieldValue(order.getManyToManyField(OrderFields.STAFF).size());

            LookupComponent staffLookup = (LookupComponent) view.getComponentByReference("staffLookup");

            FilterValueHolder valueHolder = staffLookup.getFilterValue();
            valueHolder.put(StaffCriteriaModifierPFTD.L_ORDER_ID, form.getEntityId());
            if (productionLine != null) {
                valueHolder.put(StaffCriteriaModifierPFTD.L_PRODUCTION_LINE_ID, productionLine.getId());
            } else if (valueHolder.has(StaffCriteriaModifierPFTD.L_PRODUCTION_LINE_ID)) {
                valueHolder.remove(StaffCriteriaModifierPFTD.L_PRODUCTION_LINE_ID);
            }
            staffLookup.setFilterValue(valueHolder);
            String state = order.getStringField(OrderFields.STATE);
            GridComponent staffGrid = (GridComponent) view.getComponentByReference(OrderFields.STAFF);
            staffGrid.setEnabled(!OrderState.DECLINED.getStringValue().equals(state)
                    && !OrderState.ABANDONED.getStringValue().equals(state)
                    && !OrderState.COMPLETED.getStringValue().equals(state));
        } else {
            staffTab.setVisible(false);
        }
    }

    public void onBeforeRenderAdditionalForm(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        FieldComponent ignoreMissingProductsField = (FieldComponent) view
                .getComponentByReference(OrderFieldsPFTD.IGNORE_MISSING_COMPONENTS);

        if (form.getEntityId() == null) {
            ignoreMissingProductsField.setFieldValue(parameterService.getParameter().getBooleanField(
                    ParameterFieldsPFTD.IGNORE_MISSING_COMPONENTS));
            ignoreMissingProductsField.requestComponentUpdateState();
        }

    }
}
