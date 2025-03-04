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
package com.sleektiv.mes.orders.hooks;

import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.ribbon.Ribbon;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;
import com.sleektiv.view.internal.components.window.WindowComponentState;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class ProductDetailsViewHooksOTest {

    private static final long L_ID = 1L;

    private ProductDetailsViewHooksO productDetailsViewHooksO;

    @Mock
    private ViewDefinitionState view;

    @Mock
    private FormComponent productForm;

    @Mock
    private WindowComponentState window;

    @Mock
    private Ribbon ribbon;

    @Mock
    private RibbonGroup orders;

    @Mock
    private RibbonActionItem showOrdersWithProductMain, showOrdersWithProductPlanned;

    @Mock
    private Entity product, technologyGroup;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        productDetailsViewHooksO = new ProductDetailsViewHooksO();

        given(view.getComponentByReference(SleektivViewConstants.L_FORM)).willReturn(productForm);
        given(productForm.getEntity()).willReturn(product);
    }

    @Test
    public void shouldntUpdateRibbonStateIfProductIsntSaved() {
        // given
        given(product.getId()).willReturn(null);

        given(view.getComponentByReference(SleektivViewConstants.L_WINDOW)).willReturn((ComponentState) window);

        given(window.getRibbon()).willReturn(ribbon);

        given(ribbon.getGroupByName("orders")).willReturn(orders);

        given(orders.getItemByName("showOrdersWithProductMain")).willReturn(showOrdersWithProductMain);
        given(orders.getItemByName("showOrdersWithProductPlanned")).willReturn(showOrdersWithProductPlanned);

        // when
        productDetailsViewHooksO.updateRibbonState(view);

        // then
        verify(showOrdersWithProductMain).setEnabled(false);
        verify(showOrdersWithProductPlanned).setEnabled(false);
    }

    @Test
    public void shouldUpdateRibbonStateIfProductIsSaved() {
        // given
        given(product.getId()).willReturn(L_ID);

        given(product.getBelongsToField("technologyGroup")).willReturn(technologyGroup);

        given(view.getComponentByReference(SleektivViewConstants.L_WINDOW)).willReturn((ComponentState) window);

        given(window.getRibbon()).willReturn(ribbon);

        given(ribbon.getGroupByName("orders")).willReturn(orders);

        given(orders.getItemByName("showOrdersWithProductMain")).willReturn(showOrdersWithProductMain);
        given(orders.getItemByName("showOrdersWithProductPlanned")).willReturn(showOrdersWithProductPlanned);

        // when
        productDetailsViewHooksO.updateRibbonState(view);

        // then
        verify(showOrdersWithProductMain).setEnabled(true);
        verify(showOrdersWithProductPlanned).setEnabled(true);
    }

}
