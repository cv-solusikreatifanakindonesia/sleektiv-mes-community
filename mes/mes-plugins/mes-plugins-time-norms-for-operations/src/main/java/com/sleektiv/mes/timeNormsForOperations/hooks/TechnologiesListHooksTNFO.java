package com.sleektiv.mes.timeNormsForOperations.hooks;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.technologies.constants.TechnologyFields;
import com.sleektiv.mes.technologies.states.constants.TechnologyState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class TechnologiesListHooksTNFO {

    public void toggleCopyTimeNormsFromOperationForTechnologiesButton(final ViewDefinitionState view) {
        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
        RibbonActionItem copyTimeNormsFromOperationForTechnologies = window.getRibbon().getGroupByName("norm")
                .getItemByName("copyTimeNormsFromOperationForTechnologies");

        GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

        copyTimeNormsFromOperationForTechnologies.setEnabled(!grid.getSelectedEntities().isEmpty() &&
                grid.getSelectedEntities().stream().allMatch(e -> e.getStringField(TechnologyFields.STATE).equals(TechnologyState.DRAFT.getStringValue())));

        copyTimeNormsFromOperationForTechnologies.requestUpdate(true);
    }
}
