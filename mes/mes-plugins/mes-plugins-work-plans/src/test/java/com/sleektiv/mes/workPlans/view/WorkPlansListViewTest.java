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
package com.sleektiv.mes.workPlans.view;

import com.google.common.collect.ImmutableList;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.ribbon.Ribbon;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;
import com.sleektiv.view.internal.components.window.WindowComponentState;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static com.sleektiv.testing.model.EntityTestUtils.mockEntity;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class WorkPlansListViewTest {

    private WorkPlansListView workPlansListView;

    @Mock
    private WindowComponentState windowComponent;

    @Mock
    private GridComponent workPlansGrid;

    @Mock
    private RibbonActionItem deleteButton;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        workPlansListView = new WorkPlansListView(windowComponent, deleteButton, workPlansGrid);
    }

    @Test
    public final void shouldBuildFromViewDefinitionState() {
        // given
        ViewDefinitionState viewDefinitionState = mock(ViewDefinitionState.class);
        given(viewDefinitionState.getComponentByReference(SleektivViewConstants.L_WINDOW)).willReturn(windowComponent);
        Ribbon ribbon = mock(Ribbon.class);
        given((windowComponent).getRibbon()).willReturn(ribbon);
        RibbonGroup actionsRibbonGroup = mock(RibbonGroup.class);
        given(ribbon.getGroupByName("actions")).willReturn(actionsRibbonGroup);
        given(actionsRibbonGroup.getItemByName("delete")).willReturn(deleteButton);

        given(viewDefinitionState.getComponentByReference(SleektivViewConstants.L_GRID)).willReturn(workPlansGrid);

        // when
        WorkPlansListView workPlansListView = WorkPlansListView.from(viewDefinitionState);
        workPlansListView.setUpDeleteButton(true, null);
        workPlansListView.getSelectedWorkPlans();

        // then
        verify(deleteButton).setEnabled(anyBoolean());
        verify(workPlansGrid).getSelectedEntities();

    }

    @Test
    public final void shouldGetSelectedEntities() {
        // given
        List<Entity> entities = ImmutableList.of(mockEntity(), mockEntity(), mockEntity(), mockEntity());
        given(workPlansGrid.getSelectedEntities()).willReturn(entities);

        // when
        List<Entity> result = workPlansListView.getSelectedWorkPlans();

        // then
        assertEquals(entities, result);
    }

    @Test
    public final void shouldSetUpDeleteButton() {
        // given
        boolean isEnabled = true;
        String message = "some arbitrary mesage";

        // when
        workPlansListView.setUpDeleteButton(isEnabled, message);

        // then
        verify(deleteButton).setEnabled(isEnabled);
        verify(deleteButton).setMessage(message);
        verify(deleteButton).requestUpdate(true);
        verify(windowComponent).requestRibbonRender();
    }

}
