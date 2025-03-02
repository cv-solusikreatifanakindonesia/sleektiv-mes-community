package com.sleektiv.mes.stoppage.criteriaModifiers;

import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.orders.states.constants.OrderState;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import org.springframework.stereotype.Service;

@Service
public class StoppageOrdersCriteriaModifiers {

    public void showNotRejected(final SearchCriteriaBuilder scb) {
        scb.add(SearchRestrictions.ne(OrderFields.STATE,
                OrderState.DECLINED.getStringValue()));
    }
}
