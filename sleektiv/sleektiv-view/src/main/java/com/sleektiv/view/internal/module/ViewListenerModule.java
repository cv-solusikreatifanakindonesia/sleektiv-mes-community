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
package com.sleektiv.view.internal.module;

import com.google.common.base.Preconditions;
import com.sleektiv.plugin.api.Module;
import com.sleektiv.plugin.api.ModuleException;
import com.sleektiv.view.internal.api.ComponentPattern;
import com.sleektiv.view.internal.api.InternalViewDefinition;
import com.sleektiv.view.internal.api.InternalViewDefinitionService;
import com.sleektiv.view.internal.hooks.ViewEventListenerHook;

public class ViewListenerModule extends Module {

    private final InternalViewDefinitionService viewDefinitionService;

    private final String extendsViewPlugin;

    private final String extendsViewName;

    private final String extendsComponentName;

    private final ViewEventListenerHook eventListenerHook;

    private final String pluginIdentifier;

    public ViewListenerModule(final String pluginIdentifier, final InternalViewDefinitionService viewDefinitionService,
            final String extendsViewPlugin, final String extendsViewName, final String extendsComponentName,
            final ViewEventListenerHook eventListenerHook) {
        super();

        this.pluginIdentifier = pluginIdentifier;
        this.viewDefinitionService = viewDefinitionService;
        this.extendsViewPlugin = extendsViewPlugin;
        this.extendsViewName = extendsViewName;
        this.extendsComponentName = extendsComponentName;
        this.eventListenerHook = eventListenerHook;
    }

    @Override
    public void enableOnStartup() {
        enable();
    }

    @Override
    public void enable() {
        try {
            getComponent().addCustomEvent(eventListenerHook);
        } catch (Exception e) {
            throw new ModuleException(pluginIdentifier, "view-listener", e);
        }
    }

    @Override
    public void disable() {
        getComponent().removeCustomEvent(eventListenerHook);
    }

    private ComponentPattern getComponent() {
        InternalViewDefinition extendsView = viewDefinitionService.getWithoutSession(extendsViewPlugin, extendsViewName);
        Preconditions.checkNotNull(extendsView, String.format("extension in %s: referes to view which not exists (%s - %s)",
                pluginIdentifier, extendsViewPlugin, extendsViewName));
        ComponentPattern component = extendsView.getComponentByReference(extendsComponentName);
        Preconditions.checkNotNull(component, String.format("extension in %s: component '%s' not exists in view (%s - %s)",
                pluginIdentifier, extendsComponentName, extendsViewPlugin, extendsViewName));
        return component;
    }

}
