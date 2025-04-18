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
package com.sleektiv.mes.materialFlowResources.criteriaModifiers;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.materialFlowResources.constants.DivisionFieldsMFR;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;

@Service
public class DivisionCriteriaModifiers {

    private static final String L_ID = "id";

    private static final String L_LOCATION = "location";

    public void restrictDivisionsToLocation(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        if (!filterValue.has(L_LOCATION)) {
            return;
        }

        Long locationId = filterValue.getLong(L_LOCATION);

        scb.add(SearchRestrictions.or(SearchRestrictions.eq(DivisionFieldsMFR.COMPONENTS_LOCATION + "." + L_ID, locationId),
                SearchRestrictions.eq(DivisionFieldsMFR.COMPONENTS_OUTPUT_LOCATION + "." + L_ID, locationId),
                SearchRestrictions.eq(DivisionFieldsMFR.PRODUCTS_INPUT_LOCATION + "." + L_ID, locationId)));
    }
}
