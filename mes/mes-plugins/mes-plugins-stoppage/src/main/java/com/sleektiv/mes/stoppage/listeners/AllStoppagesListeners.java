package com.sleektiv.mes.stoppage.listeners;

import com.google.common.collect.Maps;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.json.JSONException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AllStoppagesListeners {

    

    private static final String L_ORDER = "order";

    public void addNew(final ViewDefinitionState view, final ComponentState state, final String[] args) throws JSONException {
        Map<String, Object> parameters = Maps.newHashMap();
        view.redirectTo("../page/stoppage/allStoppagesForm.html", false, true, parameters);
    }

    public void addNewFromProductionTracking(final ViewDefinitionState view, final ComponentState state, final String[] args)
            throws JSONException {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity productionTracking = form.getEntity();
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("form.productionTracking", productionTracking.getId());
        parameters.put("form.order", productionTracking.getBelongsToField(L_ORDER).getId());
        view.openModal("../page/stoppage/allStoppagesForm.html", parameters);
    }
}
