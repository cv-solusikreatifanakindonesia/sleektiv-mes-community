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
package com.sleektiv.mes.workPlans.pdf.document.operation.component;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.report.api.FontUtils;

@Component
public class OperationProductOutTableHeader {

    private TranslationService translationService;

    @Autowired
    public OperationProductOutTableHeader(final TranslationService translationService) {
        this.translationService = translationService;
    }

    public OperationProductOutTableHeader() {
    }

    public void print(final Document document, final Locale locale) throws DocumentException {
        document.add(paragraph(title(locale)));
    }

    private Paragraph paragraph(final String title) {
        return new Paragraph(title, FontUtils.getDejavuBold10Dark());
    }

    private String title(final Locale locale) {
        return translationService.translate("workPlans.workPlan.report.productsOutTable", locale);
    }

}
