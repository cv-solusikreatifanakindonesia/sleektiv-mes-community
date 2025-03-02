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

import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.plugin.api.Module;
import com.sleektiv.security.constants.GroupFields;
import com.sleektiv.security.constants.SleektivSecurityConstants;
import com.sleektiv.security.constants.RoleFields;

public class UserGroupModule extends Module {

    private final String name;

    private final String identifier;

    private final String roles;

    private final String permissionType;

    private final DataDefinitionService dataDefinitionService;

    public UserGroupModule(final String name, final String identifier, final String roles, final String permissionType,
                           final DataDefinitionService dataDefinitionService) {
        super();

        this.name = name;
        this.identifier = identifier;
        this.roles = roles;
        this.permissionType = permissionType;
        this.dataDefinitionService = dataDefinitionService;
    }

    @Override
    public void multiTenantEnable() {
        if (dataDefinitionService.get(SleektivSecurityConstants.PLUGIN_IDENTIFIER, SleektivSecurityConstants.MODEL_GROUP).find().add(SearchRestrictions.eq(GroupFields.IDENTIFIER, identifier))
                .list().getTotalNumberOfEntities() > 0) {
            return;
        }

        Entity entity = dataDefinitionService.get(SleektivSecurityConstants.PLUGIN_IDENTIFIER, SleektivSecurityConstants.MODEL_GROUP).create();
        entity.setField("name", name);
        entity.setField(GroupFields.IDENTIFIER, identifier);
        entity.setField("permissionType", permissionType);
        DataDefinition roleDD = dataDefinitionService.get(SleektivSecurityConstants.PLUGIN_IDENTIFIER, "role");
        entity.setField("roles", roleDD.find().add(SearchRestrictions.in(RoleFields.IDENTIFIER, (Object[]) roles.split(","))).list()
                .getEntities());
        dataDefinitionService.get(SleektivSecurityConstants.PLUGIN_IDENTIFIER, SleektivSecurityConstants.MODEL_GROUP).save(entity);
    }
}
