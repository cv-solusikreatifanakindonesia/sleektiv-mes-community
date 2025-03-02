package com.sleektiv.mes.cmmsMachineParts.listeners;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.cmmsMachineParts.constants.CmmsMachinePartsConstants;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;

@Service
public class WorkTimeForUserListeners {

    public void showWorkTime(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        view.openModal(CmmsMachinePartsConstants.PLUGIN_IDENTIFIER + "/workTimeForUserList.html");
    }
}
