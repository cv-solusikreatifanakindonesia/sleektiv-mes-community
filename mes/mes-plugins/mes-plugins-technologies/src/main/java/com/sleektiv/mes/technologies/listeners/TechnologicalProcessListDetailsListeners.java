package com.sleektiv.mes.technologies.listeners;

import com.google.common.collect.Maps;
import com.sleektiv.mes.technologies.constants.TechnologiesConstants;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

import static com.sleektiv.mes.technologies.constants.TechnologicalProcessListFields.TECHNOLOGICAL_PROCESS_COMPONENTS;

@Service
public class TechnologicalProcessListDetailsListeners {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void addProcesses(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        FormComponent formComponent = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Map<String, Object> parameters = Maps.newHashMap();

        parameters.put("form.id", formComponent.getEntityId());

        String url = "../page/technologies/addProcesses.html";
        view.openModal(url, parameters);
    }

    public void onRemoveSelectedEntities(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent technologicalProcessComponents = (GridComponent) view
                .getComponentByReference(TECHNOLOGICAL_PROCESS_COMPONENTS);
        Set<Long> selectedComponents = technologicalProcessComponents.getSelectedEntitiesIds();
        DataDefinition dataDefinition = dataDefinitionService.get(TechnologiesConstants.PLUGIN_IDENTIFIER,
                TechnologiesConstants.MODEL_TECHNOLOGICAL_PROCESS_COMPONENT);
        dataDefinition.delete(selectedComponents.toArray(new Long[0]));
    }
}
