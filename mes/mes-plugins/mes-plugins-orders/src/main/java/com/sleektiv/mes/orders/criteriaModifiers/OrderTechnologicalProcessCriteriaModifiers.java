package com.sleektiv.mes.orders.criteriaModifiers;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.orders.constants.OrderTechnologicalProcessFields;
import com.sleektiv.mes.orders.states.constants.OrderStateStringValues;
import com.sleektiv.model.api.search.JoinType;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;

@Service
public class OrderTechnologicalProcessCriteriaModifiers {

    public void filterByDateWorkerAndOrderState(final SearchCriteriaBuilder scb) {
        scb.add(SearchRestrictions.isNotNull(OrderTechnologicalProcessFields.DATE))
                .add(SearchRestrictions.isNotNull(OrderTechnologicalProcessFields.WORKER));

        String orderAlias = OrderTechnologicalProcessFields.ORDER;

        if (!scb.existsAliasForAssociation(OrderTechnologicalProcessFields.ORDER)) {
            scb.createAlias(OrderTechnologicalProcessFields.ORDER, orderAlias, JoinType.LEFT);
        } else {
            orderAlias = scb.getAliasForAssociation(OrderTechnologicalProcessFields.ORDER);
        }

        scb.add(SearchRestrictions.not(SearchRestrictions.in(orderAlias + "." + OrderFields.STATE, Lists.newArrayList(
                OrderStateStringValues.DECLINED, OrderStateStringValues.COMPLETED, OrderStateStringValues.ABANDONED))));
    }

}
