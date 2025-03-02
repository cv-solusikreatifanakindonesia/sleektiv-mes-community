package com.sleektiv.mes.productionCounting.hooks;

import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProductionTrackingsDraftListHooks {

    public void onBeforeRender(final ViewDefinitionState view) {
        GridComponent gridComponent = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
        Map<String, String> filters = gridComponent.getFilters();
        filters.put("state", "01draft");
        gridComponent.setFilters(filters);
    }
}
