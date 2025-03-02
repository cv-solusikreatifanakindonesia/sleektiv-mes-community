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
package com.sleektiv.plugins.customTranslations.internal.listeners;

import com.sleektiv.customTranslation.api.CustomTranslationManagementService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ComponentState.MessageType;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sleektiv.customTranslation.constants.CustomTranslationFields.*;

@Service
public class ReplaceCustomTranslationsListeners {

    

    private static final String L_REPLACE_TO = "replaceTo";

    private static final String L_REPLACE_FROM = "replaceFrom";

    @Autowired
    private CustomTranslationManagementService customTranslationManagementService;

    public void replaceCustomTranslations(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        FormComponent replaceCustomTranslationsFrom = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        FieldComponent localeField = (FieldComponent) view.getComponentByReference(LOCALE);
        FieldComponent replaceFromField = (FieldComponent) view.getComponentByReference(L_REPLACE_FROM);
        FieldComponent replaceToField = (FieldComponent) view.getComponentByReference(L_REPLACE_TO);

        String locale = (String) localeField.getFieldValue();
        String replaceFrom = (String) replaceFromField.getFieldValue();
        String replaceTo = (String) replaceToField.getFieldValue();

        if (StringUtils.isEmpty(replaceFrom) || StringUtils.isEmpty(replaceTo)) {
            replaceCustomTranslationsFrom.addMessage(
                    "sleektivCustomTranslations.replaceCustomTranslations.message.replaceCustomTranslationsFailure",
                    MessageType.FAILURE);
        } else {
            List<Entity> customTranslations = customTranslationManagementService.getCustomTranslations(locale);

            if (customTranslations.isEmpty()) {
                replaceCustomTranslationsFrom.addMessage(
                        "sleektivCustomTranslations.replaceCustomTranslations.message.replaceCustomTranslationsEmpty",
                        MessageType.INFO);
            } else {
                int count = 0;

                for (Entity customTranslation : customTranslations) {
                    String translation = customTranslation.getStringField(PROPERTIES_TRANSLATION);

                    if (translation.contains(replaceFrom)) {
                        count++;

                        translation = translation.replace(replaceFrom, replaceTo);

                        customTranslation.setField(CUSTOM_TRANSLATION, translation);

                        customTranslation.getDataDefinition().save(customTranslation);
                    }
                }

                if (count > 0) {
                    replaceCustomTranslationsFrom.addMessage(
                            "sleektivCustomTranslations.replaceCustomTranslations.message.replaceCustomTranslationsSuccess",
                            MessageType.SUCCESS);
                } else {
                    replaceCustomTranslationsFrom.addMessage(
                            "sleektivCustomTranslations.replaceCustomTranslations.message.replaceCustomTranslationsInfo",
                            MessageType.INFO);
                }
            }
        }
    }

}
