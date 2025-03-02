package com.sleektiv.mes.materialFlowResources.hooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.materialFlow.constants.LocationFields;
import com.sleektiv.mes.materialFlow.constants.MaterialFlowConstants;
import com.sleektiv.mes.materialFlowResources.constants.StorageLocationFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.AwesomeDynamicListComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;

@Service
public class PalletMoveToStorageLocationHelperHooks extends PalletStorageStateHooks {

    @Autowired
    public PalletMoveToStorageLocationHelperHooks(DataDefinitionService dataDefinitionService) {
        super(dataDefinitionService);
    }

    @Override
    protected void setStorageLocationFilters(final ViewDefinitionState view) {
        DataDefinition locationDD = dataDefinitionService.get(MaterialFlowConstants.PLUGIN_IDENTIFIER,
                MaterialFlowConstants.MODEL_LOCATION);
        AwesomeDynamicListComponent adl = (AwesomeDynamicListComponent) view.getComponentByReference("palletStorageStateDtos");

        for (FormComponent form : adl.getFormComponents()) {
            LookupComponent newStorageLocation = (LookupComponent) form.findFieldComponentByName("newStorageLocation");
            FilterValueHolder filter = newStorageLocation.getFilterValue();
            Entity dto = form.getPersistedEntityWithIncludedFormValues();

            String locationNumber = dto.getStringField("locationNumber");
            Entity location = locationDD.find().add(SearchRestrictions.eq(LocationFields.NUMBER, locationNumber))
                    .setMaxResults(1).uniqueResult();
            filter.put(StorageLocationFields.LOCATION, location.getId());
            newStorageLocation.setFilterValue(filter);

        }

    }
}
