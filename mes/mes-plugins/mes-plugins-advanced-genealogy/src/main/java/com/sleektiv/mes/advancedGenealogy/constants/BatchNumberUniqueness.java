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
package com.sleektiv.mes.advancedGenealogy.constants;

import static com.sleektiv.model.api.search.SearchRestrictions.*;

import org.apache.commons.lang3.StringUtils;

import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchCriterion;

public enum BatchNumberUniqueness {

    GLOBALLY("01globally") {

        @Override
        public SearchCriterion buildCriterionFor(final Entity batchEntity) {
            SearchCriterion numberMatches = eq(BatchFields.NUMBER, batchEntity.getStringField(BatchFields.NUMBER));
            if (batchEntity.getId() == null) {
                return numberMatches;
            }
            SearchCriterion isNotTheSameBatch = idNe(batchEntity.getId());
            return and(isNotTheSameBatch, numberMatches);
        }
    },
    SUPPLIER("02supplier") {

        @Override
        public SearchCriterion buildCriterionFor(final Entity batchEntity) {
            Entity supplier = batchEntity.getBelongsToField(BatchFields.SUPPLIER);
            return and(GLOBALLY.buildCriterionFor(batchEntity), belongsTo(BatchFields.SUPPLIER, supplier));
        }
    },
    SUPPLIER_AND_PRODUCT("03supplierAndProduct") {

        @Override
        public SearchCriterion buildCriterionFor(final Entity batchEntity) {
            Entity product = batchEntity.getBelongsToField(BatchFields.PRODUCT);
            return and(SUPPLIER.buildCriterionFor(batchEntity), belongsTo(BatchFields.PRODUCT, product));
        }
    },

    PRODUCT("04product") {

        @Override
        public SearchCriterion buildCriterionFor(final Entity batchEntity) {
            Entity product = batchEntity.getBelongsToField(BatchFields.PRODUCT);
            return and(GLOBALLY.buildCriterionFor(batchEntity), belongsTo(BatchFields.PRODUCT, product));
        }
    };

    private String stringValue;

    private BatchNumberUniqueness(final String stringValue) {
        this.stringValue = stringValue;
    }

    public abstract SearchCriterion buildCriterionFor(final Entity batchEntity);

    public String getStringValue() {
        return stringValue;
    }

    public static final BatchNumberUniqueness parseString(final String stringToParse) {
        if (StringUtils.isBlank(stringToParse)) {
            return null;
        }
        for (BatchNumberUniqueness numberUniquenessValue : values()) {
            if (numberUniquenessValue.getStringValue().equalsIgnoreCase(stringToParse)) {
                return numberUniquenessValue;
            }
        }
        return null;
    }
}
