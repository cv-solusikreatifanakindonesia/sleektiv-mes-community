package com.sleektiv.mes.stoppage.criteriaModifiers;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.mes.stoppage.constants.StoppageFields;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;

@Service
public class ProductionTrackingsCriteriaModifiers {

    public void showForOrder(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        if (filterValue.has(StoppageFields.ORDER)) {
            scb.add(SearchRestrictions.belongsTo(StoppageFields.ORDER, OrdersConstants.PLUGIN_IDENTIFIER,
                    OrdersConstants.MODEL_ORDER, filterValue.getLong(StoppageFields.ORDER)));
        } else {
            scb.add(SearchRestrictions.belongsTo(StoppageFields.ORDER, OrdersConstants.PLUGIN_IDENTIFIER,
                    OrdersConstants.MODEL_ORDER, 0L));
        }
    }
}
