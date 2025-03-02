package com.sleektiv.mes.productFlowThruDivision.hooks;

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.orders.states.constants.OrderStateStringValues;
import com.sleektiv.mes.productionCounting.constants.ParameterFieldsPC;
import com.sleektiv.mes.productionCounting.constants.ReleaseOfMaterials;
import com.sleektiv.view.api.ViewDefinitionState;

import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.Ribbon;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DetailedProductionCountingAndProgressListHooksBPC {

    public static final String L_ORDER_FORM = "order";

    public static final String L_ISSUE = "issue";

    public static final String L_RESOURCE_ISSUE = "resourceIssue";

    @Autowired
    private ParameterService parameterService;

    public void onBeforeRender(final ViewDefinitionState view) {
        String releaseOfMaterials = parameterService.getParameter().getStringField(ParameterFieldsPC.RELEASE_OF_MATERIALS);
        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
        Ribbon ribbon = window.getRibbon();
        RibbonGroup issueRibbonGroup = ribbon.getGroupByName(L_ISSUE);
        RibbonActionItem resourceIssueRibbonActionItem = issueRibbonGroup.getItemByName(L_RESOURCE_ISSUE);
        if (!ReleaseOfMaterials.MANUALLY_TO_ORDER_OR_GROUP.getStringValue().equals(releaseOfMaterials)) {
            resourceIssueRibbonActionItem.setEnabled(false);
            resourceIssueRibbonActionItem
                    .setMessage("basicProductionCounting.detailedProductionCountingAndProgressList.resourceIssue.description");
            resourceIssueRibbonActionItem.requestUpdate(true);
        }
    }

}
