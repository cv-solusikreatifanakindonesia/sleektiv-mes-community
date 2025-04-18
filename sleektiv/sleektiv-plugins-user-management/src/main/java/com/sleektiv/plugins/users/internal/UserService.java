/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv Framework
 * Version: 1.4
 *
 * This file is part of Sleektiv.
 *
 * Sleektiv is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.sleektiv.plugins.users.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.security.api.SecurityService;
import com.sleektiv.security.constants.GroupFields;
import com.sleektiv.security.constants.PermissionType;
import com.sleektiv.security.constants.SleektivSecurityConstants;
import com.sleektiv.security.constants.UserFields;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public final class UserService {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private TranslationService translationService;

    public void setPasswordAndOldPasswordAdRequired(final ViewDefinitionState state) {
        FieldComponent viewIdentifier = (FieldComponent) state.getComponentByReference("viewIdentifierHiddenInput");
        FieldComponent oldPassword = (FieldComponent) state.getComponentByReference("oldPasswordTextInput");
        FieldComponent password = (FieldComponent) state.getComponentByReference("passwordTextInput");
        FieldComponent passwordConfirmation = (FieldComponent) state.getComponentByReference("passwordConfirmationTextInput");

        oldPassword.setRequired(true);
        password.setRequired(true);
        passwordConfirmation.setRequired(true);
        viewIdentifier.setFieldValue("profileChangePassword");
    }

    public void setPasswordAsRequired(final ViewDefinitionState state) {
        FieldComponent viewIdentifier = (FieldComponent) state.getComponentByReference("viewIdentifierHiddenInput");
        FieldComponent password = (FieldComponent) state.getComponentByReference("passwordTextInput");
        FieldComponent passwordConfirmation = (FieldComponent) state.getComponentByReference("passwordConfirmationTextInput");

        password.setRequired(true);
        passwordConfirmation.setRequired(true);
        viewIdentifier.setFieldValue("userChangePassword");
    }

    public void hidePasswordOnUpdateForm(final ViewDefinitionState state) {
        FormComponent form = (FormComponent) state.getComponentByReference(SleektivViewConstants.L_FORM);
        FieldComponent password = (FieldComponent) state.getComponentByReference("passwordTextInput");
        FieldComponent passwordConfirmation = (FieldComponent) state.getComponentByReference("passwordConfirmationTextInput");
        ComponentState changePasswordButton = state.getComponentByReference("changePasswordButton");

        password.setRequired(true);
        passwordConfirmation.setRequired(true);

        if (form.getEntityId() == null) {
            password.setVisible(true);
            passwordConfirmation.setVisible(true);
            changePasswordButton.setVisible(false);
        } else {
            password.setVisible(false);
            passwordConfirmation.setVisible(false);
            changePasswordButton.setVisible(true);
        }
    }

    public void disableFormForAdmin(final ViewDefinitionState state) {
        FormComponent form = (FormComponent) state.getComponentByReference(SleektivViewConstants.L_FORM);

        Entity loggedUser = dataDefinitionService.get(SleektivSecurityConstants.PLUGIN_IDENTIFIER,
                SleektivSecurityConstants.MODEL_USER).get(securityService.getCurrentUserId());

        if (!securityService.hasRole(loggedUser, "ROLE_USERS_EDIT")) {
            form.setFormEnabled(false);
        }
    }

    public void disableFormForSuperadmin(final ViewDefinitionState state) {
        FormComponent form = (FormComponent) state.getComponentByReference(SleektivViewConstants.L_FORM);

        Long viewedUserId = form.getEntityId();
        if (viewedUserId != null) {
            Entity viewedUser = dataDefinitionService.get(SleektivSecurityConstants.PLUGIN_IDENTIFIER,
                    SleektivSecurityConstants.MODEL_USER).get(viewedUserId);
            Entity loggedUser = dataDefinitionService.get(SleektivSecurityConstants.PLUGIN_IDENTIFIER,
                    SleektivSecurityConstants.MODEL_USER).get(securityService.getCurrentUserId());

            if (!securityService.hasRole(loggedUser, "ROLE_SUPERADMIN") && (securityService.hasRole(viewedUser, "ROLE_SUPERADMIN")
                    || PermissionType.SUPER_ADMIN.getStringValue().equals(viewedUser.getBelongsToField(UserFields.GROUP).getStringField(GroupFields.PERMISSION_TYPE)))) {
                form.setFormEnabled(false);
            }
        }
    }

    public void showPermissionTypeForGroup(final ViewDefinitionState state) {
        FieldComponent groupPermissionType = (FieldComponent) state.getComponentByReference("groupPermissionType");
        LookupComponent groupField = (LookupComponent) state.getComponentByReference("groupLookup");

        Entity group = groupField.getEntity();
        if (group != null) {
            groupPermissionType.setFieldValue(translationService.translate("sleektivSecurity.group.permissionType.value." + group.getStringField(GroupFields.PERMISSION_TYPE),
                    state.getLocale()));
        } else {
            groupPermissionType.setFieldValue(null);
        }
    }

    public void setupRibbonForAdmins(final ViewDefinitionState state) {
        WindowComponent window = (WindowComponent) state.getComponentByReference(SleektivViewConstants.L_WINDOW);
        if (!securityService.hasCurrentUserRole("ROLE_USERS_EDIT") && securityService.hasCurrentUserRole("ROLE_ADMIN")) {
            RibbonGroup actions = window.getRibbon().getGroupByName("actions");
            for (RibbonActionItem actionItem : actions.getItems()) {
                actionItem.setEnabled(false);
                actionItem.requestUpdate(true);
            }
        }
    }

}
