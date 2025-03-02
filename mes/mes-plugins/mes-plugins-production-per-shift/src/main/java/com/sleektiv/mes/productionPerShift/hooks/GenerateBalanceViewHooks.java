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

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sleektiv.localization.api.utils.DateUtils;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.CheckBoxComponent;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.Ribbon;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class GenerateBalanceViewHooks {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateBalanceViewHooks.class);

    public void onBeforeRender(final ViewDefinitionState viewState) {
        toggleRibbonExportButtons(viewState);
        CheckBoxComponent isInitializedCheckbox = (CheckBoxComponent) findComponent(viewState, "viewIsInitialized");
        if (isInitializedCheckbox != null && !isInitializedCheckbox.isChecked()) {
            fillDeviationThresholdUnit(viewState);
            fillDateFieldsWithCurrentWorkWeekBounds(viewState);
            isInitializedCheckbox.setChecked(true);
        }
    }

    private void toggleRibbonExportButtons(final ViewDefinitionState viewState) {
        WindowComponent window = (WindowComponent) findComponent(viewState, SleektivViewConstants.L_WINDOW);
        if (window == null) {
            return;
        }
        Ribbon ribbon = window.getRibbon();
        RibbonGroup genericExportGroup = ribbon.getGroupByName("genericExport");
        if (genericExportGroup == null) {
            return;
        }
        FormComponent form = (FormComponent) findComponent(viewState, SleektivViewConstants.L_FORM);
        boolean hasGeneratedBalances = form.getEntityId() != null;
        for (RibbonActionItem ribbonExportButton : genericExportGroup.getItems()) {
            ribbonExportButton.setEnabled(hasGeneratedBalances);
            ribbonExportButton.requestUpdate(true);
        }
    }

    private void fillDeviationThresholdUnit(final ViewDefinitionState viewState) {
        FieldComponent deviationThresholdUnitComponent = (FieldComponent) findComponent(viewState, "deviationThresholdUnit");
        deviationThresholdUnitComponent.setFieldValue("%");
        deviationThresholdUnitComponent.requestComponentUpdateState();
    }

    private void fillDateFieldsWithCurrentWorkWeekBounds(final ViewDefinitionState viewState) {
        DateTime now = DateTime.now();

        LocalDate currentWeekMonday = now.withDayOfWeek(DateTimeConstants.MONDAY).toLocalDate();
        fillDateComponent(viewState, "fromDate", currentWeekMonday);

        LocalDate currentWeekFriday = now.withDayOfWeek(DateTimeConstants.FRIDAY).toLocalDate();
        fillDateComponent(viewState, "toDate", currentWeekFriday);
    }

    private void fillDateComponent(final ViewDefinitionState viewState, final String referenceName, final LocalDate localDate) {
        FieldComponent dateFieldComponent = (FieldComponent) findComponent(viewState, referenceName);
        if (dateFieldComponent != null) {
            dateFieldComponent.setFieldValue(DateUtils.toDateString(localDate.toDate()));
            dateFieldComponent.requestComponentUpdateState();
        }
    }

    private ComponentState findComponent(final ViewDefinitionState viewState, final String referenceName) {
        ComponentState component = viewState.getComponentByReference(referenceName);
        if (component == null && LOGGER.isWarnEnabled()) {
            LOGGER.warn(String.format("Can't find field component with reference name = '%s'", referenceName));
        }
        return component;
    }

}
