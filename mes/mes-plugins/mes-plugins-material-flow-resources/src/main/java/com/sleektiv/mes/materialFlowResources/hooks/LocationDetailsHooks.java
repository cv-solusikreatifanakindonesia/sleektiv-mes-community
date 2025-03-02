package com.sleektiv.mes.materialFlowResources.hooks;

import com.sleektiv.mes.materialFlowResources.constants.LocationFieldsMFR;
import com.sleektiv.mes.materialFlowResources.constants.ResourceFields;
import com.sleektiv.mes.materialFlowResources.criteriaModifiers.StorageLocationCriteriaModifiers;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class LocationDetailsHooks {

    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        LookupComponent transferStorageLocationLookup = (LookupComponent) view.getComponentByReference(LocationFieldsMFR.TRANSFER_STORAGE_LOCATION);

        FilterValueHolder filter = transferStorageLocationLookup.getFilterValue();

        Entity location = form.getPersistedEntityWithIncludedFormValues();

        if (Objects.nonNull(location.getId())) {
            filter.put(StorageLocationCriteriaModifiers.L_LOCATION, location.getId());
        }

        transferStorageLocationLookup.setFilterValue(filter);
    }
}
