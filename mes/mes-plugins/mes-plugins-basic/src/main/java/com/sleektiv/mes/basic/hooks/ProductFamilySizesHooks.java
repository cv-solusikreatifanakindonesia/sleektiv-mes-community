package com.sleektiv.mes.basic.hooks;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.criteriaModifiers.ProductFamilySizesCriteriaModifiers;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class ProductFamilySizesHooks {

    public void onBeforeRender(final ViewDefinitionState view) throws JSONException {
        JSONObject obj = view.getJsonContext();
        if (obj.has("window.mainTab.product.id")) {
            GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
            FilterValueHolder holder = grid.getFilterValue();
            holder.put(ProductFamilySizesCriteriaModifiers.L_PRODUCT_ID, obj.getLong("window.mainTab.product.id"));
            grid.setFilterValue(holder);
        }
    }

    public void addOnBeforeRender(final ViewDefinitionState view) throws JSONException {
        JSONObject obj = view.getJsonContext();
        if (obj.has("window.mainTab.product.productId")) {
            GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
            FilterValueHolder holder = grid.getFilterValue();
            holder.put(ProductFamilySizesCriteriaModifiers.L_PRODUCT_ID, obj.getLong("window.mainTab.product.productId"));
            grid.setFilterValue(holder);
        }
    }
}
