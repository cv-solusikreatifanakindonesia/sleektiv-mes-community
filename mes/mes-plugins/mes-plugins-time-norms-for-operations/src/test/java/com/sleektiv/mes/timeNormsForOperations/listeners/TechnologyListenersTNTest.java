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
package com.sleektiv.mes.timeNormsForOperations.listeners;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.sleektiv.mes.timeNormsForOperations.NormService;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ComponentState.MessageType;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

public class TechnologyListenersTNTest {

    private TechnologyListenersTN technologyListenersTN;

    @Mock
    private Entity technology;

    @Mock
    private DataDefinition dataDefinition;

    @Mock
    private ViewDefinitionState view;

    @Mock
    private ComponentState componentState;

    @Mock
    private FormComponent form;

    @Mock
    private Entity operComp1, operComp2, prod1, prod2, prod1Comp, prod2Comp;

    @Mock
    private NormService normService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        technologyListenersTN = new TechnologyListenersTN();

        ReflectionTestUtils.setField(technologyListenersTN, "normService", normService);
    }

    @Test
    public void shouldReturnIfTheTechnologyIsntInDraftState() {
        // given
        given(view.getComponentByReference(SleektivViewConstants.L_FORM)).willReturn(form);
        given(form.getEntity()).willReturn(technology);
        given(technology.getStringField("state")).willReturn("02accepted");

        // when
        technologyListenersTN.checkOperationOutputQuantities(view, componentState, null);

        // then
        verify(normService, never()).checkOperationOutputQuantities(technology);
        verify(form, never()).addMessage(Mockito.anyString(), Mockito.eq(MessageType.INFO), Mockito.eq(false));
    }

    @Ignore
    @Test
    public void shouldPassValidationErrorsToTheEntityForAcceptedTechnology() {
        // given
        given(view.getComponentByReference(SleektivViewConstants.L_FORM)).willReturn(form);
        given(form.getEntity()).willReturn(technology);
        given(technology.getStringField("state")).willReturn("01draft");

        given(technology.getDataDefinition()).willReturn(dataDefinition);
        given(technology.getId()).willReturn(0L);
        given(dataDefinition.get(0L)).willReturn(technology);

        // given(normService.checkOperationOutputQuantities(technology)).willReturn(asList("err1", "err2"));

        // when
        technologyListenersTN.checkOperationOutputQuantities(view, componentState, null);

        // then
        verify(form).addMessage(Mockito.eq("err1"), Mockito.eq(MessageType.INFO), Mockito.eq(false));
        verify(form).addMessage(Mockito.eq("err2"), Mockito.eq(MessageType.INFO), Mockito.eq(false));
    }
}
