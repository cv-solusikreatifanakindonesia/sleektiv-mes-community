package com.sleektiv.mes.masterOrders.listeners;

import com.sleektiv.mes.masterOrders.states.SalesPlanServiceMarker;
import com.sleektiv.mes.newstates.StateExecutorService;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SalesPlanListListeners {

    @Autowired
    private StateExecutorService stateExecutorService;

    public void changeState(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        stateExecutorService.changeState(SalesPlanServiceMarker.class, view, args);
    }
}
