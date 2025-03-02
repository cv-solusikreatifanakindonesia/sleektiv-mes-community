package com.sleektiv.mes.productionCounting.criteriaModifiers;

import com.sleektiv.mes.productionCounting.hooks.TrackingOperationProductOutComponentDetailsHooks;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import org.springframework.stereotype.Service;

@Service
public class StorageLocationCriteriaModifierPC {

    public void filter(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        if (filterValue.has(TrackingOperationProductOutComponentDetailsHooks.L_LOCATION_ID)) {
            Long location = filterValue.getLong(TrackingOperationProductOutComponentDetailsHooks.L_LOCATION_ID);
            scb.add(SearchRestrictions.eq("location.id", location));
        } else {
            scb.add(SearchRestrictions.eq("location.id", Long.valueOf("-1")));
        }

    }
}
