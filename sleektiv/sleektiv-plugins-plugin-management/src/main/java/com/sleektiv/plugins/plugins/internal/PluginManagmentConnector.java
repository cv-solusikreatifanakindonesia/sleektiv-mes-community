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

import com.sleektiv.plugin.api.*;
import com.sleektiv.plugin.api.artifact.PluginArtifact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PluginManagmentConnector {

    @Autowired
    private PluginManager pluginManager;

    @Autowired
    private PluginAccessor pluginAccessor;

    @Autowired
    private PluginServerManager pluginServerManager;

    public PluginOperationResult performInstall(final PluginArtifact artifact) {
        return pluginManager.installPlugin(artifact);
    }

    public PluginOperationResult performEnable(final List<String> pluginIdentifiers) {
        return pluginManager.enablePlugin(pluginIdentifiers.toArray(new String[pluginIdentifiers.size()]));
    }

    public PluginOperationResult performDisable(final List<String> pluginIdentifiers) {
        return pluginManager.disablePlugin(pluginIdentifiers.toArray(new String[pluginIdentifiers.size()]));
    }

    public PluginOperationResult performRemove(final List<String> pluginIdentifiers) {
        return pluginManager.uninstallPlugin(pluginIdentifiers.toArray(new String[pluginIdentifiers.size()]));
    }

    public PluginAdditionalData getPluginData(final String pluginIdentifier) {
        Plugin plugin = pluginAccessor.getPlugin(pluginIdentifier);
        return new PluginAdditionalData(plugin.getPluginInformation().getName(), plugin.getPluginInformation().getDescription(),
                plugin.getPluginInformation().getVendor(), plugin.getPluginInformation().getVendorUrl(), plugin.isSystemPlugin(),
                plugin.getPluginInformation().getGroup(), plugin.getPluginInformation().getLicense());
    }

    public void performRestart() {
        pluginServerManager.restart();
    }

}
