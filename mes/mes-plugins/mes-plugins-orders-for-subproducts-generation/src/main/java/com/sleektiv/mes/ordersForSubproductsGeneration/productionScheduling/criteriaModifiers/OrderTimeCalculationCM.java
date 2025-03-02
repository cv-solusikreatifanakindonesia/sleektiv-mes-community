/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv Framework
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
package com.sleektiv.mes.ordersForSubproductsGeneration.productionScheduling.criteriaModifiers;

import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderTimeCalculationCM {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public static final String PARENT_COMPONENTS_ORDER = "orderID";

    public void forOrderAndSubOrders(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        Long orderId = null;
        if (!filterValue.has(PARENT_COMPONENTS_ORDER)) {
            orderId = 0L;
        } else {
            orderId = filterValue.getLong(PARENT_COMPONENTS_ORDER);
        }
        scb.add(SearchRestrictions.in("order.id", getOrderAndSubOrders(orderId)));
    }

    private List<Long> getOrderAndSubOrders(final Long orderID) {
        String sql = "select o from #orders_order as o\n" + "where o.root=:orderID or o.id=:orderID";
        List<Entity> entities = dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_ORDER).find(sql).setLong("orderID", orderID).list().getEntities();

        return entities.stream().map(entity -> entity.getId()).collect(Collectors.toList());
    }
}
