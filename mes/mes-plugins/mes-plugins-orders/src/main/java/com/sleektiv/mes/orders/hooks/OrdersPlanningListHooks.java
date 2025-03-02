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
package com.sleektiv.mes.orders.hooks;

import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.orders.states.constants.OrderState;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

@Service
public class OrdersPlanningListHooks {

    public final void addDiscriminatorRestrictionToGrid(final ViewDefinitionState view) {
        GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
        grid.setCustomRestriction(searchBuilder -> searchBuilder.add(SearchRestrictions.or(
                SearchRestrictions.eq(OrderFields.STATE, OrderState.PENDING.getStringValue()),
                SearchRestrictions.eq(OrderFields.STATE, OrderState.IN_PROGRESS.getStringValue()),
                SearchRestrictions.eq(OrderFields.STATE, OrderState.INTERRUPTED.getStringValue()),
                SearchRestrictions.eq(OrderFields.STATE, OrderState.ACCEPTED.getStringValue()))));
    }

}
