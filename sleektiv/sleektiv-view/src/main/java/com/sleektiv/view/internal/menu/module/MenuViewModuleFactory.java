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
package com.sleektiv.view.internal.menu.module;

import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.sleektiv.plugin.api.ModuleFactory;
import com.sleektiv.view.internal.api.InternalMenuService;
import com.sleektiv.view.internal.menu.definitions.MenuItemDefinition;

public class MenuViewModuleFactory extends ModuleFactory<MenuModule> {

    @Autowired
    private InternalMenuService menuService;

    @Override
    protected MenuModule parseElement(final String pluginIdentifier, final Element element) {
        String menuName = getRequiredAttribute(element, "name");
        String menuCategory = getRequiredAttribute(element, "category");
        String menuViewName = getRequiredAttribute(element, "view");
        String authRoleIdentifier = getAttribute(element, "defaultAuthorizationRole");
        String itemActiveAttribute = getAttribute(element, "active");
        boolean itemActive = itemActiveAttribute == null ? true : Boolean.parseBoolean(itemActiveAttribute);

        MenuItemDefinition menuItemDefinition = MenuItemDefinition.create(pluginIdentifier, menuName, menuCategory,
                authRoleIdentifier, itemActive).forView(pluginIdentifier, menuViewName);

        return new MenuModule(getIdentifier(), menuService, menuItemDefinition);
    }

    @Override
    public String getIdentifier() {
        return "menu-item";
    }

}
