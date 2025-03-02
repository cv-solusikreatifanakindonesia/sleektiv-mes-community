package com.sleektiv.mes.materialFlowResources.hooks;

import static com.sleektiv.mes.materialFlowResources.constants.PalletStorageStateDtoFields.LOCATION_NUMBER;
import static com.sleektiv.mes.materialFlowResources.constants.PalletStorageStateDtoFields.PALLET_NUMBER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.AwesomeDynamicListComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;

@Service
public class PalletResourcesTransferHelperHooks extends PalletStorageStateHooks {

    @Autowired
    public PalletResourcesTransferHelperHooks(DataDefinitionService dataDefinitionService) {
        super(dataDefinitionService);
    }

    @Override
    protected void setStorageLocationFilters(final ViewDefinitionState view) {
        AwesomeDynamicListComponent adl = (AwesomeDynamicListComponent) view.getComponentByReference(L_PALLET_STORAGE_STATE_DTOS);
        for (FormComponent form : adl.getFormComponents()) {
            LookupComponent newPalletNumber = (LookupComponent) form.findFieldComponentByName("newPalletNumber");
            FilterValueHolder filter = newPalletNumber.getFilterValue();
            Entity persistedEntity = form.getPersistedEntityWithIncludedFormValues();
            filter.put(LOCATION_NUMBER, persistedEntity.getStringField(LOCATION_NUMBER));
            filter.put(PALLET_NUMBER, persistedEntity.getStringField(PALLET_NUMBER));
            newPalletNumber.setFilterValue(filter);
        }
    }

}
