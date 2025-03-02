package com.sleektiv.mes.basic.hooks;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.NumberPatternFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class NumberPatternDetailsHooks {

    



    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity numberPattern = form.getPersistedEntityWithIncludedFormValues();
        if (numberPattern.getBooleanField(NumberPatternFields.USED)) {
            form.setFormEnabled(false);
            GridComponent grid = (GridComponent) view.getComponentByReference("numberPatternElements");
            grid.setEditable(false);
            WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
            RibbonGroup actions = window.getRibbon().getGroupByName("actions");

            RibbonActionItem save = actions.getItemByName("save");
            save.setEnabled(false);
            save.requestUpdate(true);
            RibbonActionItem saveBack = actions.getItemByName("saveBack");
            saveBack.setEnabled(false);
            saveBack.requestUpdate(true);
            RibbonActionItem saveNew = actions.getItemByName("saveNew");
            saveNew.setEnabled(false);
            saveNew.requestUpdate(true);
            window.requestRibbonRender();
        }
    }
}
