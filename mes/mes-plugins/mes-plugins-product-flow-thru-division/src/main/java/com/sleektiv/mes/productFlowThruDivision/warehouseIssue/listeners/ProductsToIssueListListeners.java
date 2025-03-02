package com.sleektiv.mes.productFlowThruDivision.warehouseIssue.listeners;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sleektiv.mes.productFlowThruDivision.constants.ProductFlowThruDivisionConstants;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.ProductsToIssueService;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.constans.ProductsToIssueFields;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.constans.WarehouseIssueFields;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.states.constants.WarehouseIssueState;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductsToIssueListListeners {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private ProductsToIssueService productsToIssueService;

    public void showProductAttributes(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent positionGird = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

        Set<Long> ids = positionGird.getSelectedEntitiesIds();

        if (ids.size() == 1) {
            Entity productToIssue = getProductsToIssueDD().get(ids.stream().findFirst().get());

            Map<String, Object> parameters = Maps.newHashMap();

            parameters.put("form.id", productToIssue.getBelongsToField(ProductsToIssueFields.PRODUCT).getId());

            view.redirectTo("/page/materialFlowResources/productAttributesForPositionList.html", false, true, parameters);
        } else {
            view.addMessage("materialFlow.info.document.showProductAttributes.toManyPositionsSelected",
                    ComponentState.MessageType.INFO);
        }
    }

    public void correctReservations(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent positionGird = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

        List<Long> selectedEntities = Lists.newArrayList(positionGird.getSelectedEntitiesIds());

        if (selectedEntities.isEmpty()) {
            view.addMessage("productFlowThruDivision.productsToIssueList.noSelectedEntities", ComponentState.MessageType.INFO);

            return;
        }

        Entity firstProduct = getProductsToIssueDD().get(selectedEntities.get(0));

        Map<String, Object> parameters = Maps.newHashMap();

        parameters.put("form.productsToIssueIds", selectedEntities.stream().map(Object::toString)
                .collect(Collectors.joining(",")));

        if (Objects.nonNull(firstProduct)) {
            parameters.put("form.locationFrom", firstProduct.getBelongsToField(ProductsToIssueFields.LOCATION).getId());
            parameters.put("form.placeOfIssue", firstProduct.getBelongsToField(ProductsToIssueFields.WAREHOUSE_ISSUE)
                    .getBelongsToField(WarehouseIssueFields.PLACE_OF_ISSUE).getId());
        }

        String url = "../page/productFlowThruDivision/productToIssueCorrectionHelperDetails.html";
        view.redirectTo(url, false, true, parameters);
    }

    public void copyProductsToIssue(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent positionGird = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

        List<Long> selectedEntities = Lists.newArrayList(positionGird.getSelectedEntitiesIds());

        if (selectedEntities.isEmpty()) {
            view.addMessage("productFlowThruDivision.productsToIssueList.noSelectedEntities", ComponentState.MessageType.INFO);

            return;
        }

        Entity firstProduct = getProductsToIssueDD().get(selectedEntities.get(0));

        Map<String, Object> parameters = Maps.newHashMap();

        parameters.put("form.productsToIssueIds", selectedEntities.stream().map(Object::toString)
                .collect(Collectors.joining(",")));

        if (Objects.nonNull(firstProduct)) {
            parameters.put("form.locationFrom", firstProduct.getBelongsToField(ProductsToIssueFields.WAREHOUSE_ISSUE)
                    .getBelongsToField(WarehouseIssueFields.PLACE_OF_ISSUE).getId());
        }

        Optional.ofNullable(positionGird.getFilters().get("productNumber")).ifPresent(
                value -> parameters.put("form.gridProductNumberFilter", value));

        String url = "../page/productFlowThruDivision/productsToIssueHelperDetails.html";
        view.redirectTo(url, false, true, parameters);
    }

    public void onBeforeRender(final ViewDefinitionState view) {
        changeRibbonState(view);
    }

    public void changeRibbonState(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        changeRibbonState(view);
    }

    private void changeRibbonState(final ViewDefinitionState view) {
        GridComponent positionsGrid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
        RibbonGroup group = window.getRibbon().getGroupByName("warehouseIssues");
        RibbonActionItem copyProducts = group.getItemByName("copyProducts");
        RibbonActionItem correctReservations = group.getItemByName("correctReservations");

        DataDefinition productsToIssueDD = getProductsToIssueDD();

        Set<Long> selectedEntitiesIds = positionsGrid.getSelectedEntitiesIds();

        boolean enabled = !selectedEntitiesIds.isEmpty();
        boolean correctionEnabled = !selectedEntitiesIds.isEmpty();

        if (enabled) {
            Entity warehouse = null;
            Entity location = null;

            for (Long id : selectedEntitiesIds) {
                Entity productToIssue = productsToIssueDD.get(id);

                if (Objects.nonNull(productToIssue)) {
                    Entity warehouseIssue = productToIssue.getBelongsToField(ProductsToIssueFields.WAREHOUSE_ISSUE);
                    String state = warehouseIssue.getStringField(WarehouseIssueFields.STATE);
                    Entity productLocation = productToIssue.getBelongsToField(ProductsToIssueFields.LOCATION);

                    if (Objects.isNull(location)) {
                        location = productLocation;
                    } else {
                        if (Objects.nonNull(productLocation) && !location.getId().equals(productLocation.getId())) {
                            correctionEnabled = false;
                        }
                    }

                    if (WarehouseIssueState.DISCARD.getStringValue().equals(state)
                            || WarehouseIssueState.COMPLETED.getStringValue().equals(state)) {
                        enabled = false;
                    } else {
                        Entity issueWarehouse = warehouseIssue.getBelongsToField(WarehouseIssueFields.PLACE_OF_ISSUE);

                        if (warehouse == null) {
                            warehouse = issueWarehouse;
                        } else {
                            if (!warehouse.getId().equals(issueWarehouse.getId())) {
                                enabled = false;
                                correctionEnabled = false;
                            }
                        }
                    }
                } else {
                    enabled = false;
                }
            }
        }

        correctReservations.setEnabled(correctionEnabled);
        correctReservations.requestUpdate(true);
        copyProducts.setEnabled(enabled);
        copyProducts.requestUpdate(true);
    }

    private DataDefinition getProductsToIssueDD() {
        return dataDefinitionService.get(ProductFlowThruDivisionConstants.PLUGIN_IDENTIFIER,
                ProductFlowThruDivisionConstants.MODEL_PRODUCTS_TO_ISSUE);
    }

}
