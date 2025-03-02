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

import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.sleektiv.plugin.api.ModuleFactory;
import com.sleektiv.view.internal.api.InternalViewDefinitionService;
import com.sleektiv.view.internal.hooks.HookFactory;
import com.sleektiv.view.internal.hooks.ViewEventListenerHook;

public class ViewListenerModuleFactory extends ModuleFactory<ViewListenerModule> {

    @Autowired
    private HookFactory hookFactory;

    @Autowired
    private InternalViewDefinitionService viewDefinitionService;

    @Override
    protected ViewListenerModule parseElement(final String pluginIdentifier, final Element element) {
        String plugin = getRequiredAttribute(element, "plugin");
        String view = getRequiredAttribute(element, "view");
        String component = getRequiredAttribute(element, "component");
        String eventName = getRequiredAttribute(element, "event");
        String className = getRequiredAttribute(element, "class");
        String method = getRequiredAttribute(element, "method");
        ViewEventListenerHook hook = hookFactory.buildViewEventListener(eventName, className, method, pluginIdentifier);

        return new ViewListenerModule(pluginIdentifier, viewDefinitionService, plugin, view, component, hook);
    }

    @Override
    public String getIdentifier() {
        return "view-listener";
    }

}
