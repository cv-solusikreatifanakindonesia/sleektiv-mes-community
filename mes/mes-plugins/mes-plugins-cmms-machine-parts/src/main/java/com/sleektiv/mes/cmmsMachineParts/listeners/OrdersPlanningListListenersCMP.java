package com.sleektiv.mes.cmmsMachineParts.listeners;

import com.google.common.collect.Maps;
import com.sleektiv.mes.cmmsMachineParts.constants.CmmsMachinePartsConstants;
import com.sleektiv.mes.cmmsMachineParts.constants.OrdersToolRequirementFields;
import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrdersPlanningListListenersCMP {

    private static final String L_WINDOW_ACTIVE_MENU = "window.activeMenu";

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void createOrdersToolRequirement(final ViewDefinitionState view, final ComponentState state,
                                            final String[] args) {
        GridComponent ordersGrid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

        List<Entity> selectedEntities = ordersGrid.getSelectedEntities();

        if (!selectedEntities.isEmpty()) {
            DataDefinition orderDD = getOrderDD();

            List<Entity> orders = selectedEntities.stream().map(orderPlanningListDto -> orderDD.get(orderPlanningListDto.getId()))
                    .collect(Collectors.toList());

            Entity ordersToolRequirement = createOrdersToolRequirement(orders);

            Long ordersToolRequirementId = ordersToolRequirement.getId();

            if (Objects.nonNull(ordersToolRequirementId)) {
                Map<String, Object> parameters = Maps.newHashMap();
                parameters.put("form.id", ordersToolRequirementId);

                parameters.put(L_WINDOW_ACTIVE_MENU, "cmmsMachineParts.ordersToolRequirementsList");

                String url = "../page/cmmsMachineParts/ordersToolRequirementDetails.html";
                view.redirectTo(url, false, true, parameters);
            }
        }
    }

    private Entity createOrdersToolRequirement(final List<Entity> orders) {
        Entity ordersToolRequirement = getOrdersToolRequirementDD().create();

        ordersToolRequirement.setField(OrdersToolRequirementFields.ORDERS, orders);

        ordersToolRequirement = ordersToolRequirement.getDataDefinition().save(ordersToolRequirement);

        return ordersToolRequirement;
    }

    private DataDefinition getOrderDD() {
        return dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_ORDER);
    }

    private DataDefinition getOrdersToolRequirementDD() {
        return dataDefinitionService.get(CmmsMachinePartsConstants.PLUGIN_IDENTIFIER, CmmsMachinePartsConstants.MODEL_ORDERS_TOOL_REQUIREMENT);
    }

}
