package com.sleektiv.mes.materialFlowResources.hooks;

import com.google.common.collect.Maps;
import com.sleektiv.security.api.UserService;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.CheckBoxComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WarehouseStockListHooks {

    @Autowired
    private UserService userService;

    public void fillDefaultFilters(final ViewDefinitionState view) {
        CheckBoxComponent initialized = (CheckBoxComponent) view.getComponentByReference("initialized");
        if (initialized.isChecked()) {
            return;
        }
        initialized.setChecked(true);
        GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

        Map<String, String> filters = Maps.newHashMap();
        filters.put("quantity", ">0");
        grid.setFilters(filters);
    }
}