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
package com.sleektiv.report.api.pdf;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.file.FileService;
import com.sleektiv.report.api.FooterResolver;
import com.sleektiv.report.api.ReportDocumentService;
import com.sleektiv.report.api.ReportService;

public abstract class PdfDocumentWithWriterService implements ReportDocumentService {

    @Autowired
    private TranslationService translationService;

    @Autowired
    private FileService fileService;

    @Autowired
    private PdfHelper pdfHelper;

    @Autowired
    private FooterResolver footerResolver;

    private static final Logger LOG = LoggerFactory.getLogger(PdfDocumentWithWriterService.class);

    @Override
    public void generateDocument(final Entity entity, final Locale locale, final Rectangle pageSize) throws IOException,
            DocumentException {
        String path = entity.getStringField("fileName").split(",")[0];

        generate(entity, locale, path, pageSize);
    }

    @Override
    public void generateDocument(final Entity entity, final Locale locale) throws IOException, DocumentException {
        generateDocument(entity, locale, PageSize.A4);
    }

    public void generateDocument(final Entity entity, final Locale locale, final String localePrefixToMatch) throws IOException,
            DocumentException {
        generateDocument(entity, locale, localePrefixToMatch, PageSize.A4);
    }

    public void generateDocument(final Entity entity, final Locale locale, final String localePrefixToMatch,
            final Rectangle pageSize) throws IOException, DocumentException {

        String prefix = translationService.translate(localePrefixToMatch, locale);

        String path = null;
        for (String pt : entity.getStringField("fileName").split(",")) {
            if (fileService.getName(pt).startsWith(prefix)) {
                path = pt;
            }
        }

        if (path == null) {
            throw new IllegalStateException("filename pattern not found");
        }

        generate(entity, locale, path, pageSize);
    }

    private void generate(final Entity entity, final Locale locale, final String filename, final Rectangle pageSize)
            throws IOException, DocumentException {
        Document document = new Document(pageSize);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileService.createReportFile(filename + "."
                    + ReportService.ReportType.PDF.getExtension()));
            PdfWriter writer = PdfWriter.getInstance(document, fileOutputStream);
            writer.setPageEvent(new PdfPageNumbering(footerResolver.resolveFooter(locale)));
            document.setMargins(40, 40, 60, 60);
            buildPdfMetadata(document, locale);
            writer.createXmpMetadata();
            document.open();
            buildPdfContent(writer, document, entity, locale);
            document.close();
        } catch (DocumentException e) {
            LOG.error("Problem with generating document - " + e.getMessage());
            document.close();
            throw e;
        }
    }

    protected void buildPdfMetadata(final Document document, final Locale locale) {
        document.addTitle(getReportTitle(locale));
        pdfHelper.addMetaData(document);
    }

    protected abstract void buildPdfContent(final PdfWriter writer, final Document document, final Entity entity,
            final Locale locale)
            throws DocumentException;

}
