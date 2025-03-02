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

import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.security.api.SecurityService;
import com.sleektiv.security.constants.SleektivSecurityConstants;
import com.sleektiv.security.constants.UserFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserRoleValidationService {

    @Autowired
    private SecurityService securityService;

    public boolean checkUserCreatingSuperadmin(final DataDefinition dataDefinition, final Entity entity) {
        if (isCalledFromSecurityModules() || isCurrentUserSuperAdmin(dataDefinition)) {
            return true;
        }

        Boolean isRoleSuperadminInNewGroup = securityService.hasRole(entity, SleektivSecurityConstants.ROLE_SUPERADMIN);
        Boolean isRoleSuperadminInOldGroup = Objects.nonNull(entity.getId()) && securityService.hasRole(
                dataDefinition.get(entity.getId()), SleektivSecurityConstants.ROLE_SUPERADMIN);

        if (Objects.equals(isRoleSuperadminInOldGroup, isRoleSuperadminInNewGroup)) {
            return true;
        }

        entity.addError(dataDefinition.getField(UserFields.GROUP), "sleektivUsers.validate.global.error.forbiddenRole");

        return false;
    }

    private boolean isCurrentUserSuperAdmin(final DataDefinition userDataDefinition) {
        final Long currentUserId = securityService.getCurrentUserId();

        final Entity currentUserEntity = userDataDefinition.get(currentUserId);

        return securityService.hasRole(currentUserEntity, SleektivSecurityConstants.ROLE_SUPERADMIN);
    }

    private boolean isCalledFromSecurityModules() {
        return Objects.isNull(SecurityContextHolder.getContext().getAuthentication())
                || (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken);
    }

}
