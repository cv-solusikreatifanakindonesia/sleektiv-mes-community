package com.sleektiv.mes.orders.states;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.newstates.BasicStateService;
import com.sleektiv.mes.states.StateChangeEntityDescriber;

@Service
public class OperationalTaskStateService extends BasicStateService implements OperationalTasksServiceMarker {

    @Autowired
    private OperationalTaskStateChangeDescriber operationalTaskStateChangeDescriber;

    @Override
    public StateChangeEntityDescriber getChangeEntityDescriber() {
        return operationalTaskStateChangeDescriber;
    }
}
