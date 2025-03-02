/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv MES
 * Version: 1.4
 * <p>
 * This file is part of Sleektiv.
 * <p>
 * Sleektiv is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.sleektiv.mes.basic.hooks;

import com.sleektiv.mes.basic.constants.ParameterFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.security.api.SecurityService;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.Ribbon;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParametersHooks {

    private static final String L_COMPANY = "company";

    private static final String L_REDIRECT_TO_COMPANY = "redirectToCompany";

    @Autowired
    private SecurityService securityService;

    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent parametersForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        LookupComponent companyLookup = (LookupComponent) view.getComponentByReference(ParameterFields.COMPANY);

        boolean isSaved = (parametersForm.getEntityId() != null);
        boolean isCompany = (companyLookup.getEntity() != null);

        changeButtonsState(view, isSaved && isCompany);
        toggleEmailTab(view);
    }

    private void toggleEmailTab(ViewDefinitionState view) {
        ComponentState emailTab = view.getComponentByReference("emailTab");
        emailTab.setVisible(securityService.hasCurrentUserRole("ROLE_EMAIL_PARAMETERS"));
    }

    private void changeButtonsState(final ViewDefinitionState view, final boolean enabled) {
        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);

        Ribbon ribbon = window.getRibbon();

        RibbonGroup company = ribbon.getGroupByName(L_COMPANY);

        RibbonActionItem redirectToCompany = company.getItemByName(L_REDIRECT_TO_COMPANY);

        redirectToCompany.setEnabled(enabled);
        redirectToCompany.requestUpdate(true);

        window.requestRibbonRender();
    }

    public void onLicenseBeforeRender(final ViewDefinitionState view) {
        FieldComponent typeTerminalLicenses = (FieldComponent) view.getComponentByReference(ParameterFields.TYPE_TERMINAL_LICENSES);
        FormComponent parametersForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity parameter = parametersForm.getPersistedEntityWithIncludedFormValues();

        typeTerminalLicenses.setEnabled(securityService.hasCurrentUserRole("ROLE_SUPERADMIN")
                && parameter.getField(ParameterFields.NUMBER_TERMINAL_LICENSES) != null
                && NumberUtils.isDigits(parameter.getField(ParameterFields.NUMBER_TERMINAL_LICENSES).toString())
                && parameter.getLongField(ParameterFields.NUMBER_TERMINAL_LICENSES) > 0);
    }

}
