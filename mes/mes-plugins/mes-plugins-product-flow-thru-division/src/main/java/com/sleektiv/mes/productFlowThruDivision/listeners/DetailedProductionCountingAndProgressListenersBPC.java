package com.sleektiv.mes.productFlowThruDivision.listeners;

import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.basicProductionCounting.constants.BasicProductionCountingConstants;
import com.sleektiv.mes.basicProductionCounting.constants.ProductionCountingQuantityFields;
import com.sleektiv.mes.materialFlowResources.exceptions.DocumentBuildException;
import com.sleektiv.mes.productFlowThruDivision.service.ProductionCountingDocumentService;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.model.api.validators.ErrorMessage;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class DetailedProductionCountingAndProgressListenersBPC {

    private static final String L_ORDER = "order";

    private static final String L_ERROR_NOT_ENOUGH_RESOURCES = "materialFlow.error.position.quantity.notEnoughResources";

    @Autowired
    private ProductionCountingDocumentService productionCountingDocumentService;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void resourceIssue(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        FormComponent formComponent = (FormComponent) view.getComponentByReference(L_ORDER);
        Entity order = formComponent.getEntity().getDataDefinition().get(formComponent.getEntity().getId());
        GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
        Set<Long> ids = grid.getSelectedEntitiesIds();
        List<Entity> pcqs = dataDefinitionService
                .get(BasicProductionCountingConstants.PLUGIN_IDENTIFIER,
                        BasicProductionCountingConstants.MODEL_PRODUCTION_COUNTING_QUANTITY)
                .find().add(SearchRestrictions.in("id", ids)).list().getEntities();

        if (!canIssueMaterials(order, pcqs)) {
            formComponent.setEntity(order);
            return;
        }
        try {
            productionCountingDocumentService.createInternalOutboundDocument(order, pcqs, false);
            if (order.isValid()) {
                productionCountingDocumentService.updateProductionCountingQuantity(pcqs);
                productionCountingDocumentService.updateCostsForOrder(order);
                view.addMessage("productFlowThruDivision.productionCountingQuantity.success.createInternalOutboundDocument",
                        ComponentState.MessageType.SUCCESS);
            }
        } catch (DocumentBuildException documentBuildException) {
            boolean errorsDisplayed = true;
            for (ErrorMessage error : documentBuildException.getEntity().getGlobalErrors()) {
                if (error.getMessage().equalsIgnoreCase(L_ERROR_NOT_ENOUGH_RESOURCES)) {
                    order.addGlobalError(error.getMessage(), false, error.getVars());
                } else {
                    errorsDisplayed = false;
                    order.addGlobalError(error.getMessage(), false, error.getVars());
                }
            }

            if (!errorsDisplayed) {
                order.addGlobalError(
                        "productFlowThruDivision.productionCountingQuantity.productionCountingQuantityError.createInternalOutboundDocument", false);
            }
            formComponent.setEntity(order);
        }

    }

    private boolean canIssueMaterials(Entity order, List<Entity> pcqs) {
        boolean canIssue = true;
        for (Entity pcq : pcqs) {
            if (pcq.getHasManyField(ProductionCountingQuantityFields.BATCHES).size() > 1) {
                order.addGlobalError(
                        "productFlowThruDivision.productionCountingQuantity.createInternalOutboundDocument.toManyBatchesError", false,
                        pcq.getBelongsToField(ProductionCountingQuantityFields.PRODUCT).getStringField(ProductFields.NUMBER));
                canIssue = false;
            }
        }
        return canIssue;
    }
}
