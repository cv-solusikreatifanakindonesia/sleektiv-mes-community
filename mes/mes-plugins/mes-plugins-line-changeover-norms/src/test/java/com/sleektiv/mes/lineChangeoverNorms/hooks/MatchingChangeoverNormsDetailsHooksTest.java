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
package com.sleektiv.mes.lineChangeoverNorms.hooks;

import com.sleektiv.mes.lineChangeoverNorms.constants.LineChangeoverNormsConstants;
import com.sleektiv.mes.lineChangeoverNorms.listeners.MatchingChangeoverNormsDetailsListeners;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class MatchingChangeoverNormsDetailsHooksTest {

    private MatchingChangeoverNormsDetailsHooks hooks;

    @Mock
    private DataDefinitionService dataDefinitionService;

    @Mock
    private MatchingChangeoverNormsDetailsListeners listeners;

    @Mock
    private ViewDefinitionState view;

    @Mock
    private FormComponent form;

    @Mock
    private ComponentState matchingNorm, matchingNormNotFound;

    @Mock
    private DataDefinition changeoverDD;

    @Mock
    private Entity changeover;

    @Before
    public void init() {
        hooks = new MatchingChangeoverNormsDetailsHooks();

        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(hooks, "dataDefinitionService", dataDefinitionService);
        ReflectionTestUtils.setField(hooks, "listeners", listeners);

        given(view.getComponentByReference(SleektivViewConstants.L_FORM)).willReturn(form);
        given(view.getComponentByReference("matchingNorm")).willReturn(matchingNorm);
        given(view.getComponentByReference("matchingNormNotFound")).willReturn(matchingNormNotFound);
    }

    @Test
    public void shouldntSetFieldsVisibleWhenNormsNotFound() {
        // given
        given(form.getEntityId()).willReturn(null);

        // when
        hooks.setFieldsVisible(view);

        // then
        verify(matchingNorm).setVisible(false);
        verify(matchingNormNotFound).setVisible(true);
    }

    @Test
    public void shouldSetFieldsVisibleWhenNormsFound() {
        // given
        given(form.getEntityId()).willReturn(1L);

        // when
        hooks.setFieldsVisible(view);

        // then
        verify(matchingNorm).setVisible(true);
        verify(matchingNormNotFound).setVisible(false);
    }

    @Test
    public void shouldFillOrCleanFieldsNormsNotFound() {
        // given
        given(form.getEntityId()).willReturn(null);

        // when
        hooks.fillOrCleanFields(view);

        // then
        verify(listeners).clearField(view);
        verify(listeners).changeStateEditButton(view, false);
    }

    @Test
    public void shouldFillOrCleanFieldsWhenNormsFound() {
        // given
        given(form.getEntityId()).willReturn(1L);
        given(
                dataDefinitionService.get(LineChangeoverNormsConstants.PLUGIN_IDENTIFIER,
                        LineChangeoverNormsConstants.MODEL_LINE_CHANGEOVER_NORMS)).willReturn(changeoverDD);
        given(changeoverDD.get(1L)).willReturn(changeover);

        // when
        hooks.fillOrCleanFields(view);

        // then
        verify(listeners).fillField(view, changeover);
        verify(listeners).changeStateEditButton(view, true);
    }

}
