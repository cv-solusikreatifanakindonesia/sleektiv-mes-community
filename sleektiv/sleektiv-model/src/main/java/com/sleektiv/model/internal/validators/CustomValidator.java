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
package com.sleektiv.model.internal.validators;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.FieldDefinition;
import com.sleektiv.model.internal.api.ErrorMessageDefinition;
import com.sleektiv.model.internal.api.FieldHookDefinition;

public final class CustomValidator implements FieldHookDefinition, ErrorMessageDefinition {

    private static final String CUSTOM_ERROR = "sleektivView.validate.field.error.custom";

    private final FieldHookDefinition fieldHook;

    private transient Integer hashCode = null;

    private String errorMessage = CUSTOM_ERROR;

    private FieldDefinition fieldDefinition;

    public CustomValidator(final FieldHookDefinition fieldHook) {
        this.fieldHook = fieldHook;
    }

    @Override
    public void initialize(final DataDefinition dataDefinition, final FieldDefinition fieldDefinition) {
        this.fieldDefinition = fieldDefinition;
        fieldHook.initialize(dataDefinition, fieldDefinition);
    }

    @Override
    public boolean call(final Entity entity, final Object oldValue, final Object newValue) {
        if (fieldHook.call(entity, oldValue, newValue)) {
            return true;
        }
        if (entity.getError(fieldDefinition.getName()) == null) {
            entity.addError(fieldDefinition, errorMessage);
        }
        return false;
    }

    @Override
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public int hashCode() {
        if (hashCode == null) {
            hashCode = new HashCodeBuilder(1, 31).append(fieldHook).toHashCode();
        }
        return hashCode;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CustomValidator other = (CustomValidator) obj;
        return new EqualsBuilder().append(fieldHook, other.fieldHook).append(errorMessage, other.errorMessage).isEquals();
    }

}
