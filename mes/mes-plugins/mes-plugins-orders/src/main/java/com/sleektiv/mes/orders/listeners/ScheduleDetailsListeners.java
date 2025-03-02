package com.sleektiv.mes.orders.listeners;

import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import org.springframework.stereotype.Service;

@Service
public class ScheduleDetailsListeners {

    public void informAboutGetOperations(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        view.addMessage("orders.schedule.orders.listChanged", ComponentState.MessageType.INFO);
    }
}
