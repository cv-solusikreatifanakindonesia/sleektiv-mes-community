package com.sleektiv.mes.technologies.hooks;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.technologies.criteriaModifiers.TechnologicalProcessListDetailsCriteriaModifiers;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class AddProcessesHooks {

    private static final String L_TECHNOLOGICAL_PROCESSES = "technologicalProcesses";

    public void onBeforeRender(final ViewDefinitionState view) {
        Long technologicalProcessListId = ((FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM))
                .getEntityId();
        GridComponent technologicalProcesses = (GridComponent) view.getComponentByReference(L_TECHNOLOGICAL_PROCESSES);
        FilterValueHolder gridFilterValueHolder = technologicalProcesses.getFilterValue();

        gridFilterValueHolder.put(TechnologicalProcessListDetailsCriteriaModifiers.L_TECHNOLOGICAL_PROCESS_LIST_ID,
                technologicalProcessListId);

        technologicalProcesses.setFilterValue(gridFilterValueHolder);
    }
}
