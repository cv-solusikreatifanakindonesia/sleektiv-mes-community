package com.sleektiv.mes.productFlowThruDivision.hooks;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.technologies.criteriaModifiers.TechnologyProductionLineCriteriaModifiers;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;

@Service
public class ProductionLineAddMultiHooks {

    public final void onBeforeRender(final ViewDefinitionState view) throws JSONException {
        JSONObject context = view.getJsonContext();

        GridComponent grid = (GridComponent) view.getComponentByReference("productionLineGrid");
        FilterValueHolder filter = grid.getFilterValue();
        filter.put(TechnologyProductionLineCriteriaModifiers.L_TECHNOLOGY_ID, context.getLong("window.mainTab.form.gridLayout.technologyId"));
        if (context.has("window.mainTab.form.gridLayout.divisionId")) {
            filter.put(TechnologyProductionLineCriteriaModifiers.L_DIVISION_ID, context.getLong("window.mainTab.form.gridLayout.divisionId"));
        } else if (filter.has(TechnologyProductionLineCriteriaModifiers.L_DIVISION_ID)) {
            filter.remove(TechnologyProductionLineCriteriaModifiers.L_DIVISION_ID);
        }
        grid.setFilterValue(filter);
    }

}
