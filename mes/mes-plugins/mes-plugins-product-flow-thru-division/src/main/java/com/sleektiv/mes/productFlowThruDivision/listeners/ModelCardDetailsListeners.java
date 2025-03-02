package com.sleektiv.mes.productFlowThruDivision.listeners;

import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.sleektiv.localization.api.utils.DateUtils;
import com.sleektiv.mes.productFlowThruDivision.constants.ModelCardFields;
import com.sleektiv.mes.productFlowThruDivision.constants.ProductFlowThruDivisionConstants;
import com.sleektiv.mes.productFlowThruDivision.print.ModelCardPdfService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.file.FileService;
import com.sleektiv.report.api.ReportService;
import com.sleektiv.security.api.SecurityService;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.CheckBoxComponent;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;

@Service
public class ModelCardDetailsListeners {

    @Autowired
    private ReportService reportService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ModelCardPdfService modelCardPdfService;

    public void printModelCard(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        reportService.printGeneratedReport(view, state, new String[] { args[0],
                ProductFlowThruDivisionConstants.PLUGIN_IDENTIFIER, ProductFlowThruDivisionConstants.MODEL_MODEL_CARD });
    }

    @Transactional
    public void generateModelCard(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent productsGrid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

        if (productsGrid.getEntities().isEmpty()) {
            view.addMessage("productFlowThruDivision.modelCard.generate.failure.noProducts", ComponentState.MessageType.INFO);
            return;
        }
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        CheckBoxComponent generated = (CheckBoxComponent) view.getComponentByReference(ModelCardFields.GENERATED);
        FieldComponent workerField = (FieldComponent) view.getComponentByReference(ModelCardFields.WORKER);
        FieldComponent dateField = (FieldComponent) view.getComponentByReference(ModelCardFields.DATE);

        workerField.setFieldValue(securityService.getCurrentUserName());
        dateField.setFieldValue(DateUtils.toDateTimeString(new Date()));
        generated.setChecked(true);

        Entity modelCard = form.getEntity();

        modelCard = modelCard.getDataDefinition().save(modelCard);

        form.setEntity(modelCard);

        Entity modelCardWithFileName = fileService.updateReportFileName(modelCard, ModelCardFields.DATE,
                "productFlowThruDivision.modelCard.report.fileName");

        try {
            modelCardPdfService.generateDocument(modelCardWithFileName, state.getLocale(), PageSize.A4.rotate());
        } catch (IOException | DocumentException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        view.addMessage("productFlowThruDivision.modelCard.generate.success", ComponentState.MessageType.SUCCESS);
    }
}
