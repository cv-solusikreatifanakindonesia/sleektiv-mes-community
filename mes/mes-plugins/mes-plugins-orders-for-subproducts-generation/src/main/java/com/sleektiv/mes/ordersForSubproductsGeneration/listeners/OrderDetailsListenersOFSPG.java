package com.sleektiv.mes.ordersForSubproductsGeneration.listeners;

import com.google.common.collect.Maps;
import com.sleektiv.mes.ordersForSubproductsGeneration.constants.OrdersForSubproductsGenerationConstans;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OrderDetailsListenersOFSPG {

    

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void redirectToOrdersForSubproducts(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        FormComponent orderForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity orderEntity = orderForm.getPersistedEntityWithIncludedFormValues();

        Entity ordersForSubproducts = dataDefinitionService.get(OrdersForSubproductsGenerationConstans.PLUGIN_IDENTIFIER, "subOrders").create();
        ordersForSubproducts.setField("order", orderEntity);
        ordersForSubproducts = ordersForSubproducts.getDataDefinition().save(ordersForSubproducts);
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("form.id", ordersForSubproducts.getId());

        String url = "/page/ordersForSubproductsGeneration/ordersForSubproducts.html";
        view.redirectTo(url, false, true, parameters);

    }

    public void redirectToOrdersForSubproductsGroup(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        FormComponent orderForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity orderEntity = orderForm.getPersistedEntityWithIncludedFormValues();

        Entity ordersForSubproducts = dataDefinitionService.get(OrdersForSubproductsGenerationConstans.PLUGIN_IDENTIFIER, "subOrders").create();
        ordersForSubproducts.setField("ordersGroup", orderEntity);
        ordersForSubproducts = ordersForSubproducts.getDataDefinition().save(ordersForSubproducts);
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("form.id", ordersForSubproducts.getId());

        String url = "/page/ordersForSubproductsGeneration/ordersForSubproducts.html";
        view.redirectTo(url, false, true, parameters);

    }
}
