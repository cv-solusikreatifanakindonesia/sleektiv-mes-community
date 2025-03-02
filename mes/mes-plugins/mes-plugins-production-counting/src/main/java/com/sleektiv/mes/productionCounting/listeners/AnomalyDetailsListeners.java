package com.sleektiv.mes.productionCounting.listeners;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.productionCounting.constants.AnomalyFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class AnomalyDetailsListeners {

    

    public void completeWithoutIssue(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Entity anomaly = form.getEntity();

        if (anomaly.getId() == null) {
            return;
        }

        anomaly.setField(AnomalyFields.STATE, AnomalyFields.State.COMPLETED);
        anomaly.setField(AnomalyFields.ISSUED, false);
        anomaly.getDataDefinition().save(anomaly);

        state.performEvent(view, "reset", new String[0]);
    }
}
