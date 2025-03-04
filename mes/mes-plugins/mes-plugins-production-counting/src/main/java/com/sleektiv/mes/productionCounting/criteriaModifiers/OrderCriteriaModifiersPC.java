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
package com.sleektiv.mes.productionCounting.criteriaModifiers;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.orders.states.constants.OrderState;
import com.sleektiv.mes.productionCounting.constants.OrderFieldsPC;
import com.sleektiv.mes.productionCounting.constants.TypeOfProductionRecording;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;

@Service
public class OrderCriteriaModifiersPC {

    public void showAcceptedAndInterrupted(final SearchCriteriaBuilder scb) {
        scb.add(SearchRestrictions.in(OrderFields.STATE,
                Lists.newArrayList(OrderState.IN_PROGRESS.getStringValue(), OrderState.INTERRUPTED.getStringValue()))).add(
                SearchRestrictions.in(OrderFieldsPC.TYPE_OF_PRODUCTION_RECORDING,
                        Lists.newArrayList(TypeOfProductionRecording.CUMULATED.getStringValue(),
                                TypeOfProductionRecording.FOR_EACH.getStringValue())));

    }

    public void showOrdersForProductionBalance(final SearchCriteriaBuilder scb) {
        scb.add(SearchRestrictions.in(OrderFields.STATE, Lists.newArrayList(OrderState.IN_PROGRESS.getStringValue(),
                OrderState.COMPLETED.getStringValue(), OrderState.INTERRUPTED.getStringValue(),
                OrderState.ABANDONED.getStringValue())));
    }
}
