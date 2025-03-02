package com.sleektiv.mes.technologies.hooks;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.technologies.criteriaModifiers.TechnologicalProcessListDetailsCriteriaModifiers;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class TechnologicalProcessListDetailsHooks {

    private static final String L_TECHNOLOGIES = "technologies";

    private static final String L_TECHNOLOGICAL_PROCESSES = "technologicalProcesses";

    private static final String L_ADD_PROCESSES = "addProcesses";

    public void onBeforeRender(final ViewDefinitionState view) {
        GridComponent technologies = (GridComponent) view.getComponentByReference(L_TECHNOLOGIES);

        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
        RibbonActionItem addProcesses = window.getRibbon().getGroupByName(L_TECHNOLOGICAL_PROCESSES)
                .getItemByName(L_ADD_PROCESSES);

        Long technologicalProcessListId = ((FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM))
                .getEntityId();

        if (Objects.nonNull(technologicalProcessListId)) {
            addProcesses.setEnabled(true);
            addProcesses.requestUpdate(true);
        }

        FilterValueHolder gridFilterValueHolder = technologies.getFilterValue();

        gridFilterValueHolder.put(TechnologicalProcessListDetailsCriteriaModifiers.L_TECHNOLOGICAL_PROCESS_LIST_ID,
                technologicalProcessListId);

        technologies.setFilterValue(gridFilterValueHolder);
    }

}
