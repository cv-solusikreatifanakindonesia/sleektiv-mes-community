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
package com.sleektiv.mes.productionCounting.hooks;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.sleektiv.mes.productionCounting.ProductionTrackingService;
import com.sleektiv.mes.productionCounting.constants.ProductionTrackingFields;
import com.sleektiv.mes.productionCounting.states.constants.ProductionTrackingStateStringValues;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

public class ProductionTrackingDetailsHooksTest {

    

    private static final String L_PRODUCTS_TAB = "productsTab";

    private static final String L_IS_DISABLED = "isDisabled";

    private ProductionTrackingDetailsHooks productionTrackingDetailsHooks;

    @Mock
    private ProductionTrackingService productionTrackingService;

    @Mock
    private ViewDefinitionState view;

    @Mock
    private FormComponent productionTrackingForm;

    @Mock
    private FieldComponent stateField, isDisabledField;

    @Mock
    private LookupComponent orderLookup;

    @Mock
    private ComponentState productsTab;

    @Mock
    private Entity productionTracking, order;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        productionTrackingDetailsHooks = new ProductionTrackingDetailsHooks();

        ReflectionTestUtils.setField(productionTrackingDetailsHooks, "productionTrackingService", productionTrackingService);

        given(view.getComponentByReference(SleektivViewConstants.L_FORM)).willReturn(productionTrackingForm);

        given(view.getComponentByReference(ProductionTrackingFields.STATE)).willReturn(stateField);
        given(view.getComponentByReference(ProductionTrackingFields.ORDER)).willReturn(orderLookup);
        given(view.getComponentByReference(L_IS_DISABLED)).willReturn(isDisabledField);
    }

    @Test
    public void shouldSetStateToDraftWhenInitializeTrackingDetailsViewIfProductionTrackingIsntSaved() {
        // given
        given(productionTrackingForm.getEntityId()).willReturn(null);
        given(productionTrackingForm.getEntity()).willReturn(productionTracking);
        given(productionTracking.getField(ProductionTrackingFields.STATE)).willReturn(ProductionTrackingStateStringValues.DRAFT);

        // when
        productionTrackingDetailsHooks.initializeProductionTrackingDetailsView(view);

        // then
        verify(stateField, times(1)).setFieldValue(ProductionTrackingStateStringValues.DRAFT);
        verify(isDisabledField, times(1)).setFieldValue(false);
        verify(productionTrackingService, never()).setTimeAndPieceworkComponentsVisible(view, order);
    }

    @Test
    public void shouldSetStateToAcceptedWhenInitializeTrackingDetailsViewIfProductionTrackingIsSaved() {
        // given
        given(productionTrackingForm.getEntityId()).willReturn(1L);
        given(productionTrackingForm.getEntity()).willReturn(productionTracking);
        given(productionTracking.getField(ProductionTrackingFields.STATE))
                .willReturn(ProductionTrackingStateStringValues.ACCEPTED);
        given(orderLookup.getEntity()).willReturn(order);
        given(order.getBooleanField(Mockito.anyString())).willReturn(true);
        given(view.getComponentByReference(L_PRODUCTS_TAB)).willReturn(productsTab);

        // when
        productionTrackingDetailsHooks.initializeProductionTrackingDetailsView(view);

        // then
        verify(stateField, times(1)).setFieldValue(ProductionTrackingStateStringValues.ACCEPTED);
        verify(isDisabledField, times(1)).setFieldValue(false);
        verify(productionTrackingService, times(1)).setTimeAndPieceworkComponentsVisible(view, order);
    }

}
