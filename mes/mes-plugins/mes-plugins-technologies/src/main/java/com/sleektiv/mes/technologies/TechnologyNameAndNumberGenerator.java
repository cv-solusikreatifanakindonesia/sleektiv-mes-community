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
package com.sleektiv.mes.technologies;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.technologies.constants.TechnologiesConstants;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.utils.NumberGeneratorService;

@Service
public class TechnologyNameAndNumberGenerator {

    @Autowired
    private NumberGeneratorService numberGeneratorService;

    @Autowired
    private TranslationService translationService;

    public String generateNumber(final Entity product) {
        String numberPrefix = product.getField(ProductFields.NUMBER) + "-";
        return numberGeneratorService.generateNumberWithPrefix(TechnologiesConstants.PLUGIN_IDENTIFIER,
                TechnologiesConstants.MODEL_TECHNOLOGY, 3, numberPrefix);
    }

    public String generateName(final Entity product) {
        LocalDate date = LocalDate.now();
        String currentDateString = String.format("%s.%s", date.getYear(), date.getMonthValue());
        String productName = product.getStringField(ProductFields.NAME);
        String productNumber = product.getStringField(ProductFields.NUMBER);
        return translationService.translate("technologies.operation.name.default", LocaleContextHolder.getLocale(), productName,
                productNumber, currentDateString);
    }
}
