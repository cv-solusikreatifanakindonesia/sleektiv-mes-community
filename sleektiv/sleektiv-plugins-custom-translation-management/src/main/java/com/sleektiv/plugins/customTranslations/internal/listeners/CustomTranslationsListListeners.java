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

import com.google.common.collect.Maps;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ComponentState.MessageType;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.sleektiv.customTranslation.constants.CustomTranslationFields.CUSTOM_TRANSLATION;

@Service
public class CustomTranslationsListListeners {

    private static final String L_WINDOW_ACTIVE_MENU = "window.activeMenu";

    public void cleanCustomTranslations(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent customTranslationsGrid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

        List<Entity> customTranslations = customTranslationsGrid.getSelectedEntities();

        if (!customTranslations.isEmpty()) {
            for (Entity customTranslation : customTranslations) {
                customTranslation.setField(CUSTOM_TRANSLATION, null);

                customTranslation.getDataDefinition().save(customTranslation);
            }

            customTranslationsGrid
                    .addMessage("sleektivCustomTranslations.customTranslationsList.message.cleanCustomTranslationsSuccess",
                            MessageType.SUCCESS);
        }
    }

    public void replaceCustomTranslations(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        Map<String, Object> parameters = Maps.newHashMap();

        parameters.put(L_WINDOW_ACTIVE_MENU, "administration.customTranslations");

        String url = "../page/sleektivCustomTranslations/replaceCustomTranslations.html";

        view.redirectTo(url, false, true, parameters);
    }

}
