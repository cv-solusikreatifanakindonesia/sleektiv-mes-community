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

import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.FieldDefinition;
import com.sleektiv.model.api.types.FieldType;
import com.sleektiv.model.internal.api.DefaultValidatorsProvider;
import com.sleektiv.model.internal.api.FieldHookDefinition;
import com.sleektiv.model.internal.api.InternalDataDefinition;
import com.sleektiv.model.internal.api.InternalFieldDefinition;
import com.sleektiv.model.internal.types.StringType;
import com.sleektiv.model.internal.types.TextType;
import com.sleektiv.model.internal.validators.RequiredValidator;
import com.sleektiv.model.internal.validators.UniqueValidator;
import com.sleektiv.plugin.internal.PluginUtilsService;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class FieldDefinitionImpl implements InternalFieldDefinition {

    private final String name;

    private FieldType type;

    private final List<FieldHookDefinition> validators = new ArrayList<FieldHookDefinition>();

    private boolean readOnly;

    private boolean required;

    private boolean unique;

    private boolean persistent = true;

    private Object defaultValue;

    private final DataDefinition dataDefinition;

    private String expression;

    private boolean enabled = true;

    private String pluginIdentifier;

    public FieldDefinitionImpl(final DataDefinition dataDefinition, final String name) {
        this.dataDefinition = dataDefinition;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue(final Object value, final Locale locale) {
        if (value == null) {
            return null;
        } else {
            return type.toString(value, locale);
        }
    }

    @Override
    public FieldType getType() {
        return type;
    }

    public FieldDefinitionImpl withType(final FieldType type) {
        this.type = type;
        return this;
    }

    public List<FieldHookDefinition> getValidators() {
        return validators;
    }

    @Override
    public DataDefinition getDataDefinition() {
        return dataDefinition;
    }

    public FieldDefinitionImpl withValidator(final FieldHookDefinition validator) {
        validator.initialize(dataDefinition, this);
        if (validator instanceof RequiredValidator) {
            required = true;
        }
        if (validator instanceof UniqueValidator) {
            unique = true;
        }
        this.validators.add(validator);
        return this;
    }

    public FieldDefinitionImpl withMissingDefaultValidators() {
        if (type instanceof DefaultValidatorsProvider) {
            DefaultValidatorsProvider resolver = (DefaultValidatorsProvider) type;
            for (FieldHookDefinition missingValidator : resolver.getMissingValidators(getValidators())) {
                withValidator(missingValidator);
            }
        }
        return this;
    }

    public FieldDefinitionImpl withReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }

    @Override
    public boolean isReadOnly() {
        return readOnly || expression != null;
    }

    @Override
    public boolean isRequired() {
        return required && expression == null;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    public FieldDefinition withDefaultValue(final Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    @Override
    public boolean isUnique() {
        return unique && expression == null;
    }

    public void setPersistent(final boolean persistent) {
        this.persistent = persistent;
    }

    @Override
    public boolean isPersistent() {
        return persistent && expression == null;
    }

    public void setExpression(final String expression) {
        this.expression = expression;
    }

    @Override
    public String getExpression() {
        return expression;
    }

    public String getPluginIdentifier() {
        return pluginIdentifier;
    }

    public void setPluginIdentifier(final String pluginIdentifier) {
        this.pluginIdentifier = pluginIdentifier;
    }

    @Override
    public boolean callValidators(final Entity entity, final Object oldValue, final Object newValue) {
        for (FieldHookDefinition hook : validators) {
            if (PluginUtilsService.isEnabled(pluginIdentifier) && !hook.call(entity, oldValue, newValue)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 31).append(defaultValue).append(name).append(required).append(type).append(unique)
                .append(validators).append(readOnly).append(expression).toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof FieldDefinitionImpl)) {
            return false;
        }
        FieldDefinitionImpl other = (FieldDefinitionImpl) obj;
        return new EqualsBuilder().append(defaultValue, other.defaultValue).append(name, other.name)
                .append(required, other.required).append(type, other.type).append(unique, other.unique)
                .append(validators, other.validators).append(readOnly, other.readOnly).append(expression, other.expression)
                .isEquals();
    }

    @Override
    public String toString() {
        return "FieldDefinition: " + name;
    }

    @Override
    public boolean isEnabled() {
        return enabled && ((InternalDataDefinition)dataDefinition).isEnabled();
    }

    @Override
    public void enable() {
        enabled = true;
    }

    @Override
    public void disable() {
        enabled = false;
    }

    @Override
    public boolean canBeBothCopyableAndUnique() {
        return type instanceof StringType || type instanceof TextType;
    }

}
