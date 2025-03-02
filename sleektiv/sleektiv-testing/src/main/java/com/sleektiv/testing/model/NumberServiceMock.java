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
package com.sleektiv.testing.model;

import java.math.BigDecimal;
import java.math.MathContext;

import com.sleektiv.model.api.NumberService;
import com.sleektiv.model.internal.NumberServiceImpl;

public final class NumberServiceMock {

    private NumberServiceMock() {
    }

    public static NumberService ignoringScale() {
        return new NumberServiceMockImpl(true);
    }

    public static NumberService scaleAware() {
        return new NumberServiceMockImpl(false);
    }

    private static final class NumberServiceMockImpl implements NumberService {

        private final boolean ignoreScale;

        private final NumberService numberService = new NumberServiceImpl();

        private NumberServiceMockImpl(final boolean ignoreScale) {
            this.ignoreScale = ignoreScale;
        }

        @Override
        public MathContext getMathContext() {
            return numberService.getMathContext();
        }

        @Override
        public String format(final Object obj) {
            return numberService.format(obj);
        }

        @Override
        public BigDecimal setScaleWithDefaultMathContext(final BigDecimal decimal) {
            if (ignoreScale) {
                return decimal;
            }
            return numberService.setScaleWithDefaultMathContext(decimal);
        }

        @Override
        public BigDecimal setScaleWithDefaultMathContext(final BigDecimal decimal, final int newScale) {
            if (ignoreScale) {
                return decimal;
            }
            return numberService.setScaleWithDefaultMathContext(decimal, newScale);
        }

        @Override
        public String formatWithMinimumFractionDigits(final Object obj, final int minimumFractionDigits) {
            return numberService.formatWithMinimumFractionDigits(obj, minimumFractionDigits);
        }
    }
}
