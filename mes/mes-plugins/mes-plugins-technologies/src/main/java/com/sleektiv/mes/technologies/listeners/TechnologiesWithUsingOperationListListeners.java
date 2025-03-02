package com.sleektiv.mes.technologies.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.technologies.constants.TechnologyOperationComponentDtoFields;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class TechnologiesWithUsingOperationListListeners {

    public void changeParameters(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
        Set<Long> selectedEntities = grid.getSelectedEntities().stream()
                .map(e -> e.getIntegerField(TechnologyOperationComponentDtoFields.TECHNOLOGY_ID).longValue()).collect(Collectors.toSet());
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("selectedEntities", selectedEntities);
        JSONObject context = new JSONObject(parameters);
        String url = "../page/technologies/changeTechnologyParameters.html?context=" + context;

        view.openModal(url);
    }
}
