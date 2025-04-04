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
package com.sleektiv.mes.timeGapsPreview.hooks;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.sleektiv.localization.api.utils.DateUtils;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.CheckBoxComponent;
import com.sleektiv.view.api.components.FieldComponent;

@Service
public class GenerateTimeGapsViewHooks {

    public void onBeforeRender(final ViewDefinitionState viewState) {
        CheckBoxComponent isInitializedCheckbox = (CheckBoxComponent) viewState.getComponentByReference("viewIsInitialized");
        if (!isInitializedCheckbox.isChecked()) {
            setLowerTimeBoundOnEntry(viewState);
            isInitializedCheckbox.setChecked(true);
        }
    }

    private void setLowerTimeBoundOnEntry(final ViewDefinitionState viewState) {
        FieldComponent dateFromField = (FieldComponent) viewState.getComponentByReference("fromDate");
        dateFromField.setFieldValue(DateUtils.toDateTimeString(new Date()));
    }

}
