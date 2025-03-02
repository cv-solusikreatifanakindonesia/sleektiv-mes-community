package com.sleektiv.mes.technologies.hooks;

import com.sleektiv.mes.technologies.criteriaModifiers.ProductTechnologiesCriteriaModifiers;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ProductTechnologiesListHooks {

    public void onBeforeRender(final ViewDefinitionState view) throws JSONException {
        JSONObject jsonContext = view.getJsonContext();
        jsonContext.length();

        Long productId = jsonContext.getLong("window.productId");

        if(Objects.nonNull(productId)) {

            GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

            FilterValueHolder filterValue = grid.getFilterValue();
            filterValue.put(ProductTechnologiesCriteriaModifiers.L_PRODUCT_ID, productId);
            grid.setFilterValue(filterValue);

        }

    }
}