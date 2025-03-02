package com.sleektiv.mes.orders.hooks;

import com.sleektiv.mes.orders.services.WorkstationChangeoverService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.Ribbon;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class WorkstationChangeoverNormDetailsHooksO {

    private static final String L_ACTIONS = "actions";

    private static final String L_DELETE = "delete";

    @Autowired
    private WorkstationChangeoverService workstationChangeoverService;

    public void updateRibbonState(final ViewDefinitionState view) {
        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);

        Ribbon ribbon = window.getRibbon();
        RibbonGroup actionsRibbonGroup = ribbon.getGroupByName(L_ACTIONS);
        RibbonActionItem deleteRibbonActionItem = actionsRibbonGroup.getItemByName(L_DELETE);

        FormComponent workstationChangeoverNormForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Entity workstationChangeoverNorm = workstationChangeoverNormForm.getEntity();

        boolean isSaved = Objects.nonNull(workstationChangeoverNorm.getId());
        boolean isEnabled = isSaved && !workstationChangeoverService.hasWorkstationChangeoverForOperationalTasks(workstationChangeoverNorm);

        deleteRibbonActionItem.setEnabled(isEnabled);
        deleteRibbonActionItem.requestUpdate(true);
    }

}
