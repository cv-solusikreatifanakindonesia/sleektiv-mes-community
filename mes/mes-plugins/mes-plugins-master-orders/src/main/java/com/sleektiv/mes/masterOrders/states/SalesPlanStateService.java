package com.sleektiv.mes.masterOrders.states;

import com.sleektiv.mes.masterOrders.constants.MasterOrderFields;
import com.sleektiv.mes.masterOrders.constants.SalesPlanFields;
import com.sleektiv.mes.masterOrders.states.constants.SalesPlanStateStringValues;
import com.sleektiv.mes.newstates.BasicStateService;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.states.StateChangeEntityDescriber;
import com.sleektiv.model.api.Entity;
import com.sleektiv.plugin.api.PluginUtils;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SalesPlanStateService extends BasicStateService implements SalesPlanServiceMarker {

    public static final String L_ORDERS_GROUPS_PLUGIN = "ordersGroups";

    public static final String L_ORDERS_GROUPS = "ordersGroups";

    public static final String L_NUMBER = "number";

    @Autowired
    private SalesPlanStateChangeDescriber salesPlanStateChangeDescriber;

    @Override
    public SalesPlanStateChangeDescriber getChangeEntityDescriber() {
        return salesPlanStateChangeDescriber;
    }

    @Override
    public Entity onValidate(Entity salesPlan, String sourceState, String targetState, Entity stateChangeEntity,
            StateChangeEntityDescriber describer) {
        switch (targetState) {
            case SalesPlanStateStringValues.REJECTED:
                checkIfIsUsed(salesPlan);
                break;
        }

        return salesPlan;
    }

    private void checkIfIsUsed(Entity salesPlan) {

        if (!salesPlan.getHasManyField(SalesPlanFields.MASTER_ORDERS).isEmpty()) {
            salesPlan.addGlobalError("masterOrders.salesPlan.status.changeToRejected.salesPlanHasAssignedMasterOrders", false,
                    salesPlan.getStringField(SalesPlanFields.NUMBER), salesPlan.getHasManyField(SalesPlanFields.MASTER_ORDERS)
                            .stream().map(mo -> mo.getStringField(MasterOrderFields.NUMBER)).collect(Collectors.joining(", ")));
        }

        if (!salesPlan.getHasManyField(SalesPlanFields.ORDERS).isEmpty()) {
            salesPlan.addGlobalError("masterOrders.salesPlan.status.changeToRejected.salesPlanHasAssignedOrders", false,
                    salesPlan.getStringField(SalesPlanFields.NUMBER), salesPlan.getHasManyField(SalesPlanFields.ORDERS).stream()
                            .map(mo -> mo.getStringField(OrderFields.NUMBER)).collect(Collectors.joining(", ")));
        }

        if (PluginUtils.isEnabled(L_ORDERS_GROUPS_PLUGIN)) {
            if (!salesPlan.getHasManyField(L_ORDERS_GROUPS).isEmpty()) {
                salesPlan.addGlobalError("masterOrders.salesPlan.status.changeToRejected.salesPlanHasAssignedOrdersGroups",
                        false, salesPlan.getStringField(SalesPlanFields.NUMBER), salesPlan.getHasManyField(L_ORDERS_GROUPS)
                                .stream().map(mo -> mo.getStringField(L_NUMBER)).collect(Collectors.joining(", ")));
            }
        }

    }

}
