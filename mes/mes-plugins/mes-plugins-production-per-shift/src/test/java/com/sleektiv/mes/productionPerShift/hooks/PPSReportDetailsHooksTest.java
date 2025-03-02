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
package com.sleektiv.mes.productionPerShift.hooks;

import com.sleektiv.mes.productionPerShift.constants.PPSReportFields;
import com.sleektiv.mes.productionPerShift.constants.ProductionPerShiftConstants;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PPSReportDetailsHooksTest {

    

    private PPSReportDetailsHooks hooks;

    @Mock
    private DataDefinitionService dataDefinitionService;

    @Mock
    private ViewDefinitionState view;

    @Mock
    private FormComponent reportForm;

    @Mock
    private FieldComponent fieldComponent;

    @Mock
    private DataDefinition reportDD;

    @Mock
    private Entity report;

    @Before
    public void init() {
        hooks = new PPSReportDetailsHooks();

        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(hooks, "dataDefinitionService", dataDefinitionService);
    }

    @Test
    public void shouldntDisableFieldsWhenEntityIsntSaved() {
        // given
        given(view.getComponentByReference(SleektivViewConstants.L_FORM)).willReturn(reportForm);

        given(view.getComponentByReference(PPSReportFields.NUMBER)).willReturn(fieldComponent);
        given(view.getComponentByReference(PPSReportFields.NAME)).willReturn(fieldComponent);
        given(view.getComponentByReference(PPSReportFields.DATE_FROM)).willReturn(fieldComponent);
        given(view.getComponentByReference(PPSReportFields.DATE_TO)).willReturn(fieldComponent);

        given(reportForm.getEntityId()).willReturn(null);

        // when
        hooks.disableFields(view);

        // then
        verify(fieldComponent, times(4)).setEnabled(true);
    }

    @Test
    public void shouldntDisableFieldsWhenEntityIsSavedAndReportIsntGenerated() {
        // given
        given(view.getComponentByReference(SleektivViewConstants.L_FORM)).willReturn(reportForm);

        given(view.getComponentByReference(PPSReportFields.NUMBER)).willReturn(fieldComponent);
        given(view.getComponentByReference(PPSReportFields.NAME)).willReturn(fieldComponent);
        given(view.getComponentByReference(PPSReportFields.DATE_FROM)).willReturn(fieldComponent);
        given(view.getComponentByReference(PPSReportFields.DATE_TO)).willReturn(fieldComponent);

        given(reportForm.getEntityId()).willReturn(1L);

        given(dataDefinitionService.get(ProductionPerShiftConstants.PLUGIN_IDENTIFIER,
                ProductionPerShiftConstants.MODEL_PPS_REPORT)).willReturn(reportDD);
        given(reportDD.get(1L)).willReturn(report);

        given(report.getBooleanField(PPSReportFields.GENERATED)).willReturn(false);

        // when
        hooks.disableFields(view);

        // then
        verify(fieldComponent, times(4)).setEnabled(true);
    }

    @Test
    public void shouldDisableFieldsWhenEntityIsSavedAndReportIsGenerated() {
        // given
        given(view.getComponentByReference(SleektivViewConstants.L_FORM)).willReturn(reportForm);

        given(view.getComponentByReference(PPSReportFields.NUMBER)).willReturn(fieldComponent);
        given(view.getComponentByReference(PPSReportFields.NAME)).willReturn(fieldComponent);
        given(view.getComponentByReference(PPSReportFields.DATE_FROM)).willReturn(fieldComponent);
        given(view.getComponentByReference(PPSReportFields.DATE_TO)).willReturn(fieldComponent);

        given(reportForm.getEntityId()).willReturn(1L);

        given(dataDefinitionService.get(ProductionPerShiftConstants.PLUGIN_IDENTIFIER,
                ProductionPerShiftConstants.MODEL_PPS_REPORT)).willReturn(reportDD);
        given(reportDD.get(1L)).willReturn(report);

        given(report.getBooleanField(PPSReportFields.GENERATED)).willReturn(true);

        // when
        hooks.disableFields(view);

        // then
        verify(fieldComponent, times(4)).setEnabled(false);
    }

}
