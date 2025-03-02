package com.sleektiv.mes.productFlowThruDivision.warehouseIssue.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.hooks.IssueDetailsHooks;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;

@Service
public class IssueDetailsListeners {

    @Autowired
    private IssueDetailsHooks issueDetailsHooks;

    public void onProductSelect(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        issueDetailsHooks.onBeforeRender(view);
    }


    public void onLocationSelect(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        issueDetailsHooks.onBeforeRender(view);
    }


}
