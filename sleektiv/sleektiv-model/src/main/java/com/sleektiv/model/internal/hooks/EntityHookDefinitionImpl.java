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
package com.sleektiv.model.internal.hooks;

import org.springframework.context.ApplicationContext;

import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.internal.api.EntityHookDefinition;
import com.sleektiv.plugin.api.PluginUtils;

public final class EntityHookDefinitionImpl extends AbstractModelHookDefinition implements EntityHookDefinition {

    private boolean enabled = true;

    public EntityHookDefinitionImpl(final String className, final String methodName, final String pluginIdentifier,
            final ApplicationContext applicationContext) throws HookInitializationException {
        super(className, methodName, pluginIdentifier, applicationContext);
    }

    @Override
    public String getName() {
        return getBean().getClass().getCanonicalName().split("\\$\\$")[0] + "." + getMethod().getName();
    }

    @Override
    public boolean call(final Entity entity) {
        return call(getDataDefinition(), entity);
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return new Class[] { DataDefinition.class, Entity.class };
    }

    @Override
    public boolean isEnabled() {
        return enabled && PluginUtils.isEnabledOrEnabling(getPluginIdentifier());
    }

    @Override
    public void enable() {
        enabled = true;
    }

    @Override
    public void disable() {
        enabled = false;
    }

}
