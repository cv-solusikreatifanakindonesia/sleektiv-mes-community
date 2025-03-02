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
package com.sleektiv.mes.advancedGenealogy.hooks;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.sleektiv.mes.basic.CompanyService;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;

public class BatchViewHooksTest {

    @Mock
    private ViewDefinitionState view;

    @Mock
    private FieldComponent supplier;

    @Mock
    private SearchCriteriaBuilder searchCriteria;

    @Mock
    private Entity company;

    @Mock
    private DataDefinition companyDD;

    @Mock
    private DataDefinitionService dataDefinitionService;

    private BatchViewHooks batchViewHooks;

    @Mock
    private CompanyService companyService;

    @Before
    public final void init() {
        MockitoAnnotations.initMocks(this);

        batchViewHooks = new BatchViewHooks();

        ReflectionTestUtils.setField(batchViewHooks, "companyService", companyService);

        given(view.getComponentByReference("supplier")).willReturn(supplier);
    }

    @Test
    public void shouldSupplierToOwner() {
        // given
        given(supplier.getFieldValue()).willReturn(null);

        given(companyService.getCompany()).willReturn(company);

        // when
        batchViewHooks.setSupplierToOwner(view);

        // then
        verify(supplier).setFieldValue(company.getId());
        verify(supplier).requestComponentUpdateState();
    }

    @Test
    public void shouldntSetSupplierToOwner() {
        // given
        given(supplier.getFieldValue()).willReturn(1L);

        // when
        batchViewHooks.setSupplierToOwner(view);

        // then
        verify(supplier, never()).setFieldValue(Mockito.any());
        verify(supplier, never()).requestComponentUpdateState();
    }

}
