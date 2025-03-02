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
package com.sleektiv.mes.materialRequirementCoverageForOrder.aspects;

import com.google.common.collect.Lists;
import com.sleektiv.localization.api.utils.DateUtils;
import com.sleektiv.mes.materialRequirementCoverageForOrder.MaterialRequirementCoverageForOrderService;
import com.sleektiv.mes.materialRequirementCoverageForOrder.constans.CoverageLocationFields;
import com.sleektiv.mes.materialRequirementCoverageForOrder.constans.MaterialRequirementCoverageForOrderConstans;
import com.sleektiv.mes.orderSupplies.constants.ParameterFieldsOS;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.productFlowThruDivision.ProductFlowThruDivisionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.plugin.api.RunIfEnabled;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.AwesomeDynamicListComponent;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.i18n.LocaleContextHolder;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

@Aspect
@Configurable
@RunIfEnabled(MaterialRequirementCoverageForOrderConstans.PLUGIN_IDENTIFIER)
public class MaterialRequirementCoverageHooksAspect {



    @Autowired
    private MaterialRequirementCoverageForOrderService mRCForOrderService;

    @Autowired
    private ProductFlowThruDivisionService productFlowThruDivisionService;

    @Pointcut("execution(public void com.sleektiv.mes.orderSupplies.hooks.GenerateMaterialRequirementCoverageHooks.fillCoverageDate(..)) "
            + "&& args(view)")
    public void fillCoverageDate(final ViewDefinitionState view) {
    }

    @Around("fillCoverageDate(view)")
    public void aroundFillCoverageDate(final ProceedingJoinPoint pjp, final ViewDefinitionState view) throws Throwable {
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
        } else {
            pjp.proceed();
        }
    }

    @Pointcut("execution(private void com.sleektiv.mes.orderSupplies.hooks.GenerateMaterialRequirementCoverageHooks.copyLocationFromParameter(..)) "
            + "&& args(view, parameter)")
    public void copyLocationFromParameter(final ViewDefinitionState view, final Entity parameter) {
    }

    @Around("copyLocationFromParameter(view, parameter)")
    public void aroundcCopyLocationFromParameter(final ProceedingJoinPoint pjp, final ViewDefinitionState view,
            final Entity parameter) throws Throwable {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Entity mRCForOrder = form.getEntity();

        Entity order = mRCForOrder.getBelongsToField("order");

        if (mRCForOrder.getId() == null && order != null) {
            Set<Entity> locations = productFlowThruDivisionService
                    .getProductsLocations(order.getBelongsToField(OrderFields.TECHNOLOGY).getId());
            List<Entity> parameterCoverageLocations = parameter.getHasManyField(ParameterFieldsOS.COVERAGE_LOCATIONS);

            for (Entity parameterCoverageLocation : parameterCoverageLocations) {
                Entity location = parameterCoverageLocation
                        .getBelongsToField(com.sleektiv.mes.orderSupplies.constants.CoverageLocationFields.LOCATION);
                locations.add(location);
            }
            AwesomeDynamicListComponent locationADL = (AwesomeDynamicListComponent) view
                    .getComponentByReference("coverageLocations");
            if (locationADL.getFormComponents().isEmpty()) {
                List<Entity> locationsList = Lists.newArrayList();

                for (Entity location : locations) {

                    Entity coverageLocation = mRCForOrderService.getCoverageLocationDD().create();

                    coverageLocation.setField(CoverageLocationFields.LOCATION, location);

                    locationsList.add(coverageLocation);
                }
                locationADL.setFieldValue(locationsList);

                locationADL.requestComponentUpdateState();
            }
        } else {
            pjp.proceed();
        }
    }

}
