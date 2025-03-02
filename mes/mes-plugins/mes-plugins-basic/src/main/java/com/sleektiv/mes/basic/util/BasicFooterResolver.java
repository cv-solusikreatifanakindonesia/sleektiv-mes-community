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
package com.sleektiv.mes.basic.util;

import static com.sleektiv.mes.basic.constants.ParameterFields.ADDITIONAL_TEXT_IN_FOOTER;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.mes.basic.CompanyService;
import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.report.api.Footer;
import com.sleektiv.report.api.FooterResolver;
import com.sleektiv.report.api.pdf.PdfHelper;

@Primary
@Component
public class BasicFooterResolver implements FooterResolver {

    private static final String EMAIL = "email";

    @Autowired
    private TranslationService translationService;

    @Autowired
    private ParameterService parameterService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private PdfHelper pdfHelper;

    @Override
    public Footer resolveFooter(final Locale locale) {
        String companyName = "";
        String address = "";
        String phoneEmail = "";

        Entity parameter = parameterService.getParameter();
        Entity company = companyService.getCompany();
        String additionalText = parameter.getStringField(ADDITIONAL_TEXT_IN_FOOTER);

        if (additionalText == null) {
            additionalText = "";
        }

        StringBuilder generatedBy = new StringBuilder();
        generatedBy = generatedBy.append(translationService.translate("sleektivReport.commons.generatedBy.label", locale));
        generatedBy = generatedBy.append(" ");
        generatedBy = generatedBy.append(pdfHelper.getDocumentAuthor());

        if (company != null) {
            StringBuilder companyData = new StringBuilder();

            companyData = companyData.append(company.getStringField("name"));
            if (company.getStringField("tax") != null) {
                companyData = companyData.append(", ");
                companyData = companyData.append(translationService.translate("sleektivReport.commons.tax.label", locale) + ": ");
                companyData = companyData.append(company.getStringField("tax"));
            }
            companyName = companyData.toString();

            if (company.getStringField("street") != null && company.getStringField("house") != null
                    && company.getStringField("zipCode") != null && company.getStringField("city") != null) {
                companyData.setLength(0);
                companyData = companyData.append(company.getStringField("street"));
                companyData = companyData.append(" ");
                companyData = companyData.append(company.getStringField("house"));
                if (company.getStringField("flat") != null) {
                    companyData = companyData.append("/");
                    companyData = companyData.append(company.getStringField("flat"));
                }
                companyData = companyData.append(", ");
                companyData = companyData.append(company.getStringField("zipCode"));
                companyData = companyData.append(" ");
                companyData = companyData.append(company.getStringField("city"));
                if (company.getBelongsToField("country") != null) {
                    companyData = companyData.append(", ");
                    companyData = companyData.append(company.getBelongsToField("country").getStringField("country"));
                }
                address = companyData.toString();
            }

            if (company.getStringField("phone") == null) {
                if (company.getStringField(EMAIL) != null) {
                    companyData.setLength(0);
                    companyData = companyData.append("E-mail: ");
                    companyData = companyData.append(company.getStringField(EMAIL));
                    phoneEmail = companyData.toString();
                }
            } else {
                companyData.setLength(0);
                companyData = companyData.append(translationService.translate("sleektivReport.commons.phone.label", locale));
                companyData = companyData.append(": ");
                companyData = companyData.append(company.getStringField("phone"));
                if (company.getStringField(EMAIL) != null) {
                    companyData = companyData.append(", ");
                    companyData = companyData.append("E-mail: ");
                    companyData = companyData.append(company.getStringField(EMAIL));
                }
                phoneEmail = companyData.toString();
            }
        }

        return new Footer(translationService.translate("sleektivReport.commons.page.label", locale), translationService.translate(
                "sleektivReport.commons.of.label", locale), companyName, address, phoneEmail, generatedBy.toString(),
                additionalText);
    }

}
