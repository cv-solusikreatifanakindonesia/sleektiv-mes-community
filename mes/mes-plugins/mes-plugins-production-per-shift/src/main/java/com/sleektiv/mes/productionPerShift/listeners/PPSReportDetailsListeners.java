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
package com.sleektiv.mes.productionPerShift.listeners;

import java.io.IOException;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.sleektiv.mes.productionPerShift.constants.PPSReportFields;
import com.sleektiv.mes.productionPerShift.constants.ProductionPerShiftConstants;
import com.sleektiv.mes.productionPerShift.report.print.PPSReportXlsService;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.file.FileService;
import com.sleektiv.report.api.ReportService;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ComponentState.MessageType;
import com.sleektiv.view.api.ViewDefinitionState;

@Service
public class PPSReportDetailsListeners {

    @Autowired
    private PPSReportXlsService ppsReportXlsService;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ReportService reportService;

    public void printReport(final ViewDefinitionState viewDefinitionState, final ComponentState state, final String[] args) {
        reportService.printGeneratedReport(viewDefinitionState, state, new String[] { args[0],
                ProductionPerShiftConstants.PLUGIN_IDENTIFIER, ProductionPerShiftConstants.MODEL_PPS_REPORT });
    }

    @Transactional
    public void generateReport(final ViewDefinitionState viewDefinitionState, final ComponentState state, final String[] args) {
        state.performEvent(viewDefinitionState, "save", new String[0]);

        if (!state.isHasError()) {
            Entity report = getReportFromDB((Long) state.getFieldValue());

            if (report == null) {
                state.addMessage("sleektivView.message.entityNotFound", MessageType.FAILURE);
                return;
            } else if (StringUtils.hasText(report.getStringField(PPSReportFields.FILE_NAME))) {
                state.addMessage("productionPerShift.report.error.documentsWasGenerated", MessageType.FAILURE);
                return;
            }

            if (!report.getBooleanField(PPSReportFields.GENERATED)) {
                fillReportValues(report);
            }
            report = getReportFromDB((Long) state.getFieldValue());

            try {
                generateReportDocuments(report, state.getLocale());

                state.performEvent(viewDefinitionState, "reset", new String[0]);

                state.addMessage("productionPerShift.window.mainTab.goodFoodReportDetails.generatedMessage", MessageType.SUCCESS);
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage(), e);
            } catch (DocumentException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }

    public void generateReportDocuments(final Entity goodFoodReport, final Locale locale) throws IOException, DocumentException {

        String localePrefix = "productionPerShift.report.fileName";

        Entity reportWithFileName = fileService.updateReportFileName(goodFoodReport, PPSReportFields.CREATE_DATE, localePrefix);

        try {
            ppsReportXlsService.generateDocument(reportWithFileName, locale, PageSize.A3);

        } catch (IOException e) {
            throw new IllegalStateException("Problem with saving goodFood report");
        }
    }

    private void fillReportValues(final Entity report) {
        report.setField(PPSReportFields.GENERATED, true);
        report.getDataDefinition().save(report);
    }

    private Entity getReportFromDB(final Long entityId) {
        return dataDefinitionService
                .get(ProductionPerShiftConstants.PLUGIN_IDENTIFIER, ProductionPerShiftConstants.MODEL_PPS_REPORT).get(entityId);
    }

}
