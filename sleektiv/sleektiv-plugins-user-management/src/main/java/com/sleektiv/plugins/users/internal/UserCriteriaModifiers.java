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
package com.sleektiv.plugins.users.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.JoinType;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.security.api.SecurityService;
import com.sleektiv.security.constants.GroupFields;
import com.sleektiv.security.constants.PermissionType;
import com.sleektiv.security.constants.SleektivSecurityConstants;
import com.sleektiv.security.constants.UserFields;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;

@Service
public class UserCriteriaModifiers {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void criteriaForGroup(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        Entity loggedUser = dataDefinitionService
                .get(SleektivSecurityConstants.PLUGIN_IDENTIFIER, SleektivSecurityConstants.MODEL_USER).get(
                        securityService.getCurrentUserId());
        if (!securityService.hasRole(loggedUser, "ROLE_SUPERADMIN")) {
            scb.add(SearchRestrictions.ne(GroupFields.PERMISSION_TYPE, PermissionType.SUPER_ADMIN.getStringValue()));
        }

    }
}