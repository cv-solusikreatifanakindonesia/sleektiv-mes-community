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

import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.internal.api.EntityHookDefinition;
import com.sleektiv.model.internal.api.ErrorMessageDefinition;

public final class CustomEntityValidator implements EntityHookDefinition, ErrorMessageDefinition {

    private static final String CUSTOM_ERROR = "sleektivView.validate.global.error.custom";

    private final EntityHookDefinition entityHook;

    private String errorMessage = CUSTOM_ERROR;

    public CustomEntityValidator(final EntityHookDefinition entityHook) {
        this.entityHook = entityHook;
    }

    @Override
    public void initialize(final DataDefinition dataDefinition) {
        entityHook.initialize(dataDefinition);
    }

    @Override
    public boolean call(final Entity entity) {
        boolean result = entityHook.call(entity);

        if (result) {
            return true;
        }

        if (entity.isValid() || CUSTOM_ERROR.equals(errorMessage)) {
            entity.addGlobalError(errorMessage);
        }

        return false;
    }

    @Override
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String getName() {
        return entityHook.getName();
    }

    @Override
    public boolean isEnabled() {
        return entityHook.isEnabled();
    }

    @Override
    public void enable() {
        entityHook.enable();
    }

    @Override
    public void disable() {
        entityHook.disable();
    }

}
