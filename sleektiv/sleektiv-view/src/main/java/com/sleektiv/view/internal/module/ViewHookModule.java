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
import com.sleektiv.view.internal.api.InternalViewDefinition;
import com.sleektiv.view.internal.api.InternalViewDefinitionService;
import com.sleektiv.view.internal.hooks.AbstractViewHookDefinition;
import com.sleektiv.view.internal.hooks.ViewLifecycleHook;

public class ViewHookModule<T> extends Module {

    private final InternalViewDefinitionService viewDefinitionService;

    private final String extendsViewPlugin;

    private final String extendsViewName;

    private final AbstractViewHookDefinition hook;

    private final String pluginIdentifier;

    public ViewHookModule(final String pluginIdentifier, final InternalViewDefinitionService viewDefinitionService,
            final String extendsViewPlugin, final String extendsViewName,
            final AbstractViewHookDefinition hook) {
        super();

        this.pluginIdentifier = pluginIdentifier;
        this.viewDefinitionService = viewDefinitionService;
        this.extendsViewPlugin = extendsViewPlugin;
        this.extendsViewName = extendsViewName;
        this.hook = hook;
    }

    @Override
    public void enableOnStartup() {
        enable();
    }

    @Override
    public void enable() {
        try {
            getViewDefinition().addHook(hook);
        } catch (Exception e) {
            throw new ModuleException(pluginIdentifier, "view-hook", e);
        }
    }

    @Override
    public void disable() {
        getViewDefinition().removeHook(hook);
    }

    private InternalViewDefinition getViewDefinition() {
        InternalViewDefinition extendsView = viewDefinitionService.getWithoutSession(extendsViewPlugin, extendsViewName);
        Preconditions.checkNotNull(extendsView, String.format("extension from %s: refers to view which not exists (%s - %s)",
                pluginIdentifier, extendsViewPlugin, extendsViewName));
        return extendsView;
    }

}
