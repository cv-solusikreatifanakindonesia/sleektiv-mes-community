package com.sleektiv.mes.orders.hooks;

import com.sleektiv.mes.orders.constants.WorkstationChangeoverForOperationalTaskChangeoverType;
import com.sleektiv.mes.orders.constants.WorkstationChangeoverForOperationalTaskDtoFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.Ribbon;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkstationChangeoverForOperationalTasksListHooks {

    private static final String L_ACTIONS = "actions";

    private static final String L_COPY = "copy";

    public void onBeforeRender(final ViewDefinitionState view) {
        setRibbonState(view);
    }

    private void setRibbonState(final ViewDefinitionState view) {
        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);

        Ribbon ribbon = window.getRibbon();
        RibbonGroup actionsRibbonGroup = ribbon.getGroupByName(L_ACTIONS);
        RibbonActionItem copyRibbonActionItem = actionsRibbonGroup.getItemByName(L_COPY);

        GridComponent workstationChangeoverForOperationalTasksGrid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

        List<Entity> workstationChangeoverForOperationalTasks = workstationChangeoverForOperationalTasksGrid.getSelectedEntities();

        boolean areSelected = !workstationChangeoverForOperationalTasks.isEmpty();
        boolean areOwn = workstationChangeoverForOperationalTasks.stream()
                .allMatch(workstationChangeoverForOperationalTaskDto ->
                        WorkstationChangeoverForOperationalTaskChangeoverType.OWN.getStringValue()
                                .equals(workstationChangeoverForOperationalTaskDto.getStringField(WorkstationChangeoverForOperationalTaskDtoFields.CHANGEOVER_TYPE)));

        copyRibbonActionItem.setEnabled(areSelected && areOwn);
        copyRibbonActionItem.requestUpdate(true);
    }

}
