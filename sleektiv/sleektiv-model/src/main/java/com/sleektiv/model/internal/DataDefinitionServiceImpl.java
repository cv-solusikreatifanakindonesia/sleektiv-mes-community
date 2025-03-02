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
package com.sleektiv.model.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.aop.Monitorable;
import com.sleektiv.model.internal.api.InternalDataDefinitionService;

@Service
public final class DataDefinitionServiceImpl implements InternalDataDefinitionService {

    private final Map<String, DataDefinition> dataDefinitions = new HashMap<String, DataDefinition>();

    @Override
    @Monitorable
    public DataDefinition get(final String pluginIdentifier, final String modelName) {
        DataDefinition dataDefinition = dataDefinitions.get(pluginIdentifier + "." + modelName);
        checkNotNull(dataDefinition, "data definition for %s#%s cannot be found", pluginIdentifier, modelName);
        return dataDefinition;
    }

    @Override
    @Monitorable
    public List<DataDefinition> list() {
        return new ArrayList<DataDefinition>(dataDefinitions.values());
    }

    @Override
    @Monitorable
    public void save(final DataDefinition dataDefinition) {
        dataDefinitions.put(dataDefinition.getPluginIdentifier() + "." + dataDefinition.getName(), dataDefinition);
    }
}
