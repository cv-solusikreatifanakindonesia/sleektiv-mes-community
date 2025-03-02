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
package com.sleektiv.plugins.plugins.internal;

import com.google.common.base.Preconditions;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.plugin.constants.SleektivPluginConstants;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Service
public class PluginManagmentViewHook {

    @Autowired
    private PluginManagmentPerformer pluginManagmentPerformer;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void onDownloadButtonClick(final ViewDefinitionState viewDefinitionState, final ComponentState triggerState,
            final String[] args) {
        viewDefinitionState.openModal("../pluginPages/downloadPage.html");
    }

    public void onEnableButtonClick(final ViewDefinitionState viewDefinitionState, final ComponentState triggerState,
            final String[] args) {
        viewDefinitionState.openModal(pluginManagmentPerformer.performEnable(getPluginIdentifiersFromView(viewDefinitionState)));
    }

    public void onDisableButtonClick(final ViewDefinitionState viewDefinitionState, final ComponentState triggerState,
            final String[] args) {
        viewDefinitionState.openModal(pluginManagmentPerformer.performDisable(getPluginIdentifiersFromView(viewDefinitionState)));
    }

    public void onRemoveButtonClick(final ViewDefinitionState viewDefinitionState, final ComponentState triggerState,
            final String[] args) {
        String url = pluginManagmentPerformer.performRemove(getPluginIdentifiersFromView(viewDefinitionState));

        if (url.contains("type=success")) {
            GridComponent grid = (GridComponent) viewDefinitionState.getComponentByReference(SleektivViewConstants.L_GRID);
            grid.setSelectedEntitiesIds(new HashSet<Long>());
        }
        viewDefinitionState.openModal(url);
    }

    private List<String> getPluginIdentifiersFromView(final ViewDefinitionState viewDefinitionState) {

        List<String> pluginIdentifiers = new LinkedList<String>();
        GridComponent grid = (GridComponent) viewDefinitionState.getComponentByReference(SleektivViewConstants.L_GRID);

        Preconditions.checkState(grid.getSelectedEntitiesIds().size() > 0, "No record selected");

        DataDefinition pluginDataDefinition = dataDefinitionService.get(SleektivPluginConstants.PLUGIN_IDENTIFIER,
                SleektivPluginConstants.MODEL_PLUGIN);
        for (Long entityId : grid.getSelectedEntitiesIds()) {
            Entity pluginEntity = pluginDataDefinition.get(entityId);

            pluginIdentifiers.add(pluginEntity.getStringField("identifier"));
        }

        return pluginIdentifiers;
    }

}
