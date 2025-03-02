package com.sleektiv.mes.technologies.listeners;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.technologies.constants.TechnologicalProcessFields;
import com.sleektiv.mes.technologies.constants.TechnologiesConstants;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class AddProcessesListeners {

    private static final String L_TECHNOLOGICAL_PROCESSES = "technologicalProcesses";

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void addProcesses(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        GridComponent technologicalProcesses = (GridComponent) view.getComponentByReference(L_TECHNOLOGICAL_PROCESSES);
        Set<Long> selectedEntities = technologicalProcesses.getSelectedEntitiesIds();
        if (selectedEntities.isEmpty()) {
            view.addMessage("technologies.addProcesses.noSelectedProcesses", ComponentState.MessageType.INFO);

            return;
        }
        for (Long technologicalProcessId : selectedEntities) {
            Entity technologicalProcess = dataDefinitionService
                    .get(TechnologiesConstants.PLUGIN_IDENTIFIER, TechnologiesConstants.MODEL_TECHNOLOGICAL_PROCESS)
                    .get(technologicalProcessId);
            Entity technologicalProcessComponent = dataDefinitionService
                    .get(TechnologiesConstants.PLUGIN_IDENTIFIER, TechnologiesConstants.MODEL_TECHNOLOGICAL_PROCESS_COMPONENT)
                    .create();
            technologicalProcessComponent.setField(TechnologiesConstants.MODEL_TECHNOLOGICAL_PROCESS, technologicalProcess);
            technologicalProcessComponent.setField(TechnologiesConstants.MODEL_TECHNOLOGICAL_PROCESS_LIST, form.getEntityId());
            technologicalProcessComponent.setField(TechnologicalProcessFields.TPZ,
                    technologicalProcess.getField(TechnologicalProcessFields.TPZ));
            technologicalProcessComponent.setField(TechnologicalProcessFields.TJ,
                    technologicalProcess.getField(TechnologicalProcessFields.TJ));
            technologicalProcessComponent.setField(TechnologicalProcessFields.ADDITIONAL_TIME,
                    technologicalProcess.getField(TechnologicalProcessFields.ADDITIONAL_TIME));
            technologicalProcessComponent.setField(TechnologicalProcessFields.EXTENDED_TIME_FOR_SIZE_GROUP,
                    technologicalProcess.getField(TechnologicalProcessFields.EXTENDED_TIME_FOR_SIZE_GROUP));
            technologicalProcessComponent.setField(TechnologicalProcessFields.INCREASE_PERCENT,
                    technologicalProcess.getField(TechnologicalProcessFields.INCREASE_PERCENT));
            technologicalProcessComponent.setField(TechnologicalProcessFields.SIZE_GROUP,
                    technologicalProcess.getField(TechnologicalProcessFields.SIZE_GROUP));
            technologicalProcessComponent.getDataDefinition().save(technologicalProcessComponent);
        }
    }
}
