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
package com.sleektiv.mes.workPlans.pdf.document.component;

import java.util.Date;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.mes.workPlans.constants.WorkPlanFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.report.api.pdf.PdfHelper;

@Component
public class DocumentHeader {

    private static final String MSG_TITLE = "workPlans.workPlan.report.title";

    private static final String MSG_AUTHOR = "sleektivReport.commons.generatedBy.label";

    private TranslationService translationService;

    private PdfHelper pdfHelper;

    @Autowired
    public DocumentHeader(final TranslationService translationService, final PdfHelper pdfHelper) {
        this.translationService = translationService;
        this.pdfHelper = pdfHelper;
    }

    public void print(final Entity workPlan, final Document document, final Locale locale) throws DocumentException {
        pdfHelper.addDocumentHeader(document, name(workPlan), title(locale), author(locale), date(workPlan));
    }

    private Date date(final Entity workPlan) {
        return workPlan.getDateField(WorkPlanFields.DATE);
    }

    private String name(final Entity workPlan) {
        return workPlan.getStringField(WorkPlanFields.NAME);
    }

    private String author(final Locale locale) {
        return translationService.translate(MSG_AUTHOR, locale);
    }

    private String title(final Locale locale) {
        return translationService.translate(MSG_TITLE, locale);
    }

}
