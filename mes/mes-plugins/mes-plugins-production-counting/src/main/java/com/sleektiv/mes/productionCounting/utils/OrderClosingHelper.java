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
package com.sleektiv.mes.productionCounting.utils;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.basicProductionCounting.BasicProductionCountingService;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.productionCounting.constants.OrderFieldsPC;
import com.sleektiv.mes.productionCounting.constants.ParameterFieldsPC;
import com.sleektiv.mes.productionCounting.constants.ProductionTrackingFields;
import com.sleektiv.mes.productionCounting.constants.TypeOfProductionRecording;
import com.sleektiv.mes.productionCounting.states.constants.ProductionTrackingStateStringValues;
import com.sleektiv.mes.technologies.constants.TechnologyFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchProjections;
import com.sleektiv.model.api.search.SearchRestrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class OrderClosingHelper {

    private static final Function<Entity, Long> FUNC_EXTRACT_PROJECTION_ID = from -> (Long) from.getField("id");

    public static final String L_PRODUCT = "product";

    @Autowired
    private ParameterService parameterService;

    @Autowired
    private BasicProductionCountingService basicProductionCountingService;

    public boolean orderShouldBeClosed(final Entity productionTracking) {
        Entity order = productionTracking.getBelongsToField(ProductionTrackingFields.ORDER);

        if (Objects.isNull(order)) {
            return false;
        }

        Entity parameter = parameterService.getParameter();

        Boolean autoCloseOrder = parameter.getBooleanField(ParameterFieldsPC.AUTO_CLOSE_ORDER);
        Boolean isLastRecord = productionTracking.getBooleanField(ProductionTrackingFields.LAST_TRACKING);

        TypeOfProductionRecording recType = TypeOfProductionRecording
                .parseString(order.getStringField(OrderFieldsPC.TYPE_OF_PRODUCTION_RECORDING));

        return isLastRecord && autoCloseOrder
                && (!(TypeOfProductionRecording.FOR_EACH.equals(recType))
                || eachOperationHasLastRecords(order, productionTracking))
                || isProductionOrderedQuantityClosesTheOrder(productionTracking);
    }

    private boolean isProductionOrderedQuantityClosesTheOrder(final Entity productionTracking) {
        boolean productionOrderedQuantityClosesTheOrder = parameterService.getParameter()
                .getBooleanField(ParameterFieldsPC.PRODUCTION_ORDERED_QUANTITY_CLOSES_THE_ORDER);
        if (productionOrderedQuantityClosesTheOrder) {
            Entity order = productionTracking.getBelongsToField(ProductionTrackingFields.ORDER);
            Entity orderDb = order.getDataDefinition().get(order.getId());
            BigDecimal planned = orderDb.getDecimalField(OrderFields.PLANNED_QUANTITY);
            BigDecimal done = basicProductionCountingService.getProducedQuantityFromBasicProductionCountings(order);

            return done.compareTo(planned) >= 0;
        } else {
            return false;
        }
    }

    private boolean eachOperationHasLastRecords(final Entity order, final Entity productionTracking) {
        Entity technology = order.getBelongsToField(OrderFields.TECHNOLOGY);

        int numOfOrderOperations = technology.getHasManyField(TechnologyFields.OPERATION_COMPONENTS).size();

        Set<Long> matchingRecordIds = getIdsOfAcceptedLastRecords(productionTracking.getDataDefinition(), order.getId());

        int numOfMatchingRecords = matchingRecordIds.size();
        long recordId = productionTracking.getId();

        return (numOfMatchingRecords >= numOfOrderOperations)
                || (numOfMatchingRecords == (numOfOrderOperations - 1) && !matchingRecordIds.contains(recordId));
    }

    private Set<Long> getIdsOfAcceptedLastRecords(final DataDefinition productionTrackingDD, final Long orderId) {
        SearchCriteriaBuilder scb = productionTrackingDD.find();

        scb.add(SearchRestrictions.eq(ProductionTrackingFields.STATE, ProductionTrackingStateStringValues.ACCEPTED));
        scb.add(SearchRestrictions.eq(ProductionTrackingFields.LAST_TRACKING, true));
        scb.add(SearchRestrictions.eq(ProductionTrackingFields.ORDER + ".id", orderId));

        scb.setProjection(SearchProjections.alias(SearchProjections.id(), "id"));

        List<Entity> result = scb.list().getEntities();

        return Sets.newHashSet(Collections2.transform(result, FUNC_EXTRACT_PROJECTION_ID));
    }

}
