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
package com.sleektiv.plugins.sleektivExport.internal.controllers;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.model.api.aop.Monitorable;
import com.sleektiv.model.api.file.FileService;
import com.sleektiv.plugins.sleektivExport.api.ExportToPdfColumns;
import com.sleektiv.plugins.sleektivExport.api.helpers.ExportToFileColumnsHelper;
import com.sleektiv.report.api.FontUtils;
import com.sleektiv.report.api.FooterResolver;
import com.sleektiv.report.api.pdf.PdfHelper;
import com.sleektiv.report.api.pdf.PdfPageNumbering;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.crud.CrudService;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
public class ExportToPdfController {

    private static final String L_VIEW_NAME_VARIABLE = "viewName";

    private static final String L_PLUGIN_IDENTIFIER_VARIABLE = "pluginIdentifier";

    private static final String L_CONTROLLER_PATH = "exportToPdf/{" + L_PLUGIN_IDENTIFIER_VARIABLE + "}/{" + L_VIEW_NAME_VARIABLE
            + "}";

    @Autowired
    private CrudService crudService;

    @Autowired
    private FileService fileService;

    @Autowired
    private TranslationService translationService;

    @Autowired
    private PdfHelper pdfHelper;

    @Autowired
    private FooterResolver footerResolver;

    @Autowired
    private ExportToFileColumnsHelper<ExportToPdfColumns> exportToFileColumnsHelper;

    @Monitorable(threshold = 500)
    @ResponseBody
    @RequestMapping(value = { L_CONTROLLER_PATH }, method = RequestMethod.POST)
    public Object generatePdf(@PathVariable(L_PLUGIN_IDENTIFIER_VARIABLE) final String pluginIdentifier,
            @PathVariable(L_VIEW_NAME_VARIABLE) final String viewName, @RequestBody final JSONObject body, final Locale locale,
            @RequestHeader("User-Agent") final String userAgent) {

        try {
            changeMaxResults(body);

            ViewDefinitionState state = crudService.invokeEvent(pluginIdentifier, viewName, body, locale);

            GridComponent grid = (GridComponent) state.getComponentByReference(SleektivViewConstants.L_GRID);

            if (grid == null) {
                JSONArray args = body.getJSONObject("event").getJSONArray("args");
                if (args.length() > 0) {
                    grid = (GridComponent) state.getComponentByReference(args.getString(0));
                }
            }

            Document document = new Document(PageSize.A4.rotate());
            File file = getExportFile(grid, viewName);

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, fileOutputStream);

            pdfWriter.setPageEvent(new PdfPageNumbering(footerResolver.resolveFooter(locale)));

            document.setMargins(40, 40, 60, 60);

            document.addTitle("export.pdf");
            pdfHelper.addMetaData(document);
            pdfWriter.createXmpMetadata();
            document.open();

            String title = translationService
                    .translate(pluginIdentifier + "." + viewName + ".window.mainTab." + grid.getName() + ".header", locale);

            Date generationDate = new Date();

            pdfHelper.addDocumentHeader(document, "", title,
                    translationService.translate("sleektivReport.commons.generatedBy.label", locale), generationDate);

            List<String> columns = getColumns(grid);
            List<String> columnNames = getColumnNames(grid, columns);

            PdfPTable pdfTable = pdfHelper.createTableWithHeader(columnNames.size(), columnNames, false);

            List<Map<String, String>> rows;

            if (grid.getSelectedEntitiesIds().isEmpty()) {
                rows = grid.getColumnValuesOfAllRecords();
            } else {
                rows = grid.getColumnValuesOfSelectedRecords();
            }

            addPdfTableCells(pdfTable, rows, columns, viewName);

            document.add(pdfTable);
            document.close();

            boolean openInNewWindow = !StringUtils.isNoneBlank(userAgent) || (!userAgent.contains("Chrome") && !userAgent.contains("Safari"))
                    || userAgent.contains("Edge");

            state.redirectTo(fileService.getUrl(file.getAbsolutePath()) + "?clean", openInNewWindow, false);

            return crudService.renderView(state);
        } catch (JSONException | FileNotFoundException | DocumentException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private File getExportFile(final GridComponent grid, final String viewName) {
        String date = DateFormat.getDateInstance().format(new Date());
        return fileService.createExportFile("export_" + grid.getName() + "_" + date + ".pdf");
    }

    private List<String> getColumns(final GridComponent grid) {
        return exportToFileColumnsHelper.getColumns(grid, ExportToPdfColumns.class);
    }

    private List<String> getColumnNames(final GridComponent grid, final List<String> columns) {
        List<String> columnNames = Lists.newLinkedList();

        columns.forEach(column -> {
            String columnName = grid.getColumnNames().get(column);

            if (!Strings.isNullOrEmpty(columnName)) {
                columnNames.add(columnName);
            }
        });

        return columnNames;
    }

    private void addPdfTableCells(final PdfPTable pdfTable, final List<Map<String, String>> rows, final List<String> columns,
            final String viewName) {
        rows.forEach(row -> columns.forEach(column -> pdfTable.addCell(new Phrase(row.get(column), FontUtils.getDejavuRegular7Dark()))));
    }

    private void changeMaxResults(final JSONObject json) throws JSONException {
        JSONObject component = getComponent(json, getComponentName(json));

        component.getJSONObject("content").put("firstEntity", 0);
        component.getJSONObject("content").put("maxEntities", Integer.MAX_VALUE);
    }

    private JSONObject getComponent(final JSONObject json, final String componentName) throws JSONException {
        String[] path = componentName.split("\\.");

        JSONObject component = json;

        for (String p : path) {
            component = component.getJSONObject("components").getJSONObject(p);
        }

        return component;
    }

    private String getComponentName(final JSONObject body) throws JSONException {
        return body.getJSONObject("event").getString("component");
    }

}
