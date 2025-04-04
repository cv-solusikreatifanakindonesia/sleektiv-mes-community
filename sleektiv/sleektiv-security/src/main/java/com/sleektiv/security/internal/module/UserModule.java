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

import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.plugin.api.Module;
import com.sleektiv.security.constants.SleektivSecurityConstants;

public class UserModule extends Module {

    private final String login;

    private final String email;

    private final String firstName;

    private final String lastName;

    private final String password;

    private final String groupIdentifier;

    private final DataDefinitionService dataDefinitionService;

    public UserModule(final String login, final String email, final String firstName, final String lastName,
            final String password, final String groupIdentifier, final DataDefinitionService dataDefinitionService) {
        super();

        this.login = login;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.groupIdentifier = groupIdentifier;
        this.dataDefinitionService = dataDefinitionService;
    }

    @Override
    public void multiTenantEnable() {
        if (dataDefinitionService.get(SleektivSecurityConstants.PLUGIN_IDENTIFIER, SleektivSecurityConstants.MODEL_USER).find().add(SearchRestrictions.eq("userName", login)).list()
                .getTotalNumberOfEntities() > 0) {
            return;
        }

        Entity group = dataDefinitionService.get(SleektivSecurityConstants.PLUGIN_IDENTIFIER, "group").find()
                .add(SearchRestrictions.eq("identifier", groupIdentifier)).list().getEntities().get(0);

        Entity entity = dataDefinitionService.get(SleektivSecurityConstants.PLUGIN_IDENTIFIER, SleektivSecurityConstants.MODEL_USER).create();
        entity.setField("userName", login);
        entity.setField("email", email);
        entity.setField("firstName", firstName);
        entity.setField("lastName", lastName);
        entity.setField("password", password);
        entity.setField("passwordConfirmation", password);
        entity.setField("enabled", true);
        entity.setField("group", group);
        dataDefinitionService.get(SleektivSecurityConstants.PLUGIN_IDENTIFIER, SleektivSecurityConstants.MODEL_USER).save(entity);
    }
}
