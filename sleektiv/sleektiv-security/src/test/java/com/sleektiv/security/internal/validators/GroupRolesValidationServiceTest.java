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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Lists;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.FieldDefinition;
import com.sleektiv.security.api.SecurityService;
import com.sleektiv.security.constants.GroupFields;
import com.sleektiv.security.constants.SleektivSecurityConstants;
import com.sleektiv.security.constants.RoleFields;
import com.sleektiv.security.constants.UserFields;

public class GroupRolesValidationServiceTest {

    private GroupRolesValidationService groupRolesValidationService;

    @Mock
    private SecurityService securityService;

    @Mock
    private DataDefinitionService dataDefinitionService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Entity userGroupMock, existingUserGroupMock, currentUserEntityMock;

    @Mock
    private FieldDefinition groupRolesFieldDefMock;

    @Mock
    private DataDefinition userDataDefMock;

    @Mock
    private DataDefinition groupDataDefMock;

    @Before
    public final void init() {
        MockitoAnnotations.initMocks(this);

        given(securityService.getCurrentUserId()).willReturn(1L);

        SecurityContextHolder.setContext(securityContext);

        given(userDataDefMock.get(1L)).willReturn(currentUserEntityMock);

        given(groupDataDefMock.getField(GroupFields.ROLES)).willReturn(groupRolesFieldDefMock);
        given(userGroupMock.getId()).willReturn(1000L);
        given(groupDataDefMock.get(1000L)).willReturn(existingUserGroupMock);

        given(dataDefinitionService.get(SleektivSecurityConstants.PLUGIN_IDENTIFIER, SleektivSecurityConstants.MODEL_USER))
                .willReturn(userDataDefMock);

        groupRolesValidationService = new GroupRolesValidationService();
        ReflectionTestUtils.setField(groupRolesValidationService, "securityService", securityService);
        ReflectionTestUtils.setField(groupRolesValidationService, "dataDefinitionService", dataDefinitionService);
    }

    private void stubGroupRoles(Entity group, String... roles) {

        List<Entity> rolesEntity = Lists.newArrayList();
        for (String role : roles) {
            Entity roleEntity = mock(Entity.class);
            given(roleEntity.getStringField(RoleFields.IDENTIFIER)).willReturn(role);
            rolesEntity.add(roleEntity);
        }

        given(group.getManyToManyField(GroupFields.ROLES)).willReturn(rolesEntity);
    }

    private void stubCurrentUserRole(final String role) {
        Entity roleEntity = mock(Entity.class);
        given(roleEntity.getStringField(RoleFields.IDENTIFIER)).willReturn(role);
        given(currentUserEntityMock.getManyToManyField(UserFields.GROUP)).willReturn(Lists.newArrayList(roleEntity));
        given(securityService.hasRole(currentUserEntityMock, SleektivSecurityConstants.ROLE_SUPERADMIN)).willReturn(
                SleektivSecurityConstants.ROLE_SUPERADMIN.equals(role));
    }

    private void stubSecurityContextWithAuthentication() {
        final Authentication authentication = mock(Authentication.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
    }

    @Test
    public final void shouldMarkAddingRoleSuperadminAsInvalidWhenPerformedByNonSuperadmin() {
        // given
        stubSecurityContextWithAuthentication();
        stubCurrentUserRole(SleektivSecurityConstants.ROLE_USER);
        stubGroupRoles(existingUserGroupMock, SleektivSecurityConstants.ROLE_ADMIN);
        stubGroupRoles(userGroupMock, SleektivSecurityConstants.ROLE_ADMIN, SleektivSecurityConstants.ROLE_SUPERADMIN);
        // when

        final boolean isValid = groupRolesValidationService.checkUserAddingRoleSuperadmin(groupDataDefMock, userGroupMock);

        // then
        assertFalse(isValid);
        verify(userGroupMock).addError(Mockito.eq(groupRolesFieldDefMock), Mockito.anyString());
    }

    @Test
    public final void shouldMarkRemovingRoleSuperadminAsInvalidWhenPerformedByNonSuperadmin() {
        // given
        stubSecurityContextWithAuthentication();
        stubCurrentUserRole(SleektivSecurityConstants.ROLE_USER);
        stubGroupRoles(existingUserGroupMock, SleektivSecurityConstants.ROLE_ADMIN, SleektivSecurityConstants.ROLE_SUPERADMIN);
        stubGroupRoles(userGroupMock, SleektivSecurityConstants.ROLE_ADMIN);
        // when

        final boolean isValid = groupRolesValidationService.checkUserAddingRoleSuperadmin(groupDataDefMock, userGroupMock);

        // then
        assertFalse(isValid);
        verify(userGroupMock).addError(Mockito.eq(groupRolesFieldDefMock), Mockito.anyString());
    }

    @Test
    public final void shouldAlloveAddingRoleSuperadminWhenPerformedBySuperadmin() {
        // given
        stubSecurityContextWithAuthentication();
        stubCurrentUserRole(SleektivSecurityConstants.ROLE_SUPERADMIN);
        stubGroupRoles(existingUserGroupMock, SleektivSecurityConstants.ROLE_ADMIN);
        stubGroupRoles(userGroupMock, SleektivSecurityConstants.ROLE_ADMIN, SleektivSecurityConstants.ROLE_SUPERADMIN);
        // when

        final boolean isValid = groupRolesValidationService.checkUserAddingRoleSuperadmin(groupDataDefMock, userGroupMock);

        // then
        assertTrue(isValid);
    }

    @Test
    public final void shouldAlloveRemovingRoleSuperadminWhenPerformedBySuperadmin() {
        // given
        stubSecurityContextWithAuthentication();
        stubCurrentUserRole(SleektivSecurityConstants.ROLE_SUPERADMIN);
        stubGroupRoles(existingUserGroupMock, SleektivSecurityConstants.ROLE_ADMIN, SleektivSecurityConstants.ROLE_SUPERADMIN);
        stubGroupRoles(userGroupMock, SleektivSecurityConstants.ROLE_ADMIN);
        // when

        final boolean isValid = groupRolesValidationService.checkUserAddingRoleSuperadmin(groupDataDefMock, userGroupMock);

        // then
        assertTrue(isValid);
    }

}
