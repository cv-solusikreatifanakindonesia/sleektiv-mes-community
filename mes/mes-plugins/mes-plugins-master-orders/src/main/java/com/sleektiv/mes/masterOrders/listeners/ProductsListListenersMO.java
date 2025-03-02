package com.sleektiv.mes.masterOrders.listeners;

import com.google.common.collect.Maps;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class ProductsListListenersMO {

    public final void generateOrders(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent productsGrid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

        Set<Long> selectedEntities = productsGrid.getSelectedEntitiesIds();

        Map<String, Object> parameters = Maps.newHashMap();

        parameters.put("selectedEntities", selectedEntities);

        JSONObject context = new JSONObject(parameters);

        StringBuilder url = new StringBuilder("../page/masterOrders/ordersGenerationFromProducts.html");

        url.append("?context=");
        url.append(context);

        view.openModal(url.toString());
    }

    public final void outsourceProcessingComponent(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent productsGrid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

        Long selectedEntities = productsGrid.getSelectedEntitiesIds().stream().findFirst().orElseThrow(() -> new IllegalStateException("Missing product id!"));

        Map<String, Object> parameters = Maps.newHashMap();

        parameters.put("selectedEntity", selectedEntities);

        JSONObject context = new JSONObject(parameters);

        StringBuilder url = new StringBuilder("../page/masterOrders/outsourceProcessingComponent.html");
        url.append("?context=");
        url.append(context);

        view.openModal(url.toString());
    }

}
