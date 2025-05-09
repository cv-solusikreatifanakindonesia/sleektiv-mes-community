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
package com.sleektiv.model.internal.types;

import java.util.Locale;
import java.util.Set;

import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.FieldDefinition;
import com.sleektiv.model.api.types.TreeType;
import com.sleektiv.model.internal.api.ValueAndError;

public final class TreeEntitiesType extends AbstractFieldType implements TreeType {

    private final String entityName;

    private final String joinFieldName;

    private final DataDefinitionService dataDefinitionService;

    private final String pluginIdentifier;

    private final Cascade cascade;

    // TODO MAKU reduce arguments length using parameter object
    public TreeEntitiesType(final String pluginIdentifier, final String entityName, final String joinFieldName,
            final Cascade cascade, final boolean copyable, final DataDefinitionService dataDefinitionService) {
        super(copyable);
        this.pluginIdentifier = pluginIdentifier;
        this.entityName = entityName;
        this.joinFieldName = joinFieldName;
        this.cascade = cascade;
        this.dataDefinitionService = dataDefinitionService;
    }

    @Override
    public Class<?> getType() {
        return Set.class;
    }

    @Override
    public ValueAndError toObject(final FieldDefinition fieldDefinition, final Object value) {
        return ValueAndError.empty();
    }

    @Override
    public String toString(final Object value, final Locale locale) {
        return null;
    }

    @Override
    public Object fromString(final String value, final Locale locale) {
        return null;
    }

    @Override
    public String getJoinFieldName() {
        return joinFieldName;
    }

    @Override
    public Cascade getCascade() {
        return cascade;
    }

    @Override
    public DataDefinition getDataDefinition() {
        return dataDefinitionService.get(pluginIdentifier, entityName);
    }

}
