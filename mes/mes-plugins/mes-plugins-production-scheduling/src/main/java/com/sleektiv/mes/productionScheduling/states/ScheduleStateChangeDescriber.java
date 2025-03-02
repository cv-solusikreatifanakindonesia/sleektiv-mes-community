package com.sleektiv.mes.productionScheduling.states;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.mes.orders.constants.ScheduleStateChangeFields;
import com.sleektiv.mes.orders.states.constants.ScheduleState;
import com.sleektiv.mes.states.AbstractStateChangeDescriber;
import com.sleektiv.mes.states.StateEnum;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;

@Service
public class ScheduleStateChangeDescriber extends AbstractStateChangeDescriber {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Override
    public DataDefinition getDataDefinition() {
        return dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_SCHEDULE_STATE_CHANGE);
    }

    @Override
    public StateEnum parseStateEnum(String stringValue) {
        return ScheduleState.parseString(stringValue);
    }

    @Override
    public String getOwnerFieldName() {
        return ScheduleStateChangeFields.SCHEDULE;
    }

    @Override
    public DataDefinition getOwnerDataDefinition() {
        return dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_SCHEDULE);
    }
}
