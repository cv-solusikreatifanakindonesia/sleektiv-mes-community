package com.sleektiv.mes.costCalculation.hooks;

import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

@Service
public class TechnologiesListHooksCC {

    public void toggleGenerateCostCalculationButton(final ViewDefinitionState view) {
        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
        RibbonActionItem generateCostCalculation = window.getRibbon().getGroupByName("costCalculation")
                .getItemByName("createCostCalculation");

        GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

        generateCostCalculation.setEnabled(!grid.getSelectedEntities().isEmpty());

        generateCostCalculation.requestUpdate(true);
    }
}
