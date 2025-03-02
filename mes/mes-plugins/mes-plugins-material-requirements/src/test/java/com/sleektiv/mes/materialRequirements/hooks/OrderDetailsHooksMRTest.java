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
package com.sleektiv.mes.materialRequirements.hooks;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.materialRequirements.constants.InputProductsRequiredForType;
import com.sleektiv.mes.materialRequirements.constants.OrderFieldsMR;
import com.sleektiv.mes.materialRequirements.constants.ParameterFieldsMR;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

public class OrderDetailsHooksMRTest {

    

    private OrderDetailsHooksMR orderDetailsHooksMR;

    @Mock
    private ParameterService parameterService;

    @Mock
    private ViewDefinitionState view;

    @Mock
    private Entity parameter;

    @Mock
    private FormComponent orderForm;

    @Mock
    private FieldComponent inputProductsRequiredForTypeField;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        orderDetailsHooksMR = new OrderDetailsHooksMR();

        ReflectionTestUtils.setField(orderDetailsHooksMR, "parameterService", parameterService);

        given(view.getComponentByReference(SleektivViewConstants.L_FORM)).willReturn(orderForm);
        given(orderForm.getEntityId()).willReturn(null);

        given(view.getComponentByReference(OrderFieldsMR.INPUT_PRODUCTS_REQUIRED_FOR_TYPE)).willReturn(
                inputProductsRequiredForTypeField);

        given(parameterService.getParameter()).willReturn(parameter);
        given(parameter.getStringField(ParameterFieldsMR.INPUT_PRODUCTS_REQUIRED_FOR_TYPE)).willReturn(
                InputProductsRequiredForType.START_ORDER.getStringValue());
    }

    @Test
    public final void shouldSetInputProductsRequiredForTypeFromParameters() {
        // given
        given(inputProductsRequiredForTypeField.getFieldValue()).willReturn(null);

        // when
        orderDetailsHooksMR.setInputProductsRequiredForTypeFromParameters(view);

        // then
        verify(inputProductsRequiredForTypeField).setFieldValue(InputProductsRequiredForType.START_ORDER.getStringValue());
    }

    @Test
    public final void shouldntSetInputProductsRequiredForTypeFromParameters() {
        // given
        given(inputProductsRequiredForTypeField.getFieldValue()).willReturn(
                InputProductsRequiredForType.START_ORDER.getStringValue());

        // when
        orderDetailsHooksMR.setInputProductsRequiredForTypeFromParameters(view);

        // then
        verify(inputProductsRequiredForTypeField, never()).setFieldValue(
                InputProductsRequiredForType.START_ORDER.getStringValue());
    }

}
