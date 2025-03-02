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
package com.sleektiv.mes.workPlans.pdf.document.operation.grouping.container;

import com.google.common.collect.ListMultimap;
import com.sleektiv.mes.columnExtension.constants.ColumnAlignment;
import com.sleektiv.mes.workPlans.pdf.document.operation.grouping.holder.OrderOperationComponent;
import com.sleektiv.mes.workPlans.pdf.document.operation.product.column.OperationProductColumn;
import com.sleektiv.mes.workPlans.pdf.document.order.column.OrderColumn;
import com.sleektiv.model.api.Entity;

import java.util.List;
import java.util.Map;

public interface GroupingContainer {

    void add(final Entity order, final Entity operationComponent, List<Entity> productionCountingQuantitiesIn,
            List<Entity> productionCountingQuantitiesOut);

    void add(final OrderOperationComponent orderOperationComponent);

    ListMultimap<String, OrderOperationComponent> getTitleToOperationComponent();

    List<Entity> getOrders();

    Map<OrderColumn, ColumnAlignment> getOrderColumnToAlignment();

    Map<Long, Map<OperationProductColumn, ColumnAlignment>> getOperationComponentIdProductInColumnToAlignment();

    Map<Long, Map<OperationProductColumn, ColumnAlignment>> getOperationComponentIdProductOutColumnToAlignment();

}
