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
package com.sleektiv.mes.productFlowThruDivision.criteriaModifiers;

import com.sleektiv.mes.technologies.OperationComponentDataProvider;
import com.sleektiv.mes.technologies.constants.OperationProductInComponentFields;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductsFlowInCriteriaModifiers {

    public static final String TECHNOLOGY_PARAMETER = "technologyID";

    @Autowired
    private OperationComponentDataProvider operationComponentDataProvider;

    public void showComponents(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        Long technologyId;
        if (filterValue.has(TECHNOLOGY_PARAMETER)) {
            technologyId = filterValue.getLong(TECHNOLOGY_PARAMETER);

            List<Long> ids = operationComponentDataProvider.getComponentsForTechnology(technologyId);

            if (!ids.isEmpty()) {
                scb.add(SearchRestrictions
                        .in("id", ids));
                return;
            }

        }

        technologyId = 0L;

        scb.createAlias(OperationProductInComponentFields.OPERATION_COMPONENT, "c").add(
                SearchRestrictions.eq("c.technology.id", technologyId));
    }

    public void showIntermediateIn(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        Long technologyId;
        if (filterValue.has(TECHNOLOGY_PARAMETER)) {
            technologyId = filterValue.getLong(TECHNOLOGY_PARAMETER);

            List<Long> ids = operationComponentDataProvider.getIntermediateInProductsForTechnology(technologyId);

            if (!ids.isEmpty()) {
                scb.add(SearchRestrictions
                        .in("id", ids));
                return;
            }

        }

        technologyId = 0L;

        scb.createAlias(OperationProductInComponentFields.OPERATION_COMPONENT, "c").add(
                SearchRestrictions.eq("c.technology.id", technologyId));
    }

    public void showIntermediateOut(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        Long technologyId;
        if (filterValue.has(TECHNOLOGY_PARAMETER)) {
            technologyId = filterValue.getLong(TECHNOLOGY_PARAMETER);

            List<Long> ids = operationComponentDataProvider.getIntermediateOutProductsForTechnology(technologyId);

            if (!ids.isEmpty()) {
                scb.add(SearchRestrictions
                        .in("id", ids));
                return;
            }

        }

        technologyId = 0L;

        scb.createAlias(OperationProductInComponentFields.OPERATION_COMPONENT, "c").add(
                SearchRestrictions.eq("c.technology.id", technologyId));
    }

    public void showWaste(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        Long technologyId;
        if (filterValue.has(TECHNOLOGY_PARAMETER)) {
            technologyId = filterValue.getLong(TECHNOLOGY_PARAMETER);

            List<Long> ids = operationComponentDataProvider.getWasteProductsForTechnology(technologyId);

            if (!ids.isEmpty()) {
                scb.add(SearchRestrictions.in("id", ids));
                return;
            }

        }

        technologyId = 0L;

        scb.createAlias(OperationProductInComponentFields.OPERATION_COMPONENT, "c").add(
                SearchRestrictions.eq("c.technology.id", technologyId));
    }

    public void showFinal(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        Long technologyId;
        if (filterValue.has(TECHNOLOGY_PARAMETER)) {
            technologyId = filterValue.getLong(TECHNOLOGY_PARAMETER);

            List<Long> ids = operationComponentDataProvider.getFinalProductsForTechnology(technologyId);

            if (!ids.isEmpty()) {
                scb.add(SearchRestrictions
                        .in("id", ids));
                return;
            }

        }

        technologyId = 0L;

        scb.createAlias(OperationProductInComponentFields.OPERATION_COMPONENT, "c").add(
                SearchRestrictions.eq("c.technology.id", technologyId));
    }

    public void showOPOCTechnology(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        Long technologyId;
        if (!filterValue.has(TECHNOLOGY_PARAMETER)) {
            technologyId = 0L;
        } else {
            technologyId = filterValue.getLong(TECHNOLOGY_PARAMETER);
        }
        scb.createAlias(OperationProductInComponentFields.OPERATION_COMPONENT, "c").add(
                SearchRestrictions.eq("c.technology.id", technologyId));
    }

}
