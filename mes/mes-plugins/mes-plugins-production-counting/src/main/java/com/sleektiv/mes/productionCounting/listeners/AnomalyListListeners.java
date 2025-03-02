package com.sleektiv.mes.productionCounting.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.productionCounting.constants.AnomalyFields;
import com.sleektiv.mes.productionCounting.constants.ProductionCountingConstants;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class AnomalyListListeners {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void completeWithoutIssue(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent gridComponent = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
        for (Long anomalyId : gridComponent.getSelectedEntitiesIds()) {
            DataDefinition anomalyDD = getAnomalyDD();
            Entity anomaly = anomalyDD.get(anomalyId);

            anomaly.setField(AnomalyFields.STATE, AnomalyFields.State.COMPLETED);
            anomaly.setField(AnomalyFields.ISSUED, false);
            anomalyDD.save(anomaly);
        }
    }

    private DataDefinition getAnomalyDD() {
        return dataDefinitionService.get(ProductionCountingConstants.PLUGIN_IDENTIFIER, ProductionCountingConstants.MODEL_ANOMALY);
    }
}
