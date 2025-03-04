package com.sleektiv.mes.materialFlowResources.criteriaModifiers;

import org.springframework.stereotype.Service;

import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;

@Service
public class PalletMoveToStorageLocationHelperCriteriaModifiers {

    private static final String LOCATION = "location";

    public void restrictStorageLocation(final SearchCriteriaBuilder searchCriteriaBuilder,
            final FilterValueHolder filterValueHolder) {

        if (filterValueHolder.has(LOCATION)) {
            Long locationId = filterValueHolder.getLong(LOCATION);
            searchCriteriaBuilder.add(SearchRestrictions.eq(LOCATION + ".id", locationId));
        }
    }

}
