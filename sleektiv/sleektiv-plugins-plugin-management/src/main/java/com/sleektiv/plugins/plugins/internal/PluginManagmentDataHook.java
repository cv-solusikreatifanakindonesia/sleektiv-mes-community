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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

@Service
public class PluginManagmentDataHook {

    @Autowired
    private PluginManagmentConnector pluginManagmentConnector;

    public void updatePluginData(final DataDefinition dataDefinition, final Entity entity) {

        String pluginIdentifier = entity.getStringField("identifier");

        PluginAdditionalData pluginAdditionalData = pluginManagmentConnector.getPluginData(pluginIdentifier);

        entity.setField("name", pluginAdditionalData.getName());
        entity.setField("description", pluginAdditionalData.getDescription());
        entity.setField("vendor", pluginAdditionalData.getVendor());
        entity.setField("vendorUrl", pluginAdditionalData.getVendorUrl());
    }

}
