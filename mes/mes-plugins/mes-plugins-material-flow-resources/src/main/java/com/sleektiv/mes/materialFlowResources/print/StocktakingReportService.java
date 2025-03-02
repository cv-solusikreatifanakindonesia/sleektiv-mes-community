package com.sleektiv.mes.materialFlowResources.print;

import com.lowagie.text.DocumentException;
import com.sleektiv.mes.materialFlowResources.constants.MaterialFlowResourcesConstants;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.file.FileService;
import com.sleektiv.report.api.ReportService;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class StocktakingReportService {

    public static final String GENERATION_DATE = "generationDate";

    @Autowired
    private StocktakingPdfReportService stocktakingPdfReportService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ReportService reportService;

    public void generateReport(final ComponentState state, final Entity stocktakingReport) throws IOException, DocumentException {
        stocktakingPdfReportService.generateDocument(fileService.updateReportFileName(stocktakingReport,
                GENERATION_DATE, "materialFlowResources.stocktaking.report.fileName"), state.getLocale());
    }

    public void printReport(final ViewDefinitionState view, final ComponentState state) {
        reportService.printGeneratedReport(view, state, new String[] { "pdf", MaterialFlowResourcesConstants.PLUGIN_IDENTIFIER,
                MaterialFlowResourcesConstants.MODEL_STOCKTAKING });
    }
}
