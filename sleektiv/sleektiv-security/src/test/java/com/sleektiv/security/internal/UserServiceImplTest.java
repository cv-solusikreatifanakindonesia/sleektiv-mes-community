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

import static com.sleektiv.testing.model.EntityTestUtils.stubStringField;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.security.constants.UserFields;

public class UserServiceImplTest {

    private UserServiceImpl userService;

    @Mock
    private Entity userEntity;

    @Mock
    private DataDefinitionService dataDefinitionService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        userService = new UserServiceImpl();
        ReflectionTestUtils.setField(userService, "dataDefinitionService", dataDefinitionService);
    }

    @Test
    public final void shouldReturnFullName() {
        // given
        String firstName = "firstName";
        String lastName = "lastName";
        stubStringField(userEntity, UserFields.FIRST_NAME, firstName);
        stubStringField(userEntity, UserFields.LAST_NAME, lastName);

        // when
        String fullName = userService.extractFullName(userEntity);

        // then
        assertEquals(String.format("%s %s", firstName, lastName), fullName);
    }

    @Test
    public final void shouldReturnNullIfGivenEntityIsNull() {
        // when
        String fullName = userService.extractFullName(null);

        // then
        assertNull(fullName);
    }

}
