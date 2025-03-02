package com.sleektiv.mes.orders.states;

import com.sleektiv.mes.newstates.BasicStateService;
import com.sleektiv.mes.states.StateChangeEntityDescriber;
import com.sleektiv.model.api.Entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderPackStateService extends BasicStateService implements OrderPackServiceMarker {


    @Autowired
    private OrderPackStateChangeDescriber salesPlanStateChangeDescriber;

    @Override
    public OrderPackStateChangeDescriber getChangeEntityDescriber() {
        return salesPlanStateChangeDescriber;
    }

    @Override
    public Entity onValidate(Entity orderPack, String sourceState, String targetState, Entity stateChangeEntity,
            StateChangeEntityDescriber describer) {

        return orderPack;
    }


}
