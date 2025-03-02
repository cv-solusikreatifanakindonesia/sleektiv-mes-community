package com.sleektiv.mes.masterOrders.listeners;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.imports.services.XlsxImportService;
import com.sleektiv.mes.masterOrders.constants.MasterOrderFields;
import com.sleektiv.mes.masterOrders.constants.MasterOrdersConstants;
import com.sleektiv.mes.masterOrders.imports.masterOrder.MasterOrderCellBinderRegistry;
import com.sleektiv.mes.masterOrders.imports.masterOrder.MasterOrderXlsxImportService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchCriterion;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;

@Service
public class MasterOrdersImportListeners {

    @Autowired
    private MasterOrderXlsxImportService masterOrderXlsxImportService;

    @Autowired
    private MasterOrderCellBinderRegistry masterOrderCellBinderRegistry;

    public void downloadImportSchema(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        masterOrderXlsxImportService.downloadImportSchema(view, MasterOrdersConstants.PLUGIN_IDENTIFIER,
                MasterOrdersConstants.MODEL_MASTER_ORDER, XlsxImportService.L_XLSX);
    }

    public void processImportFile(final ViewDefinitionState view, final ComponentState state, final String[] args)
            throws IOException {
        masterOrderXlsxImportService.processImportFile(view, masterOrderCellBinderRegistry.getCellBinderRegistry(), true,
                MasterOrdersConstants.PLUGIN_IDENTIFIER, MasterOrdersConstants.MODEL_MASTER_ORDER,
                MasterOrdersImportListeners::createRestrictionForMasterOrder);
    }

    public static SearchCriterion createRestrictionForMasterOrder(final Entity masterOrder) {
        return SearchRestrictions.eq(MasterOrderFields.NUMBER, masterOrder.getStringField(MasterOrderFields.NUMBER));
    }

    public void redirectToLogs(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        masterOrderXlsxImportService.redirectToLogs(view, MasterOrdersConstants.MODEL_MASTER_ORDER);
    }

    public void onInputChange(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        masterOrderXlsxImportService.changeButtonsState(view, false);
    }

}
