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
package com.sleektiv.mes.technologies.criteriaModifiers;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.sleektiv.mes.technologies.constants.TechnologyFields;
import com.sleektiv.mes.technologies.states.constants.TechnologyStateStringValues;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;

@Service
public class TechnologyCriteriaModifiers {

    public void showPatternTechnologyFromOperationProductInComponentDto(final SearchCriteriaBuilder scb) {
        scb.add(SearchRestrictions.eq("activeTechnology", true));
    }

    public void showAcceptedPatternTechnology(final SearchCriteriaBuilder scb) {
        scb.add(SearchRestrictions.or(SearchRestrictions.eq(TechnologyFields.STATE, TechnologyStateStringValues.ACCEPTED),
                SearchRestrictions.and(SearchRestrictions.eq(TechnologyFields.TEMPLATE, true),
                        SearchRestrictions.eq(TechnologyFields.IS_TEMPLATE_ACCEPTED, true))));
    }

    public void showAcceptedAndCheckedPatternTechnology(final SearchCriteriaBuilder scb) {
        scb.add(SearchRestrictions.in(TechnologyFields.STATE,
                Lists.newArrayList(TechnologyStateStringValues.ACCEPTED, TechnologyStateStringValues.CHECKED)));
    }

    public void showDraftAcceptedAndCheckedPatternTechnology(final SearchCriteriaBuilder scb) {
        scb.add(SearchRestrictions.in(TechnologyFields.STATE,
                Lists.newArrayList(TechnologyStateStringValues.DRAFT, TechnologyStateStringValues.ACCEPTED, TechnologyStateStringValues.CHECKED)));
    }

    public void showPatternTechnologyWithoutGroup(final SearchCriteriaBuilder scb) {
        scb.add(SearchRestrictions.isNull(TechnologyFields.TECHNOLOGY_GROUP));
    }
}
