package com.sleektiv.mes.materialFlowResources.hooks;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class PositionAddMultiHooks {

    private static final String L_LOCATION_FROM = "locationFrom";

    public final void onBeforeRender(final ViewDefinitionState view) throws JSONException {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity helper = form.getPersistedEntityWithIncludedFormValues();
        JSONObject context = view.getJsonContext();
        if (context != null) {
            Long warehouseId = context.getLong("window.mainTab.helper.gridLayout.warehouseId");
            Long documentId = context.getLong("window.mainTab.helper.gridLayout.documentId");
            helper.setField("documentId", documentId);
            helper.setField("warehouseId", warehouseId);

            GridComponent grid = (GridComponent) view.getComponentByReference("resourceGrid");
            FilterValueHolder filter = grid.getFilterValue();
            filter.put(L_LOCATION_FROM, warehouseId);
            grid.setFilterValue(filter);

            form.setEntity(helper);
        }

    }

}
