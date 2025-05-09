/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv MES
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
package com.sleektiv.mes.materialFlow.hooks;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.mes.basic.constants.DashboardButtonFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.constants.MenuCategoryFields;
import com.sleektiv.view.constants.MenuItemFields;
import com.sleektiv.view.constants.SleektivViewConstants;
import com.sleektiv.view.constants.ViewFields;

@Component
public class DashboardButtonDetailsHooks {

    private static final String L_BASIC_DASHBOARD_BUTTON_IDENTIFIER = "basic.dashboardButton.identifier";

    private static final String L_DOT = ".";

    @Autowired
    private TranslationService translationService;

    public void onBeforeRender(final ViewDefinitionState view) {
        setIdentifier(view);
    }

    private void setIdentifier(final ViewDefinitionState view) {
        FormComponent dashboardButtonForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        FieldComponent identifierField = (FieldComponent) view.getComponentByReference(DashboardButtonFields.IDENTIFIER);
        LookupComponent itemLookup = (LookupComponent) view.getComponentByReference(DashboardButtonFields.ITEM);

        Entity dashboardButton = dashboardButtonForm.getEntity();
        Entity item = dashboardButton.getBelongsToField(DashboardButtonFields.ITEM);

        String identifier = StringUtils.EMPTY;

        if (Objects.nonNull(item)) {
            identifier = getItemTranslation(item);
        }

        identifierField.setFieldValue(identifier);
        identifierField.setRequired(true);
        identifierField.requestComponentUpdateState();

        itemLookup.setRequired(true);
        itemLookup.requestComponentUpdateState();
    }

    private String getItemTranslation(final Entity item) {
        Entity itemCategory = item.getBelongsToField(MenuItemFields.CATEGORY);
        Entity itemView = item.getBelongsToField(MenuItemFields.VIEW);

        StringBuilder translationKey = new StringBuilder(L_BASIC_DASHBOARD_BUTTON_IDENTIFIER);
        translationKey.append(L_DOT);
        translationKey.append(itemCategory.getStringField(MenuCategoryFields.NAME));
        translationKey.append(L_DOT);
        translationKey.append(itemView.getStringField(ViewFields.NAME));

        return translationService.translate(translationKey.toString(), LocaleContextHolder.getLocale());
    }

}
