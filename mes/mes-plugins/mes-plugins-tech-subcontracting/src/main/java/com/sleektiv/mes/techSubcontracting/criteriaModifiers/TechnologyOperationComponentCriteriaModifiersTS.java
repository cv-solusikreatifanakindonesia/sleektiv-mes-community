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
package com.sleektiv.mes.techSubcontracting.criteriaModifiers;

import com.sleektiv.mes.techSubcontracting.constants.TechnologyOperationComponentFieldsTS;
import com.sleektiv.model.api.search.JoinType;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import org.springframework.stereotype.Service;

@Service
public class TechnologyOperationComponentCriteriaModifiersTS {

    public static final String L_TECHNOLOGY = "technology";

    private static final String L_ID = "id";

    private static final String DOT = ".";

    public void restrictTechnologyOperationComponentsToOrderTechnology(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        if (filterValue.has(L_TECHNOLOGY)) {
            Long technologyId = filterValue.getLong(L_TECHNOLOGY);

            scb.createAlias(L_TECHNOLOGY, L_TECHNOLOGY, JoinType.LEFT);
            scb.add(SearchRestrictions.eq(L_TECHNOLOGY + DOT + L_ID, technologyId));
            scb.add(SearchRestrictions.eq(TechnologyOperationComponentFieldsTS.IS_SUBCONTRACTING, true));
        } else {
            scb.add(SearchRestrictions.idEq(-1));
        }
    }

}
