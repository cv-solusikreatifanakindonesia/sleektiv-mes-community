/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv MES
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
package com.sleektiv.mes.wageGroups.hooks;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.sleektiv.mes.basic.util.CurrencyService;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;

public class WageGroupsDetailsHooksTest {

    private WageGroupsDetailsHooks hooks;

    @Mock
    private CurrencyService currencyService;

    @Mock
    private FieldComponent field;

    @Mock
    private ViewDefinitionState view;

    @Before
    public void init() {
        hooks = new WageGroupsDetailsHooks();
        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(hooks, "currencyService", currencyService);
    }

    @Test
    public void shouldFillFieldWithCurrency() throws Exception {
        // given
        String currency = "pln";
        when(view.getComponentByReference("laborHourlyCostCURRENCY")).thenReturn(field);
        when(currencyService.getCurrencyAlphabeticCode()).thenReturn(currency);
        // when
        hooks.setCurrency(view);
        // then
        Mockito.verify(field).setFieldValue(currency);
    }
}
