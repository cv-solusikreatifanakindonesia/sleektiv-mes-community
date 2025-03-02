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
package com.sleektiv.security.internal.validators;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.google.common.base.Objects;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.security.api.SecurityService;
import com.sleektiv.security.constants.GroupFields;
import com.sleektiv.security.constants.SleektivSecurityConstants;

@Service
public class GroupRolesValidationService {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public boolean checkUserAddingRoleSuperadmin(final DataDefinition dataDefinition, final Entity entity) {
        if (isCalledFromSecurityModules() || isCurrentUserSuperAdmin(dataDefinitionService.get(SleektivSecurityConstants.PLUGIN_IDENTIFIER,
                SleektivSecurityConstants.MODEL_USER))) {
            return true;
        }
        Boolean isRoleSuperadminInNewGroup = hasRoleSuperAdmin(entity);
        Boolean isRoleSuperadminInOldGroup = entity.getId() != null && hasRoleSuperAdmin(dataDefinition.get(entity.getId()));

        if (Objects.equal(isRoleSuperadminInNewGroup, isRoleSuperadminInOldGroup)) {
            return true;
        }

        entity.addError(dataDefinition.getField(GroupFields.ROLES), "sleektivUsers.validate.global.error.forbiddenRole");
        return false;
    }

    private Boolean hasRoleSuperAdmin(final Entity entity) {
        List<Entity> roles = entity.getManyToManyField(GroupFields.ROLES);
        for (Entity role : roles) {
            if (SleektivSecurityConstants.ROLE_SUPERADMIN.equals(role.getStringField("identifier"))) {
                return true;
            }
        }
        return false;
    }

    private boolean isCurrentUserSuperAdmin(final DataDefinition userDataDefinition) {
        final Long currentUserId = securityService.getCurrentUserId();
        final Entity currentUserEntity = userDataDefinition.get(currentUserId);
        return securityService.hasRole(currentUserEntity, SleektivSecurityConstants.ROLE_SUPERADMIN);
    }

    private boolean isCalledFromSecurityModules() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }

}
