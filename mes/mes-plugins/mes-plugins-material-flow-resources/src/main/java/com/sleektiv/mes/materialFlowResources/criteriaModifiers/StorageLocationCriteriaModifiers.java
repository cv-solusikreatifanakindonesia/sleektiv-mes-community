package com.sleektiv.mes.materialFlowResources.criteriaModifiers;

import com.sleektiv.mes.materialFlow.constants.MaterialFlowConstants;
import com.sleektiv.mes.materialFlowResources.constants.StorageLocationFields;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StorageLocationCriteriaModifiers {

    public static final String L_LOCATION = "location";

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void showStorageLocationsForLocation(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        if (filterValue.has(L_LOCATION)) {
            Long locationId = filterValue.getLong(L_LOCATION);

            scb.add(SearchRestrictions.belongsTo(StorageLocationFields.LOCATION, MaterialFlowConstants.PLUGIN_IDENTIFIER,
                    MaterialFlowConstants.MODEL_LOCATION, locationId));
        }
    }

    public void showStorageLocationsForLocationWithoutProducts(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        showStorageLocationsForLocation(scb, filterValue);
        scb.add(SearchRestrictions.isEmpty(StorageLocationFields.PRODUCTS));
    }

}
