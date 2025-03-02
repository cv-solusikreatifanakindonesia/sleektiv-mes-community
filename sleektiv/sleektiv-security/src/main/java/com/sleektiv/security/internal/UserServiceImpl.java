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
package com.sleektiv.security.internal;

import static com.sleektiv.model.api.search.SearchRestrictions.eq;
import static com.sleektiv.model.api.search.SearchRestrictions.idEq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.security.api.SecurityService;
import com.sleektiv.security.api.UserService;
import com.sleektiv.security.constants.SleektivSecurityConstants;
import com.sleektiv.security.constants.UserFields;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private SecurityService securityService;

    @Override
    public Entity find(final Long userId) {
        return getUserDataDefinition().find().add(idEq(userId)).uniqueResult();
    }

    @Override
    public Entity find(final String userName) {
        return getUserDataDefinition().find().add(eq(UserFields.USER_NAME, userName)).uniqueResult();
    }

    @Override
    public Entity getCurrentUserEntity() {
        return find(securityService.getCurrentUserId());
    }

    @Override
    public String extractFullName(final Entity user) {
        if (user == null) {
            return null;
        }
        String firstName = user.getStringField(UserFields.FIRST_NAME);
        String lastName = user.getStringField(UserFields.LAST_NAME);
        return String.format("%s %s", firstName, lastName);
    }

    private DataDefinition getUserDataDefinition() {
        return dataDefinitionService.get(SleektivSecurityConstants.PLUGIN_IDENTIFIER, SleektivSecurityConstants.MODEL_USER);
    }
}
