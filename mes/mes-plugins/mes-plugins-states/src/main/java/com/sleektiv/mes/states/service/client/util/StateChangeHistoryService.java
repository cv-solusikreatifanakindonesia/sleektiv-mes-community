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
package com.sleektiv.mes.states.service.client.util;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sleektiv.model.api.search.CustomRestriction;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;

@Service
public class StateChangeHistoryService {

    public final CustomRestriction buildStatusRestriction(final String statusFieldName, final List<String> allowedStatuses) {

        return new CustomRestriction() {

            @Override
            public void addRestriction(final SearchCriteriaBuilder scb) {
                scb.add(SearchRestrictions.in(statusFieldName, allowedStatuses));
            }
        };
    }

}
