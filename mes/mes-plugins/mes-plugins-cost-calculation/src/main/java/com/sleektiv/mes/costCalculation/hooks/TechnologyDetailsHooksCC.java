package com.sleektiv.mes.costCalculation.hooks;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.costCalculation.constants.TechnologyFieldsCC;
import com.sleektiv.mes.technologies.constants.TechnologyFields;
import com.sleektiv.mes.technologies.states.constants.TechnologyState;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class TechnologyDetailsHooksCC {

    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent technologyForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        GridComponent grid = (GridComponent) view.getComponentByReference(TechnologyFieldsCC.ADDITIONAL_DIRECT_COSTS);
        Entity technology = technologyForm.getEntity();

        String state = technology.getStringField(TechnologyFields.STATE);

        grid.setEnabled(TechnologyState.DRAFT.getStringValue().equals(state) || TechnologyState.CHECKED.getStringValue().equals(state)
                || TechnologyState.ACCEPTED.getStringValue().equals(state));
    }
}
