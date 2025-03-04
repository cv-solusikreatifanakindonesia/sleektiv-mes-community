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
package com.sleektiv.mes.orders.criteriaModifiers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.orders.constants.OperationalTaskFields;
import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.plugins.users.constants.GroupDetailsConstants;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;

@Service
public class OperationalTaskDetailsCriteriaModifiers {

    public static final String OPERATIONAL_TASK_ID = "operationalTaskId";

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void criteriaForOperationalTasks(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        Long operationalTaskId = filterValue.getLong(OPERATIONAL_TASK_ID);
        Entity operationalTask = dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_OPERATIONAL_TASK).find().
                add(SearchRestrictions.idEq(operationalTaskId)).uniqueResult();
        List<Entity> workers = operationalTask.getManyToManyField(OperationalTaskFields.WORKERS);
        for (Entity worker : workers) {
            scb.add(SearchRestrictions.idNe(worker.getId()));
        }
    }
}