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
package com.sleektiv.mes.workPlans.listeners;

import com.google.common.collect.Maps;
import com.sleektiv.mes.workPlans.WorkPlansService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrdersPlanningListListenersWP {

    @Autowired
    private WorkPlansService workPlanService;

    public final void addSelectedOrdersToWorkPlan(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent ordersGrid = (GridComponent) state;

        List<Entity> orders = workPlanService.getSelectedOrders(ordersGrid.getSelectedEntitiesIds());

        Entity workPlan = workPlanService.generateWorkPlanEntity(orders);

        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("form.id", workPlan.getId());
        parameters.put("window.activeMenu", "reports.workPlans");

        view.redirectTo("/page/workPlans/workPlanDetails.html", false, true, parameters);
    }

    public final void workPlanDelivered(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent ordersGrid = (GridComponent) state;
        workPlanService.workPlanDelivered(state, ordersGrid.getSelectedEntities());
    }


}
