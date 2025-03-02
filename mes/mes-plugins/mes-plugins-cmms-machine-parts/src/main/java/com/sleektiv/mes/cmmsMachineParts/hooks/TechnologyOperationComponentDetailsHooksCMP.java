package com.sleektiv.mes.cmmsMachineParts.hooks;

import com.sleektiv.mes.technologies.constants.TechnologiesConstants;
import com.sleektiv.mes.technologies.constants.TechnologyFields;
import com.sleektiv.mes.technologies.constants.TechnologyOperationComponentFields;
import com.sleektiv.mes.technologies.states.constants.TechnologyState;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TechnologyOperationComponentDetailsHooksCMP {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity toc = form.getEntity();

        if (toc.getId() == null) {
            return;
        }

        toc = getTechnologyOperationComponentDD().get(toc.getId());

        Entity technology = toc.getBelongsToField(TechnologyOperationComponentFields.TECHNOLOGY);
        if (!TechnologyState.DRAFT.getStringValue().equals(technology.getStringField(TechnologyFields.STATE))) {
            GridComponent toolsGrid = (GridComponent) view.getComponentByReference("tools");
            toolsGrid.setEnabled(false);
        }
        }

    private DataDefinition getTechnologyOperationComponentDD() {
        return dataDefinitionService.get(TechnologiesConstants.PLUGIN_IDENTIFIER,
                TechnologiesConstants.MODEL_TECHNOLOGY_OPERATION_COMPONENT);
    }
}
