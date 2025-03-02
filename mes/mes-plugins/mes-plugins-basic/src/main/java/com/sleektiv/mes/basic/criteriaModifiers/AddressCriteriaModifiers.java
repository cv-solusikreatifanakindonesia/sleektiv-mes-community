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
package com.sleektiv.mes.basic.criteriaModifiers;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.AddressFields;
import com.sleektiv.model.api.search.JoinType;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;

@Service
public class AddressCriteriaModifiers {

    public static final String L_COMPANY_ID = "companyId";

    public void showOnlyCompanyAddresses(final SearchCriteriaBuilder searchCriteriaBuilder,
            final FilterValueHolder filterValueHolder) {
        Long companyId = null;

        if (filterValueHolder.has(L_COMPANY_ID)) {
            companyId = filterValueHolder.getLong(L_COMPANY_ID);
        } else {
            companyId = 0L;
        }

        searchCriteriaBuilder.createAlias(AddressFields.COMPANY, AddressFields.COMPANY, JoinType.INNER).add(
                SearchRestrictions.eq(AddressFields.COMPANY + ".id", companyId));
    }

}
