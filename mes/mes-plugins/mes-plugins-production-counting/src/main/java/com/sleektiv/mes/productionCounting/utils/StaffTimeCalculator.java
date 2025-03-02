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
package com.sleektiv.mes.productionCounting.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.productionCounting.constants.ProductionCountingConstants;
import com.sleektiv.mes.productionCounting.constants.StaffWorkTimeFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchOrders;
import com.sleektiv.model.api.search.SearchProjection;
import com.sleektiv.model.api.search.SearchProjections;
import com.sleektiv.model.api.search.SearchRestrictions;

@Service
public class StaffTimeCalculator {

    private static final String TOTAL_LABOR_PROJECTION_ALIAS = "totalLaborProjection";

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public Long countTotalLaborTime(final Long productionRecordId) {
        SearchCriteriaBuilder scb = getStaffWorkTimeDD().find();
        SearchProjection totalLaborProjection = SearchProjections.alias(SearchProjections.sum(StaffWorkTimeFields.LABOR_TIME),
                TOTAL_LABOR_PROJECTION_ALIAS);
        SearchProjection rowCntProjection = SearchProjections.rowCount();
        scb.setProjection(SearchProjections.list().add(rowCntProjection).add(totalLaborProjection));
        scb.add(SearchRestrictions.belongsTo(StaffWorkTimeFields.PRODUCTION_RECORD,
                ProductionCountingConstants.PLUGIN_IDENTIFIER, ProductionCountingConstants.MODEL_PRODUCTION_TRACKING,
                productionRecordId));

        // Fix for missing id column. Touch on your own risk.
        scb.addOrder(SearchOrders.asc(TOTAL_LABOR_PROJECTION_ALIAS));
        Entity res = scb.setMaxResults(1).uniqueResult();
        if (res == null) {
            return 0L;
        }
        Long totalLabor = (Long) res.getField(TOTAL_LABOR_PROJECTION_ALIAS);
        if (totalLabor == null) {
            return 0L;
        }
        return totalLabor;
    }

    private DataDefinition getStaffWorkTimeDD() {
        return dataDefinitionService.get(ProductionCountingConstants.PLUGIN_IDENTIFIER,
                ProductionCountingConstants.MODEL_STAFF_WORK_TIME);
    }
}
