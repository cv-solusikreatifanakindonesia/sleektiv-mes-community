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
package com.sleektiv.plugins.customTranslations.internal.hooks;

import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.CustomRestriction;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchCriterion;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.ribbon.Ribbon;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;
import com.sleektiv.view.internal.components.window.WindowComponentState;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static com.sleektiv.customTranslation.constants.CustomTranslationFields.LOCALE;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@Ignore
public class CustomTranslationsListViewHooksTest {

    private static final String L_CLEAN = "clean";

    private static final String L_CLEAN_CUSTOM_TRANSLATIONS = "cleanCustomTranslations";

    private CustomTranslationsListViewHooks customTranslationsListViewHooks;

    @Mock
    private ViewDefinitionState view;

    @Mock
    private GridComponent customTranslationsGrid;

    @Mock
    private FieldComponent localeField;

    @Mock
    private WindowComponentState window;

    @Mock
    private Ribbon ribbon;

    @Mock
    private RibbonGroup clean;

    @Mock
    private RibbonActionItem cleanCustomTranslations;

    @Mock
    private List<Entity> customTranslations;

    @Mock
    private CustomRestriction customRestriction;

    @Mock
    private SearchCriteriaBuilder searchCriteriaBuilder;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        customTranslationsListViewHooks = new CustomTranslationsListViewHooks();
    }

    @Test
    public void shouldUpdateRibbonStateIfCustomTranslationIsSelected() {
        // given
        given(view.getComponentByReference(SleektivViewConstants.L_GRID)).willReturn(customTranslationsGrid);
        given(customTranslationsGrid.getSelectedEntities()).willReturn(customTranslations);
        given(customTranslations.isEmpty()).willReturn(false);

        given(view.getComponentByReference(SleektivViewConstants.L_WINDOW)).willReturn((ComponentState) window);

        given(window.getRibbon()).willReturn(ribbon);

        given(ribbon.getGroupByName(L_CLEAN)).willReturn(clean);

        given(clean.getItemByName(L_CLEAN_CUSTOM_TRANSLATIONS)).willReturn(cleanCustomTranslations);

        // when
        customTranslationsListViewHooks.updateRibbonState(view);

        // then
        verify(cleanCustomTranslations).setEnabled(true);
    }

    @Test
    public void shouldntUpdateRibbonStateIfCustomTranslationIsntSelected() {
        // given
        given(view.getComponentByReference(SleektivViewConstants.L_GRID)).willReturn(customTranslationsGrid);
        given(customTranslationsGrid.getSelectedEntities()).willReturn(customTranslations);
        given(customTranslations.isEmpty()).willReturn(true);

        given(view.getComponentByReference(SleektivViewConstants.L_WINDOW)).willReturn((ComponentState) window);

        given(window.getRibbon()).willReturn(ribbon);

        given(ribbon.getGroupByName(L_CLEAN)).willReturn(clean);

        given(clean.getItemByName(L_CLEAN_CUSTOM_TRANSLATIONS)).willReturn(cleanCustomTranslations);

        // when
        customTranslationsListViewHooks.updateRibbonState(view);

        // then
        verify(cleanCustomTranslations).setEnabled(false);
    }

    @Test
    public void shouldSetLocaleToDefaultIfLocaleIsNull() {
        // given
        given(view.getComponentByReference(LOCALE)).willReturn(localeField);

        given(localeField.getFieldValue()).willReturn(null);

        // when
        customTranslationsListViewHooks.setLocaleToDefault(view);

        // then
        verify(localeField).setFieldValue(Mockito.anyString());
    }

    @Test
    public void shouldntSetLocaleToDefaultIfLocaleIsntNull() {
        // given
        String locale = "pl";

        given(view.getComponentByReference(LOCALE)).willReturn(localeField);

        given(localeField.getFieldValue()).willReturn(locale);

        // when
        customTranslationsListViewHooks.setLocaleToDefault(view);

        // then
        verify(localeField, never()).setFieldValue(Mockito.anyString());
    }

    @Test
    public void shouldSetCustomRestrictionsToNullWhenAddDiscriminatorRestrictionToGridListenerIfLocaleIsEmpty() {
        // given
        given(view.getComponentByReference(SleektivViewConstants.L_GRID)).willReturn(customTranslationsGrid);

        given(view.getComponentByReference(LOCALE)).willReturn(localeField);

        given(localeField.getFieldValue()).willReturn(null);

        // when
        customTranslationsListViewHooks.addDiscriminatorRestrictionToGrid(view, null, null);

        // then
        verify(customTranslationsGrid).setCustomRestriction(null);
    }

    @Test
    public void shouldSetCustomRestrictionsWhenAddDiscriminatorRestrictionToGridListenerIfLocaleIsntEmpty() {
        // given
        String locale = "pl";

        given(view.getComponentByReference(SleektivViewConstants.L_GRID)).willReturn(customTranslationsGrid);

        given(view.getComponentByReference(LOCALE)).willReturn(localeField);

        given(localeField.getFieldValue()).willReturn(locale);

        // when
        customTranslationsListViewHooks.addDiscriminatorRestrictionToGrid(view);

        // then
        verify(customTranslationsGrid).setCustomRestriction(Mockito.any(CustomRestriction.class));
    }

    @Test
    public void shouldSetCustomRestrictionsToNullWhenAddDiscriminatorRestrictionToGridIfLocaleIsEmpty() {
        // given
        given(view.getComponentByReference(SleektivViewConstants.L_GRID)).willReturn(customTranslationsGrid);

        given(view.getComponentByReference(LOCALE)).willReturn(localeField);

        given(localeField.getFieldValue()).willReturn(null);

        // when
        customTranslationsListViewHooks.addDiscriminatorRestrictionToGrid(view);

        // then
        verify(customTranslationsGrid).setCustomRestriction(null);
    }

    @Test
    public void shouldSetCustomRestrictionsWhenAddDiscriminatorRestrictionToGridIfLocaleIsntEmpty() {
        // given
        String locale = "pl";

        given(view.getComponentByReference(SleektivViewConstants.L_GRID)).willReturn(customTranslationsGrid);

        given(view.getComponentByReference(LOCALE)).willReturn(localeField);

        given(localeField.getFieldValue()).willReturn(locale);

        ArgumentCaptor<CustomRestriction> customRestrictionCaptor = ArgumentCaptor.forClass(CustomRestriction.class);

        // when
        customTranslationsListViewHooks.addDiscriminatorRestrictionToGrid(view);

        // then
        verify(customTranslationsGrid).setCustomRestriction(Mockito.any(CustomRestriction.class));
        verify(customTranslationsGrid).setCustomRestriction(customRestrictionCaptor.capture());
        customRestriction = customRestrictionCaptor.getValue();
        customRestriction.addRestriction(searchCriteriaBuilder);
        verify(searchCriteriaBuilder).add(Mockito.any(SearchCriterion.class));
    }

}