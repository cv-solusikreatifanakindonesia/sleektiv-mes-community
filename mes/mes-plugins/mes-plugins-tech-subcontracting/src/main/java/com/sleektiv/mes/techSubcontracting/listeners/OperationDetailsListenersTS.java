package com.sleektiv.mes.techSubcontracting.listeners;

import com.sleektiv.mes.techSubcontracting.hooks.OperationDetailsHooksTS;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperationDetailsListenersTS {

    @Autowired
    private OperationDetailsHooksTS operationDetailsHooksTS;

    public final void onIsSubcontractingChange(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        operationDetailsHooksTS.setUnitCostField(view);
    }

}
