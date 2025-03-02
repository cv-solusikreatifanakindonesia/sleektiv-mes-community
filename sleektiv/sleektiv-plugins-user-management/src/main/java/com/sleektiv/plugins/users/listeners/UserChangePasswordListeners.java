package com.sleektiv.plugins.users.listeners;

import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.CheckBoxComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

@Service
public class UserChangePasswordListeners {

    private static final String L_CHANGED = "changed";

    public void changePassword(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        FormComponent userForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        CheckBoxComponent changedCheckBox = (CheckBoxComponent) view.getComponentByReference(L_CHANGED);

        userForm.performEvent(view, "save");

        if (!userForm.isHasError()) {
            changedCheckBox.setChecked(true);
        }
    }

}
