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
package com.sleektiv.mes.basic.imports.parsers;

import com.sleektiv.mes.basic.imports.helpers.CellErrorsAccessor;
import com.sleektiv.mes.basic.imports.helpers.CellParser;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BooleanCellParser implements CellParser {

    private static final String L_SLEEKTIV_VIEW_VALIDATE_FIELD_ERROR_CUSTOM = "sleektivView.validate.field.error.custom";

    private static final String L_TRUE_BOOLEAN_PATTERN = "^(true|yes|tak|t|1)$";

    @Override
    public void parse(final String cellValue, final String dependentCellValue, final CellErrorsAccessor errorsAccessor,
            final Consumer<Object> valueConsumer) {

        Optional<Boolean> mayBeValue = parse(cellValue);

        if (mayBeValue.isPresent()) {
            Boolean value = mayBeValue.get();

            valueConsumer.accept(value);
        } else {
            errorsAccessor.addError(L_SLEEKTIV_VIEW_VALIDATE_FIELD_ERROR_CUSTOM);
        }

    }

    private Optional<Boolean> parse(final String cellValue) {
        Pattern trueBooleanPattern = Pattern.compile(L_TRUE_BOOLEAN_PATTERN, Pattern.CASE_INSENSITIVE);

        Matcher trueBooleanMatcher = trueBooleanPattern.matcher(cellValue);

        if (trueBooleanMatcher.matches()) {
            return Optional.of(Boolean.TRUE);
        } else {
            return Optional.of(Boolean.FALSE);
        }
    }

}
