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
package com.sleektiv.mes.assignmentToShift.hooks;

import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchCriterion;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static com.sleektiv.mes.assignmentToShift.constants.OccupationType.WORK_ON_LINE;
import static com.sleektiv.mes.assignmentToShift.constants.StaffAssignmentToShiftFields.*;
import static com.sleektiv.model.constants.DictionaryItemFields.NAME;
import static com.sleektiv.model.constants.DictionaryItemFields.TECHNICAL_CODE;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class StaffAssignmentToShiftDetailsHooksTest {

    private StaffAssignmentToShiftDetailsHooks detailsHooks;

    @Mock
    private ViewDefinitionState view;

    @Mock
    private DataDefinitionService dataDefinitionService;

    @Mock
    SearchCriteriaBuilder builder;

    @Mock
    private Entity dictionary;

    @Mock
    private DataDefinition dataDefinition;

    @Mock
    private FormComponent staffAssignmentToShiftForm;

    @Mock
    private FieldComponent occupationType, productionLine, occupationTypeName;

    @Before
    public void init() {
        detailsHooks = new StaffAssignmentToShiftDetailsHooks();
        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(detailsHooks, "dataDefinitionService", dataDefinitionService);

        given(view.getComponentByReference(SleektivViewConstants.L_FORM)).willReturn(staffAssignmentToShiftForm);

        given(view.getComponentByReference(OCCUPATION_TYPE)).willReturn(occupationType);
        given(view.getComponentByReference(PRODUCTION_LINE)).willReturn(productionLine);
        given(view.getComponentByReference(OCCUPATION_TYPE_NAME)).willReturn(occupationTypeName);

        given(dataDefinitionService.get("sleektivModel", "dictionaryItem")).willReturn(dataDefinition);
        given(dataDefinition.find()).willReturn(builder);

    }

    @Test
    public void shouldEnabledProductionLineAndDisableOccupationTypeNameFieldsWhenWorkOnLineIsSelected() {
        // given
        String dictionaryName = "Praca na linii";
        String technicalCode = "01workOnLine";

        given(occupationType.getFieldValue()).willReturn(dictionaryName);
        SearchCriterion criterion = SearchRestrictions.eq(NAME, dictionaryName);
        given(builder.add(criterion)).willReturn(builder);
        given(builder.uniqueResult()).willReturn(dictionary);
        given(dictionary.getStringField(TECHNICAL_CODE)).willReturn(technicalCode);

        // when
        detailsHooks.setFieldsEnabledWhenTypeIsSpecific(view);

        // then
        verify(productionLine).setVisible(true);
        verify(occupationTypeName).setVisible(false);
    }

    @Test
    public void shouldDisabledProductionLineAndEnableOccupationTypeNameFieldsWhenOtherCaseIsSelected() {
        // given
        String dictionaryName = "Inne zadania";
        String technicalCode = "02otherCase";

        given(occupationType.getFieldValue()).willReturn(dictionaryName);
        SearchCriterion criterion = SearchRestrictions.eq(NAME, dictionaryName);
        given(builder.add(criterion)).willReturn(builder);
        given(builder.uniqueResult()).willReturn(dictionary);
        given(dictionary.getStringField(TECHNICAL_CODE)).willReturn(technicalCode);

        // when
        detailsHooks.setFieldsEnabledWhenTypeIsSpecific(view);

        // then
        verify(productionLine).setVisible(false);
        verify(occupationTypeName).setVisible(true);
    }

    @Test
    public void shouldDisabledProductionLineAndOccupationTypeNameFieldsWhenMixDictionaryIsSelected() {
        // given
        String dictionaryName = "MIX";

        given(occupationType.getFieldValue()).willReturn(dictionaryName);
        SearchCriterion criterion = SearchRestrictions.eq(NAME, dictionaryName);
        given(builder.add(criterion)).willReturn(builder);
        given(builder.uniqueResult()).willReturn(dictionary);
        given(dictionary.getStringField(TECHNICAL_CODE)).willReturn(Mockito.anyString());

        // when
        detailsHooks.setFieldsEnabledWhenTypeIsSpecific(view);

        // then
        verify(productionLine).setVisible(false);
        verify(occupationTypeName).setVisible(false);
    }

    @Test
    public void shouldDisabledProductionLineAndOccupationTypeNameFieldsWhenEmptyIsSelected() {
        // given
        String dictionaryName = "";

        given(occupationType.getFieldValue()).willReturn(dictionaryName);
        SearchCriterion criterion = SearchRestrictions.eq(NAME, dictionaryName);
        given(builder.add(criterion)).willReturn(builder);
        given(builder.uniqueResult()).willReturn(null);

        // when
        detailsHooks.setFieldsEnabledWhenTypeIsSpecific(view);

        // then
        verify(productionLine).setVisible(false);
        verify(occupationTypeName).setVisible(false);
    }

    @Test
    public void shouldntSetOccupationTypeToDefaultWhenFormIsSaved() {
        // given
        String dictionaryName = "Praca na linii";

        given(staffAssignmentToShiftForm.getEntityId()).willReturn(1L);
        given(occupationType.getFieldValue()).willReturn(dictionaryName);

        // when
        detailsHooks.setOccupationTypeToDefault(view);

        // then
        verify(occupationType, never()).setFieldValue(Mockito.anyString());
    }

    @Test
    public void shouldntSetOccupationTypeToDefaultWhenDictionaryIsNull() {
        // given
        given(staffAssignmentToShiftForm.getEntityId()).willReturn(null);
        given(occupationType.getFieldValue()).willReturn(null);

        SearchCriterion criterion = SearchRestrictions.eq(TECHNICAL_CODE, WORK_ON_LINE.getStringValue());
        given(builder.add(criterion)).willReturn(builder);
        given(builder.uniqueResult()).willReturn(null);

        // when
        detailsHooks.setOccupationTypeToDefault(view);

        // then
        verify(occupationType, never()).setFieldValue(Mockito.anyString());
    }

    @Test
    public void shouldSetOccupationTypeToDefaultWhenDictionary() {
        // given
        given(staffAssignmentToShiftForm.getEntityId()).willReturn(null);
        given(occupationType.getFieldValue()).willReturn(null);

        SearchCriterion criterion = SearchRestrictions.eq(TECHNICAL_CODE, WORK_ON_LINE.getStringValue());
        given(builder.add(criterion)).willReturn(builder);
        given(builder.uniqueResult()).willReturn(dictionary);
        given(dictionary.getStringField(NAME)).willReturn(Mockito.anyString());

        // when
        detailsHooks.setOccupationTypeToDefault(view);

        // then
        verify(occupationType).setFieldValue(Mockito.anyString());
    }

}
