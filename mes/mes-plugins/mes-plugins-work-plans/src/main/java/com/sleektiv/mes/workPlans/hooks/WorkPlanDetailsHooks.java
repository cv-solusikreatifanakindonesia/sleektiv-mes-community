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

import com.google.common.collect.Lists;
import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.workPlans.WorkPlansService;
import com.sleektiv.mes.workPlans.constants.ParameterFieldsWP;
import com.sleektiv.mes.workPlans.constants.WorkPlanFields;
import com.sleektiv.mes.workPlans.criteriaModifiers.WorkPlansCriteriaModifiers;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.CheckBoxComponent;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkPlanDetailsHooks {

    

    private static final String L_ATTCHMENT_GRID = "technologyAttachments";

    @Autowired
    private ParameterService parameterService;

    @Autowired
    private WorkPlansService workPlansService;

    public final void onBeforeRender(final ViewDefinitionState view) {
        setFieldsState(view);
        disableFormForGeneratedWorkPlan(view);
        setCriteriaModifierParameters(view);
        setWorkPlanDefaultValues(view);
    }

    public final void setFieldsState(final ViewDefinitionState view) {
        if (view.getComponentByReference("inputProductColumnToSortBy").getFieldValue() == null) {
            view.getComponentByReference("orderSorting").setEnabled(false);
        } else {
            view.getComponentByReference("orderSorting").setEnabled(true);
        }
    }

    final void disableFormForGeneratedWorkPlan(final ViewDefinitionState view) {
        FormComponent workPlanForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        CheckBoxComponent generatedCheckbox = (CheckBoxComponent) view.getComponentByReference(WorkPlanFields.GENERATED);

        if (workPlanForm == null) {
            return;
        }

        if (workPlanForm.getEntityId() == null) {
            view.getComponentByReference(WorkPlanFields.ORDERS).setEnabled(false);
            view.getComponentByReference(WorkPlanFields.WORK_PLAN_ORDER_COLUMNS).setEnabled(false);
        } else {
            boolean isEnabled = !generatedCheckbox.isChecked();

            view.getComponentByReference(WorkPlanFields.NAME).setEnabled(isEnabled);
            view.getComponentByReference(WorkPlanFields.TYPE).setEnabled(isEnabled);
            view.getComponentByReference(WorkPlanFields.DONT_PRINT_ORDERS_IN_WORK_PLANS).setEnabled(isEnabled);
            view.getComponentByReference(WorkPlanFields.ORDERS).setEnabled(isEnabled);
            view.getComponentByReference(WorkPlanFields.WORK_PLAN_ORDER_COLUMNS).setEnabled(isEnabled);
            view.getComponentByReference(WorkPlanFields.INPUT_PRODUCT_COLUMN_TO_SORT_BY).setEnabled(isEnabled);
            view.getComponentByReference(WorkPlanFields.ORDER_SORTING).setEnabled(isEnabled);
        }
    }

    private void setCriteriaModifierParameters(final ViewDefinitionState view) {
        // set technologies id
        FormComponent workPlanForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        if (workPlanForm.getEntityId() == null) {
            return;
        }

        List<Long> technologyIDs = Lists.newArrayList();

        List<Entity> orders = workPlanForm.getPersistedEntityWithIncludedFormValues().getManyToManyField(WorkPlanFields.ORDERS);

        if (orders.isEmpty()) {
            return;
        }
        for (Entity order : orders) {
            if(order.isActive() && order.getBelongsToField(OrderFields.TECHNOLOGY) != null) {
                technologyIDs.add(order.getBelongsToField(OrderFields.TECHNOLOGY).getId());
            }
        }
        GridComponent atachmentsGrid = (GridComponent) view.getComponentByReference(L_ATTCHMENT_GRID);
        FilterValueHolder atachmentsGridHolder = atachmentsGrid.getFilterValue();
        atachmentsGridHolder.put(WorkPlansCriteriaModifiers.TECHNOLOGY_IDS, technologyIDs);
        atachmentsGrid.setFilterValue(atachmentsGridHolder);

    }

    private void setWorkPlanDefaultValues(final ViewDefinitionState view) {
        FormComponent workPlanForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        if (workPlanForm.getEntityId() == null) {
            FieldComponent fieldComponent = (FieldComponent) view
                    .getComponentByReference(WorkPlanFields.DONT_PRINT_ORDERS_IN_WORK_PLANS);
            fieldComponent.setFieldValue(parameterService.getParameter().getField(
                    ParameterFieldsWP.DONT_PRINT_ORDERS_IN_WORK_PLANS));
            FieldComponent nameField = (FieldComponent) view.getComponentByReference("name");
            nameField.setFieldValue(workPlansService.generateNameForWorkPlan());
        }
    }

}
