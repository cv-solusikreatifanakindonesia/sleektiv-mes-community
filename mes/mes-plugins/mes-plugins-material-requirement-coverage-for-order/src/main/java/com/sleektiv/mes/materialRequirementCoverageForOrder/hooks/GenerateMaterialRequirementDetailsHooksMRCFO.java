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
package com.sleektiv.mes.materialRequirementCoverageForOrder.hooks;

import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.sleektiv.localization.api.utils.DateUtils;
import com.sleektiv.mes.materialRequirementCoverageForOrder.aspects.MRCCriteriaModifiersMRCFOOverideAspect;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class GenerateMaterialRequirementDetailsHooksMRCFO {

    private static final Logger LOG = LoggerFactory.getLogger(GenerateMaterialRequirementDetailsHooksMRCFO.class);

    



    private static final String L_COVERAGE = "coverage";

    public final void onBeforeRender(final ViewDefinitionState view) {
        fillFieldsForOrder(view);
        setCriteriaModifierParameters(view);
    }

    private void setCriteriaModifierParameters(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Entity mRCForOrder = form.getEntity();

        Entity order = mRCForOrder.getBelongsToField("order");

        GridComponent gridProductsComponent = (GridComponent) view.getComponentByReference("coverageProducts");

        FilterValueHolder gridProductsComponentHolder = gridProductsComponent.getFilterValue();
        if (order != null) {
            gridProductsComponentHolder.put(MRCCriteriaModifiersMRCFOOverideAspect.ORDER_PARAMETER, order.getId());
        }
        gridProductsComponent.setFilterValue(gridProductsComponentHolder);

    }

    private void fillFieldsForOrder(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Entity mRCForOrder = form.getEntity();

        Entity order = mRCForOrder.getBelongsToField("order");

        if (mRCForOrder.getId() == null && order != null) {
            if (order.getDateField(OrderFields.DATE_FROM) == null) {
                return;
            }
            FieldComponent coverageToDate = (FieldComponent) view.getComponentByReference("coverageToDate");
            if (coverageToDate.getFieldValue() == null) {
                coverageToDate.setFieldValue(new SimpleDateFormat(DateUtils.L_DATE_TIME_FORMAT, LocaleContextHolder.getLocale())
                        .format((order.getDateField(OrderFields.START_DATE))));
            }
        }

    }
}
