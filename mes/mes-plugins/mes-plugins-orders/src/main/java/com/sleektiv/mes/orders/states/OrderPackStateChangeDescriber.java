package com.sleektiv.mes.orders.states;

import com.sleektiv.mes.orders.constants.OrderPackSateChangeFields;
import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.mes.orders.states.constants.OrderPackState;
import com.sleektiv.mes.states.AbstractStateChangeDescriber;
import com.sleektiv.mes.states.StateEnum;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderPackStateChangeDescriber extends AbstractStateChangeDescriber {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Override
    public DataDefinition getDataDefinition() {
        return dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_ORDER_PACK_STATE_CHANGE);
    }

    @Override
    public StateEnum parseStateEnum(String stringValue) {
        return OrderPackState.parseString(stringValue);
    }

    @Override
    public String getOwnerFieldName() {
        return OrderPackSateChangeFields.ORDER_PACK;
    }

    @Override
    public DataDefinition getOwnerDataDefinition() {
        return dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_ORDER_PACK);
    }
}
