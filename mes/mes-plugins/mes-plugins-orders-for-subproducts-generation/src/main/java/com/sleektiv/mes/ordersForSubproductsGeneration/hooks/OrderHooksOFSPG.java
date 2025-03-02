package com.sleektiv.mes.ordersForSubproductsGeneration.hooks;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.orders.states.constants.OrderState;
import com.sleektiv.mes.ordersForSubproductsGeneration.constants.OrderFieldsOFSPG;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

@Service
public class OrderHooksOFSPG {

    public boolean onDelete(final DataDefinition orderDD, final Entity order) {

        List<Entity> children = order.getHasManyField(OrderFieldsOFSPG.CHILDREN);
        for (Entity subOrder : children) {
            if (!canDeleteOrder(subOrder)) {
                order.addGlobalError("orders.order.onDelete.cannotDeleteSubOrders");
                return false;
            }
        }
        return true;
    }

    public boolean canDeleteOrder(final Entity subOrder) {
        String state = subOrder.getStringField(OrderFields.STATE);
        return state.equals(OrderState.PENDING.getStringValue()) || state.equals(OrderState.DECLINED.getStringValue());
    }
}
