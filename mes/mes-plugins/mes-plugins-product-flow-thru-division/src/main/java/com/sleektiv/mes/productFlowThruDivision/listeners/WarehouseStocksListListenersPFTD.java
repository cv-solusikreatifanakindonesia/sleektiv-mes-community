package com.sleektiv.mes.productFlowThruDivision.listeners;

import com.google.common.collect.Maps;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WarehouseStocksListListenersPFTD {

    public static final String L_ORDERS = "orders";
    public static final String L_SHOW_PLANNED_CONSUMPTION_IN_ORDERS = "showPlannedConsumptionInOrders";

    public void showPlannedConsumptionInOrders(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

        Entity record = grid.getSelectedEntities().get(0);
        Integer productId = record.getIntegerField("product_id");

        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("product.id", productId);

        String url = "/page/productFlowThruDivision/plannedConsumptionInOrderList.html";
        view.redirectTo(url, false, true, parameters);
    }

    public void onBeforeRender(final ViewDefinitionState view) {
        GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);

        RibbonGroup actionsRibbonGroup = window.getRibbon().getGroupByName(L_ORDERS);
        RibbonActionItem showPlannedConsumptionInOrdersItem = actionsRibbonGroup.getItemByName(L_SHOW_PLANNED_CONSUMPTION_IN_ORDERS);
        showPlannedConsumptionInOrdersItem.setMessage("productFlowThruDivision.plannedConsumptionInOrder.description");

        showPlannedConsumptionInOrdersItem.setEnabled(grid.getSelectedEntitiesIds().size() == 1);
        showPlannedConsumptionInOrdersItem.requestUpdate(true);
    }

}