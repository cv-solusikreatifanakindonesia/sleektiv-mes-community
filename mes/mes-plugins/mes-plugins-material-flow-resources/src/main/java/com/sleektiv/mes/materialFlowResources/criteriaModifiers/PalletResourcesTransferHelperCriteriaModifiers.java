package com.sleektiv.mes.materialFlowResources.criteriaModifiers;

import static com.sleektiv.mes.materialFlowResources.constants.PalletStorageStateDtoFields.LOCATION_NUMBER;
import static com.sleektiv.mes.materialFlowResources.constants.PalletStorageStateDtoFields.PALLET_NUMBER;
import static com.sleektiv.mes.materialFlowResources.constants.PalletStorageStateDtoFields.PALLET_NUMBER_ACTIVE;

import org.springframework.stereotype.Service;

import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;

@Service
public class PalletResourcesTransferHelperCriteriaModifiers {

    public void restrictRecords(final SearchCriteriaBuilder searchCriteriaBuilder, final FilterValueHolder filterValueHolder) {
        String locationNumber = filterValueHolder.getString(LOCATION_NUMBER);
        String palletNumber = filterValueHolder.getString(PALLET_NUMBER);
        searchCriteriaBuilder.add(SearchRestrictions.eq(LOCATION_NUMBER, locationNumber))
                .add(SearchRestrictions.eq(PALLET_NUMBER_ACTIVE, Boolean.TRUE))
                .add(SearchRestrictions.ne(PALLET_NUMBER, palletNumber));
    }

}
