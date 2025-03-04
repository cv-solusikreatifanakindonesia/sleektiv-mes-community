package com.sleektiv.mes.technologies.listeners;

import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import org.springframework.stereotype.Service;

@Service
public class WorkstationChangeoverNormsListListeners {

    public void openWorkstationChangeoverNormsImportPage(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        StringBuilder url = new StringBuilder("../page/technologies/workstationChangeoverNormsImport.html");

        view.openModal(url.toString());
    }

}
