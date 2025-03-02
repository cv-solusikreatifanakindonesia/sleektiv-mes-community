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
package com.sleektiv.report.internal;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.report.api.Footer;
import com.sleektiv.report.api.FooterResolver;
import com.sleektiv.report.api.pdf.PdfHelper;
import com.sleektiv.security.api.SecurityService;

@Component
public class DefaultFooterResolver implements FooterResolver {

    @Autowired
    private TranslationService translationService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private PdfHelper pdfHelper;

    @Override
    public Footer resolveFooter(final Locale locale) {
        String companyName = "";
        String address = "";
        String phoneEmail = "";
        String additionalText = "";

        StringBuilder generatedBy = new StringBuilder();
        generatedBy = generatedBy.append(translationService.translate("sleektivReport.commons.generatedBy.label", locale));
        generatedBy = generatedBy.append(" ");
        generatedBy = generatedBy.append(pdfHelper.getDocumentAuthor());

        return new Footer(translationService.translate("sleektivReport.commons.page.label", locale), translationService.translate(
                "sleektivReport.commons.of.label", locale), companyName, address, phoneEmail, generatedBy.toString(),
                additionalText);
    }
}
