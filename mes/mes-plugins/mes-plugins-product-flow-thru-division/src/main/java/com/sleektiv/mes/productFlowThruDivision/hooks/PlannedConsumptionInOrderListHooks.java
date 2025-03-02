package com.sleektiv.mes.productFlowThruDivision.hooks;

import com.sleektiv.mes.basic.constants.BasicConstants;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.productFlowThruDivision.criteriaModifiers.ProductsFlowInCriteriaModifiers;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlannedConsumptionInOrderListHooks {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void onBeforeRender(final ViewDefinitionState view) throws JSONException {

        JSONObject jsonObject = view.getJsonContext();

        GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
        FilterValueHolder gridProductsComponentInHolder = grid.getFilterValue();
        gridProductsComponentInHolder.put("productId", Integer.valueOf(jsonObject.getString("window.mainTab.product.id")));
        grid.setFilterValue(gridProductsComponentInHolder);
    }

}