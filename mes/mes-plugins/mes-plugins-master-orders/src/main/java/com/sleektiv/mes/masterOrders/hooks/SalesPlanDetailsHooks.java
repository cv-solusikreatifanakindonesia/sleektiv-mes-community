package com.sleektiv.mes.masterOrders.hooks;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.masterOrders.constants.SalesPlanFields;
import com.sleektiv.mes.masterOrders.states.constants.SalesPlanStateStringValues;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.Ribbon;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class SalesPlanDetailsHooks {

    private static final String L_SALES_PLAN_MATERIAL_REQUIREMENT = "salesPlanMaterialRequirement";

    private static final String L_CREATE_SALES_PLAN_MATERIAL_REQUIREMENT = "createSalesPlanMaterialRequirement";

    private static final String L_PRODUCTS = "products";

    private static final String L_IMPORT = "import";

    private static final String L_OPEN_POSITIONS_IMPORT_PAGE = "openPositionsImportPage";

    private static final String TECHNOLOGY_NUMBER = "technologyNumber";

    public void onBeforeRender(final ViewDefinitionState view) {
        setRibbonEnabled(view);
        disableForm(view);
    }

    private void disableForm(final ViewDefinitionState view) {
        FormComponent salesPlanForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        GridComponent productsGrid = (GridComponent) view.getComponentByReference(L_PRODUCTS);
        Entity salesPlan = salesPlanForm.getEntity();
        String salesPlanState = salesPlan.getStringField(SalesPlanFields.STATE);

        if (SalesPlanStateStringValues.COMPLETED.equals(salesPlanState)
                || SalesPlanStateStringValues.REJECTED.equals(salesPlanState)) {
            salesPlanForm.setFormEnabled(false);
            productsGrid.setEnabled(false);
        } else {
            salesPlanForm.setFormEnabled(true);
            productsGrid.setEnabled(true);
        }
    }

    private void setRibbonEnabled(final ViewDefinitionState view) {
        FormComponent salesPlanForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity salesPlan = salesPlanForm.getEntity();
        String state = salesPlan.getStringField(SalesPlanFields.STATE);
        GridComponent productsGrid = (GridComponent) view.getComponentByReference(SalesPlanFields.PRODUCTS);

        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
        Ribbon ribbon = window.getRibbon();

        RibbonGroup salesPlanMaterialRequirementRibbonGroup = ribbon.getGroupByName(L_SALES_PLAN_MATERIAL_REQUIREMENT);

        RibbonActionItem createSalesPlanMaterialRequirementRibbonActionItem = salesPlanMaterialRequirementRibbonGroup
                .getItemByName(L_CREATE_SALES_PLAN_MATERIAL_REQUIREMENT);
        RibbonActionItem openPositionsImportPageRibbonActionItem = window.getRibbon().getGroupByName(L_IMPORT)
                .getItemByName(L_OPEN_POSITIONS_IMPORT_PAGE);

        RibbonGroup technologiesRibbonGroup = ribbon.getGroupByName("technologies");
        RibbonActionItem useOtherTechnologyActionItem = technologiesRibbonGroup.getItemByName("useOtherTechnology");
        RibbonActionItem fillTechnologyActionItem = technologiesRibbonGroup.getItemByName("fillTechnology");

        boolean isEnabled = Objects.nonNull(salesPlanForm.getEntityId()) && state.equals(SalesPlanStateStringValues.DRAFT);

        createSalesPlanMaterialRequirementRibbonActionItem.setEnabled(isEnabled);
        createSalesPlanMaterialRequirementRibbonActionItem.requestUpdate(true);
        openPositionsImportPageRibbonActionItem.setEnabled(isEnabled);
        openPositionsImportPageRibbonActionItem.requestUpdate(true);
        useOtherTechnologyActionItem.setEnabled(isEnabled && !productsGrid.getSelectedEntitiesIds().isEmpty()
                && productsGrid.getSelectedEntities().stream().noneMatch(e -> e.getStringField(TECHNOLOGY_NUMBER) == null)
                && productsGrid.getSelectedEntities().stream().map(e -> e.getStringField(TECHNOLOGY_NUMBER)).distinct()
                        .count() == 1L);
        useOtherTechnologyActionItem.requestUpdate(true);
        fillTechnologyActionItem.setEnabled(isEnabled && !productsGrid.getSelectedEntitiesIds().isEmpty()
                && productsGrid.getSelectedEntities().stream()
                        .allMatch(e -> e.getStringField(TECHNOLOGY_NUMBER) == null && e.getStringField("productFamily") != null)
                && productsGrid.getSelectedEntities().stream().map(e -> e.getStringField("productFamily")).distinct()
                        .count() == 1L);
        fillTechnologyActionItem.requestUpdate(true);
    }

}
