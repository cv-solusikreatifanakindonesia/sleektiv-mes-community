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
package com.sleektiv.model.internal.search;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.sleektiv.model.api.search.SearchCriterion;
import com.sleektiv.model.api.search.SearchDisjunction;

public class SearchDisjunctionImpl implements SearchDisjunction {

    private final Disjunction disjunction;

    public SearchDisjunctionImpl() {
        disjunction = Restrictions.disjunction();
    }

    @Override
    public Criterion getHibernateCriterion() {
        return disjunction;
    }

    @Override
    public void add(final SearchCriterion criterion) {
        disjunction.add(criterion.getHibernateCriterion());

    }

}
