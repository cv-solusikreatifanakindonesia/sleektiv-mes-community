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
package com.sleektiv.view.internal.states;

import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.internal.components.form.FormComponentPattern;
import com.sleektiv.view.internal.components.form.FormComponentState;
import com.sleektiv.view.internal.hooks.ViewEventListenerHook;

public class DefaultEventHandlerTest {

    private ApplicationContext applicationContext;

    @Before
    public void init() throws Exception {
        applicationContext = mock(ApplicationContext.class);
    }

    @Test
    public void shouldCallEventMethod() throws Exception {
        // given
        ViewDefinitionState viewDefinitionState = mock(ViewDefinitionState.class);

        FormComponentPattern pattern = mock(FormComponentPattern.class);
        given(pattern.getExpressionNew()).willReturn(null);
        given(pattern.getExpressionEdit()).willReturn(null);
        setField(pattern, "applicationContext", applicationContext);
        FormComponentState component = new FormComponentState(pattern);
        component.setFieldValue(13L);

        // when
        component.performEvent(viewDefinitionState, "clear");

        // then
        assertNull("value is " + component.getFieldValue(), component.getFieldValue());

    }

    @Test
    public void shouldCallCustomEventMethod() throws Exception {
        // given
        ViewDefinitionState viewDefinitionState = mock(ViewDefinitionState.class);

        FormComponentPattern pattern = mock(FormComponentPattern.class);
        given(pattern.getExpressionNew()).willReturn(null);
        given(pattern.getExpressionEdit()).willReturn(null);
        setField(pattern, "applicationContext", applicationContext);
        FormComponentState component = new FormComponentState(pattern);
        ViewEventListenerHook eventListener = mock(ViewEventListenerHook.class);
        given(eventListener.getEventName()).willReturn("custom");
        component.registerCustomEvent(eventListener);

        // when
        component.performEvent(viewDefinitionState, "custom", "arg0", "arg1");

        // then
        Mockito.verify(eventListener).invokeEvent(viewDefinitionState, component, new String[] { "arg0", "arg1" });
    }

    @Test
    public void shouldCallMultipleEventMethods() throws Exception {
        // given
        ViewDefinitionState viewDefinitionState = mock(ViewDefinitionState.class);

        FormComponentPattern pattern = mock(FormComponentPattern.class);
        given(pattern.getExpressionNew()).willReturn(null);
        given(pattern.getExpressionEdit()).willReturn(null);
        setField(pattern, "applicationContext", applicationContext);
        FormComponentState component = new FormComponentState(pattern);
        component.setFieldValue(13L);

        ViewEventListenerHook eventListener1 = mock(ViewEventListenerHook.class);
        given(eventListener1.getEventName()).willReturn("clear");
        component.registerCustomEvent(eventListener1);

        ViewEventListenerHook eventListener2 = mock(ViewEventListenerHook.class);
        given(eventListener2.getEventName()).willReturn("clear");
        component.registerCustomEvent(eventListener2);

        // when
        component.performEvent(viewDefinitionState, "clear");

        // then
        assertNull("value is " + component.getFieldValue(), component.getFieldValue());
        Mockito.verify(eventListener1).invokeEvent(viewDefinitionState, component, new String[0]);
        Mockito.verify(eventListener2).invokeEvent(viewDefinitionState, component, new String[0]);
    }

    @Test
    public void shouldNotThrowExceptionWhenEventNotExists() throws Exception {
        // given
        ViewDefinitionState viewDefinitionState = mock(ViewDefinitionState.class);

        FormComponentPattern pattern = mock(FormComponentPattern.class);
        given(pattern.getExpressionNew()).willReturn(null);
        given(pattern.getExpressionEdit()).willReturn(null);
        setField(pattern, "applicationContext", applicationContext);
        FormComponentState component = new FormComponentState(pattern);
        component.setFieldValue(13L);

        // when
        component.performEvent(viewDefinitionState, "noSuchMethod");
    }

}
