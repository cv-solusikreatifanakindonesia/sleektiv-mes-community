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
package com.sleektiv.mes.costNormsForOperation;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.sleektiv.mes.basic.util.CurrencyService;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

public class CostNormsForOperationServiceTest {

    private CostNormsForOperationService costNormsForOperationService;

    @Mock
    private ViewDefinitionState view;

    @Mock
    private FieldComponent field1, field2;

    @Mock
    private ComponentState state;

    @Mock
    private FormComponent form;

    @Mock
    private DataDefinitionService dataDefinitionService;

    @Mock
    private DataDefinition operationDD;

    @Mock
    private Entity operationEntity;

    @Mock
    private Object obj1, obj2;

    @Mock
    private CurrencyService currencyService;

    @Before
    public void init() {
        costNormsForOperationService = new CostNormsForOperationService();
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(costNormsForOperationService, "dataDefinitionService", dataDefinitionService);
        ReflectionTestUtils.setField(costNormsForOperationService, "currencyService", currencyService);

        when(dataDefinitionService.get("technologies", "operation")).thenReturn(operationDD);

        when(view.getComponentByReference("operation")).thenReturn(state);
        when(view.getComponentByReference(SleektivViewConstants.L_FORM)).thenReturn(form);

        when(view.getComponentByReference("laborHourlyCost")).thenReturn(field1);
        when(view.getComponentByReference("machineHourlyCost")).thenReturn(field2);

    }

    @Test
    public void shouldReturnWhenOperationIsNull() throws Exception {
        // when
        costNormsForOperationService.copyCostValuesFromOperation(view, state, null);
    }

    @Test
    public void shouldApplyCostNormsForGivenSource() throws Exception {
        // given
        when(state.getFieldValue()).thenReturn(1L);
        Long operationId = 1L;
        when(operationDD.get(operationId)).thenReturn(operationEntity);
        when(operationEntity.getField("laborHourlyCost")).thenReturn(obj1);
        when(operationEntity.getField("machineHourlyCost")).thenReturn(obj2);
        // when
        costNormsForOperationService.copyCostValuesFromOperation(view, state, null);
        // then
        Mockito.verify(field1).setFieldValue(obj1);
        Mockito.verify(field2).setFieldValue(obj2);

    }

    @Test
    public void shouldFillCurrencyFields() throws Exception {
        // given
        String currency = "PLN";
        when(currencyService.getCurrencyAlphabeticCode()).thenReturn(currency);
        when(view.getComponentByReference("laborHourlyCostCURRENCY")).thenReturn(field1);
        when(view.getComponentByReference("machineHourlyCostCURRENCY")).thenReturn(field2);

        // when
        costNormsForOperationService.fillCurrencyFields(view);
        // then

        Mockito.verify(field1).setFieldValue(currency);
        Mockito.verify(field2).setFieldValue(currency);
    }

}
