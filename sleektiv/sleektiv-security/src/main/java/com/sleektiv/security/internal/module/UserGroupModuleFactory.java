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
package com.sleektiv.security.internal.module;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.plugin.api.ModuleFactory;

public class UserGroupModuleFactory extends ModuleFactory<UserGroupModule> {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Override
    protected UserGroupModule parseElement(final String pluginIdentifier, final Element element) {
        String name = getRequiredAttribute(element, "name");
        String identifier = getRequiredAttribute(element, "identifier");
        String roles = getRequiredAttribute(element, "roles");
        String permissionType = getRequiredAttribute(element, "permissionType");

        checkNotNull(name, "Missing name attribute of " + getIdentifier() + " module");
        checkNotNull(identifier, "Missing identifier attribute of " + getIdentifier() + " module");
        checkNotNull(roles, "Missing roles attribute of " + getIdentifier() + " module");
        checkNotNull(permissionType, "Missing permissionType attribute of " + getIdentifier() + " module");

        return new UserGroupModule(name, identifier, roles, permissionType, dataDefinitionService);
    }

    @Override
    public String getIdentifier() {
        return "user-group";
    }

}
