package com.sleektiv.mes.basic.hooks;

import com.sleektiv.view.api.ViewDefinitionState;

import org.json.JSONException;
import org.springframework.stereotype.Service;

@Service
public class CompanyDefaultProductsHooks {

    public void onBeforeRender(final ViewDefinitionState view) throws JSONException {
     /*   JSONObject obj = view.getJsonContext();
        if(obj.has("window.mainTab.product.companyId")) {
            GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
            FilterValueHolder holder = grid.getFilterValue();
            holder.put("COMPANY_ID", obj.getLong("window.mainTab.product.companyId"));
            grid.setFilterValue(holder);
        }
    }*/}
}

