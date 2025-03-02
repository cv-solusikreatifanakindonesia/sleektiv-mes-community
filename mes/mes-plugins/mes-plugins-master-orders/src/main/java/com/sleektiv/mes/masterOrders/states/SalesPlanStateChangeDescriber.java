package com.sleektiv.mes.masterOrders.states;

import com.sleektiv.mes.masterOrders.constants.MasterOrdersConstants;
import com.sleektiv.mes.masterOrders.constants.SalesPlanStateChangeFields;
import com.sleektiv.mes.masterOrders.states.constants.SalesPlanState;
import com.sleektiv.mes.states.AbstractStateChangeDescriber;
import com.sleektiv.mes.states.StateEnum;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SalesPlanStateChangeDescriber extends AbstractStateChangeDescriber {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Override
    public DataDefinition getDataDefinition() {
        return dataDefinitionService.get(MasterOrdersConstants.PLUGIN_IDENTIFIER, MasterOrdersConstants.MODEL_SALES_PLAN_STATE_CHANGE);
    }

    @Override
    public StateEnum parseStateEnum(String stringValue) {
        return SalesPlanState.parseString(stringValue);
    }

    @Override
    public String getOwnerFieldName() {
        return SalesPlanStateChangeFields.SALES_PLAN;
    }

    @Override
    public DataDefinition getOwnerDataDefinition() {
        return dataDefinitionService.get(MasterOrdersConstants.PLUGIN_IDENTIFIER, MasterOrdersConstants.MODEL_SALES_PLAN);
    }
}
