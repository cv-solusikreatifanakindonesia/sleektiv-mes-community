package com.sleektiv.mes.orders.hooks;

import com.sleektiv.mes.orders.services.WorkstationChangeoverService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.Ribbon;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkstationChangeoverNormsListHooksO {

    private static final String L_ACTIONS = "actions";

    private static final String L_DELETE = "delete";

    @Autowired
    private WorkstationChangeoverService workstationChangeoverService;

    public void updateRibbonState(final ViewDefinitionState view) {
        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);

        Ribbon ribbon = window.getRibbon();
        RibbonGroup actionsRibbonGroup = ribbon.getGroupByName(L_ACTIONS);
        RibbonActionItem deleteRibbonActionItem = actionsRibbonGroup.getItemByName(L_DELETE);

        GridComponent workstationChangeoverNormsGrid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

        List<Entity> workstationChangeoverNorms = workstationChangeoverNormsGrid.getSelectedEntities();

        boolean areSelected = !workstationChangeoverNorms.isEmpty();
        boolean isEnabled = areSelected && workstationChangeoverNorms.stream().noneMatch(workstationChangeoverNorm -> workstationChangeoverService.hasWorkstationChangeoverForOperationalTasks(workstationChangeoverNorm));

        deleteRibbonActionItem.setEnabled(isEnabled);
        deleteRibbonActionItem.requestUpdate(true);
    }

}
