/*
 * **************************************************************************
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
 * **************************************************************************
 */
package com.sleektiv.mes.technologies.imports.parsers;

import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.mes.basic.imports.helpers.CellErrorsAccessor;
import com.sleektiv.mes.basic.imports.helpers.CellParser;
import com.sleektiv.mes.technologies.constants.WorkstationChangeoverNormChangeoverType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class WorkstationChangeoverTypeCellParser implements CellParser {

    private static final String L_SLEEKTIV_VIEW_VALIDATE_FIELD_ERROR_CUSTOM = "sleektivView.validate.field.error.custom";

    @Autowired
    private TranslationService translationService;

    @Override
    public void parse(final String cellValue, final String dependentCellValue, final CellErrorsAccessor errorsAccessor, final Consumer<Object> valueConsumer) {
        if (Objects.nonNull(cellValue)) {
            Optional<WorkstationChangeoverNormChangeoverType> match = getWorkstationChangeoverTypeByName(cellValue);

            if (match.isPresent()) {
                valueConsumer.accept(match.get().getStringValue());
            } else {
                errorsAccessor.addError(L_SLEEKTIV_VIEW_VALIDATE_FIELD_ERROR_CUSTOM);
            }
        }
    }

    private Optional<WorkstationChangeoverNormChangeoverType> getWorkstationChangeoverTypeByName(final String name) {
        return Arrays.stream(WorkstationChangeoverNormChangeoverType.values()).filter(wcnct -> translationService
                .translate("technologies.workstationChangeoverNorm.changeoverType.value." + wcnct.getStringValue(), LocaleContextHolder.getLocale())
                .equals(name)).findAny();
    }

}
