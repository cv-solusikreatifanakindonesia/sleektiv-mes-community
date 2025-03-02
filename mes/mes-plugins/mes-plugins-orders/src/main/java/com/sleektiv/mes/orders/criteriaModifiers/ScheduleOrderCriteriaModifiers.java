package com.sleektiv.mes.orders.criteriaModifiers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.mes.orders.constants.ScheduleFields;
import com.sleektiv.mes.orders.states.constants.OrderState;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.search.JoinType;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchProjections;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.model.api.search.SearchSubqueries;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;

@Service
public class ScheduleOrderCriteriaModifiers {

    public static final String SCHEDULE_PARAMETER = "scheduleId";

    private static final String L_TYPE_OF_PRODUCTION_RECORDING = "typeOfProductionRecording";

    private static final String L_FOR_EACH = "03forEach";

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void filterByState(final SearchCriteriaBuilder scb, final FilterValueHolder filterValueHolder) {
        scb.add(SearchRestrictions.in(OrderFields.STATE,
                Lists.newArrayList(OrderState.ACCEPTED.getStringValue(), OrderState.PENDING.getStringValue())));
        scb.add(SearchRestrictions.eq(L_TYPE_OF_PRODUCTION_RECORDING, L_FOR_EACH));
        if (filterValueHolder.has(SCHEDULE_PARAMETER)) {
            SearchCriteriaBuilder subCriteria = dataDefinitionService
                    .get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_SCHEDULE)
                    .findWithAlias(OrdersConstants.MODEL_SCHEDULE)
                    .add(SearchRestrictions.idEq(filterValueHolder.getLong(SCHEDULE_PARAMETER)))
                    .createAlias(ScheduleFields.ORDERS, ScheduleFields.ORDERS, JoinType.INNER)
                    .add(SearchRestrictions.eqField(ScheduleFields.ORDERS + ".id", "this.id"))
                    .setProjection(SearchProjections.id());
            scb.add(SearchSubqueries.notExists(subCriteria));
        }
    }

}
