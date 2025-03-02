package com.sleektiv.mes.productFlowThruDivision.warehouseIssue.criteriaModifiers;

import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import org.springframework.stereotype.Service;

@Service
public class ProductToIssueCorrectionCriteriaModifiers {

    public void showAvailableWarehouses(SearchCriteriaBuilder scb, FilterValueHolder filter) {
        if (filter.has("locationFrom")) {
            scb.add(SearchRestrictions.ne("id", filter.getLong("locationFrom")));
        }
        if (filter.has("locationTo")) {
            scb.add(SearchRestrictions.ne("id", filter.getLong("locationTo")));
        }
    }

}
