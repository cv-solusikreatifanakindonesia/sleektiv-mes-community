package com.sleektiv.mes.materialFlowResources.listeners;

import com.sleektiv.mes.materialFlowResources.hooks.StorageLocationsDetailsHooks;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StorageLocationsDetailsListeners {

    @Autowired
    private StorageLocationsDetailsHooks storageLocationsDetailsHooks;

    public void updateFields(final ViewDefinitionState view, final ComponentState componentState,
            final String[] args) {
        storageLocationsDetailsHooks.onBeforeRender(view);
    }

}
