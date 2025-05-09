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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.internal.ScopeEntityIdChangeListener;
import com.sleektiv.view.internal.components.form.FormComponentPattern;
import com.sleektiv.view.internal.components.form.FormComponentState;

public class ScopeEntityIdChangeListenerTest extends AbstractStateTest {

    @Test
    public void shouldHaveScopeListeners() throws Exception {
        // given
        ComponentState component1 = createMockComponent("component1");
        ComponentState component2 = createMockComponent("component2");

        FormComponentPattern pattern = mock(FormComponentPattern.class);
        given(pattern.getExpressionNew()).willReturn(null);
        given(pattern.getExpressionEdit()).willReturn(null);
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        setField(pattern, "applicationContext", applicationContext);
        FormComponentState container = new FormComponentState(pattern);
        container.addScopeEntityIdChangeListener("component1", (ScopeEntityIdChangeListener) component1);
        container.addScopeEntityIdChangeListener("component2", (ScopeEntityIdChangeListener) component2);

        // when
        container.setFieldValue(13L);

        // then
        verify((ScopeEntityIdChangeListener) component1).onScopeEntityIdChange(13L);
        verify((ScopeEntityIdChangeListener) component2).onScopeEntityIdChange(13L);
    }

}
