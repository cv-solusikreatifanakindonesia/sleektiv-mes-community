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
package com.sleektiv.mes.materialRequirementCoverageForOrder.listeners;

import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.sleektiv.localization.api.utils.DateUtils;
import com.sleektiv.mes.materialRequirementCoverageForOrder.MaterialRequirementCoverageForOrderService;
import com.sleektiv.mes.materialRequirementCoverageForOrder.constans.CoverageForOrderFields;
import com.sleektiv.mes.materialRequirementCoverageForOrder.constans.MaterialRequirementCoverageForOrderConstans;
import com.sleektiv.mes.materialRequirementCoverageForOrder.print.MaterialRequirementCoverageForOrderReportPdfService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.file.FileService;
import com.sleektiv.report.api.ReportService;
import com.sleektiv.security.api.SecurityService;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ComponentState.MessageType;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Service
public class GenerateMRCForOrderListeners {

    @Autowired
    private MaterialRequirementCoverageForOrderService mRCForOrderService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private MaterialRequirementCoverageForOrderReportPdfService materialRequirementCoverageForOrderReportPdfService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ReportService reportService;

    

    public final void printMaterialRequirementCoverageForOrder(final ViewDefinitionState view, final ComponentState state,
            final String[] args) {

        if (state instanceof FormComponent) {
            state.performEvent(view, "save", args);

            if (!state.isHasError()) {
                FormComponent materialRequirementCoverageForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
                Long materialRequirementCoverageId = materialRequirementCoverageForm.getEntityId();

                boolean saved = checkIfMaterialRequirementCoverageIsSaved(materialRequirementCoverageId);

                if (saved) {
                    reportService.printGeneratedReport(view, state, new String[] { args[0],
                            MaterialRequirementCoverageForOrderConstans.PLUGIN_IDENTIFIER,
                            MaterialRequirementCoverageForOrderConstans.MODEL_COVERAGE_FOR_ORDER });
                } else {
                    view.redirectTo("/materialRequirementCoverageForOrder/materialRequirementCoverageForOrderReportPdf."
                            + args[0] + "?id=" + state.getFieldValue(), true, false);
                }
            }
        } else {
            state.addMessage("materialRequirementCoverageForOrder.coverageForOrder.report.componentFormError",
                    MessageType.FAILURE);
        }
    }

    public final void saveMaterialRequirementCoverageForOrder(final ViewDefinitionState view, final ComponentState state,
            final String[] args) throws IOException, DocumentException {
        state.performEvent(view, "save", args);

        if (!state.isHasError()) {
            FormComponent materialRequirementCoverageForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
            Long materialRequirementCoverageId = materialRequirementCoverageForm.getEntityId();

            if (materialRequirementCoverageId != null) {
                Entity materialRequirementCoverage = mRCForOrderService.getMRCForOrder(materialRequirementCoverageId);

                materialRequirementCoverage.setField(CoverageForOrderFields.SAVED, true);

                materialRequirementCoverage = materialRequirementCoverage.getDataDefinition().save(materialRequirementCoverage);

                try {
                    generateMaterialRequirementCoverageReport(materialRequirementCoverage, state.getLocale());

                    state.performEvent(view, "clear", new String[0]);

                    state.addMessage("materialRequirementCoverageForOrder.coverageForOrder.report.savedMessage",
                            MessageType.SUCCESS, materialRequirementCoverage.getStringField(CoverageForOrderFields.NUMBER));
                } catch (IOException e) {
                    throw new IllegalStateException(e.getMessage(), e);
                } catch (DocumentException e) {
                    throw new IllegalStateException(e.getMessage(), e);
                }
            }
        }
    }

    private void generateMaterialRequirementCoverageReport(final Entity materialRequirementCoverage, final Locale locale)
            throws IOException, DocumentException {
        String localePrefix = "materialRequirementCoverageForOrder.coverageForOrder.report.fileName";

        Entity materialRequirementCoverageWithFileName = fileService.updateReportFileName(materialRequirementCoverage,
                CoverageForOrderFields.GENERATED_DATE, localePrefix);

        try {
            materialRequirementCoverageForOrderReportPdfService.generateDocument(materialRequirementCoverageWithFileName, locale,
                    PageSize.A4.rotate());
        } catch (IOException e) {
            throw new IllegalStateException("Problem with saving materialRequirementCoverageForOrder report");
        } catch (DocumentException e) {
            throw new IllegalStateException("Problem with generating materialRequirementCoverageForOrder report");
        }
    }

    public final void showMaterialRequirementCoverages(final ViewDefinitionState view, final ComponentState state,
            final String[] args) {
        String url = "../page/materialRequirementCoverageForOrder/materialRequirementCoveragesForOrderList.html";
        view.redirectTo(url, false, true);
    }

    @Transactional
    public final void generateMaterialRequirementCoverageForOrder(final ViewDefinitionState view, final ComponentState state,
            final String[] args) {

        //

        state.performEvent(view, "save", args);

        if (!state.isHasError()) {
            FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

            Long mRCForOrderId = form.getEntityId();

            if (mRCForOrderId != null) {
                Entity mRCForOrder = mRCForOrderService.getMRCForOrder(mRCForOrderId);

                mRCForOrder.setField(CoverageForOrderFields.GENERATED, true);
                mRCForOrder.setField(CoverageForOrderFields.GENERATED_DATE, new SimpleDateFormat(DateUtils.L_DATE_TIME_FORMAT,
                        LocaleContextHolder.getLocale()).format(new Date()));
                mRCForOrder.setField(CoverageForOrderFields.GENERATED_BY, securityService.getCurrentUserName());

                mRCForOrderService.estimateProductCoverageInTime(mRCForOrder);

                state.performEvent(view, "reset", new String[0]);

                state.addMessage("materialRequirementCoverageForOrder.coverageForOrder.report.generatedMessage",
                        MessageType.SUCCESS);
            }
        }

    }

    public boolean checkIfMaterialRequirementCoverageIsSaved(final Long materialRequirementCoverageId) {
        if (materialRequirementCoverageId != null) {
            Entity materialRequirementCoverage = mRCForOrderService.getMRCForOrder(materialRequirementCoverageId);

            if (materialRequirementCoverage != null) {
                return materialRequirementCoverage.getBooleanField(CoverageForOrderFields.SAVED);
            }
        }

        return false;
    }
}
