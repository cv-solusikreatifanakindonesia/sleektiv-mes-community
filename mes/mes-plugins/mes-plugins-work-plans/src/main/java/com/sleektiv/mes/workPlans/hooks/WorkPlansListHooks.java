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

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.sleektiv.mes.orders.states.constants.OrderState;
import com.sleektiv.mes.workPlans.constants.WorkPlanFields;
import com.sleektiv.mes.workPlans.view.WorkPlansListView;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;

@Service
public class WorkPlansListHooks {

    private static final Predicate<Entity> IS_COMPLETED_ORDER = new Predicate<Entity>() {

        @Override
        public boolean apply(final Entity order) {
            return OrderState.of(order) == OrderState.COMPLETED;
        }
    };

    private static final Predicate<Entity> WORK_PLAN_CAN_BE_DELETED = new Predicate<Entity>() {

        @Override
        public boolean apply(final Entity workPlan) {
            boolean hasAnyCompletedOrder = Iterables.any(workPlan.getHasManyField(WorkPlanFields.ORDERS), IS_COMPLETED_ORDER);
            return !(hasAnyCompletedOrder && workPlan.getBooleanField(WorkPlanFields.GENERATED));
        }
    };

    public void setGridGenerateButtonState(final ViewDefinitionState viewDefinitionState) {
        setGridGenerateButtonState(WorkPlansListView.from(viewDefinitionState));
    }

    public void setGridGenerateButtonState(final WorkPlansListView view) {
        List<Entity> selectedWorkPlans = view.getSelectedWorkPlans();
        if (selectedWorkPlans.isEmpty()) {
            view.setUpDeleteButton(false, null);
        } else {
            boolean selectedWorkPlansCanBeDeleted = Iterables.all(selectedWorkPlans, WORK_PLAN_CAN_BE_DELETED);
            String message = getDeleteButtonDescription(selectedWorkPlansCanBeDeleted);
            view.setUpDeleteButton(selectedWorkPlansCanBeDeleted, message);
        }
    }

    private String getDeleteButtonDescription(final boolean selectedWorkPlansCanBeDeleted) {
        if (selectedWorkPlansCanBeDeleted) {
            return null;
        }
        return "orders.ribbon.message.selectedRecordCannotBeDeleted";
    }

}
