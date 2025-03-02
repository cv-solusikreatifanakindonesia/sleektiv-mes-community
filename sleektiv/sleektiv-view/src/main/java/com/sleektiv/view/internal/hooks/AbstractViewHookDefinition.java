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
package com.sleektiv.view.internal.hooks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.sleektiv.model.internal.hooks.AbstractHookDefinition;
import com.sleektiv.model.internal.hooks.HookInitializationException;
import com.sleektiv.plugin.api.PluginUtils;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.internal.ViewHookDefinition;

public abstract class AbstractViewHookDefinition extends AbstractHookDefinition implements ViewHookDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractViewHookDefinition.class);

    public AbstractViewHookDefinition(final String className, final String methodName, final String pluginIdentifier,
            final ApplicationContext applicationContext) throws HookInitializationException {
        super(className, methodName, pluginIdentifier, applicationContext);
    }

    @Override
    protected Object performCall(final Object... args) {
        if (getPluginIdentifier() != null && !PluginUtils.isEnabled(getPluginIdentifier())) {
            return null;
        }
        try {
            return super.performCall(args);
        } catch (Exception e) {

            LOG.warn("Failed to invoke view hook", e);

            if (args != null && args[0] != null && args[0] instanceof ViewDefinitionState) {
                ViewDefinitionState viewDefinitionState = (ViewDefinitionState) args[0];
                viewDefinitionState.addMessage("sleektivView.errorPage.error.internalError.explanation",
                        ComponentState.MessageType.FAILURE);
            }
        }
        return null;
    }

}
