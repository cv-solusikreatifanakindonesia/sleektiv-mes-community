package com.sleektiv.mes.basic.hooks;

import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FaultTypeListHooks {



    public void onBeforeRender(final ViewDefinitionState view) {
        disableActionsWhenDefault(view);
    }

    private void disableActionsWhenDefault(final ViewDefinitionState view) {
        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
        RibbonGroup actions = window.getRibbon().getGroupByName("actions");

        RibbonActionItem copyButton = actions.getItemByName("copy");
        RibbonActionItem deleteButton = actions.getItemByName("delete");

        GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
        List<Entity> selectedFaults = grid.getSelectedEntities();

        for (Entity selectedFault : selectedFaults) {
            if (selectedFault.getBooleanField("isDefault")) {
                copyButton.setEnabled(false);
                deleteButton.setEnabled(false);
                copyButton.requestUpdate(true);
                deleteButton.requestUpdate(true);
                return;
            }
        }

        boolean enabled = !selectedFaults.isEmpty();

        copyButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
        copyButton.requestUpdate(true);
        deleteButton.requestUpdate(true);
    }

}
