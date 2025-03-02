package com.sleektiv.mes.masterOrders.hooks;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.basic.constants.SizeFields;
import com.sleektiv.mes.masterOrders.constants.MasterOrderFields;
import com.sleektiv.mes.masterOrders.constants.MasterOrderProductFields;
import com.sleektiv.mes.masterOrders.constants.MasterOrdersConstants;
import com.sleektiv.mes.masterOrders.constants.SalesPlanFields;
import com.sleektiv.mes.masterOrders.constants.SalesPlanProductFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.NumberService;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class OrderedProductsForFamilyHooks {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private NumberService numberService;

    public void onBeforeRender(final ViewDefinitionState view) {
        Entity salesPlanProduct = ((FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM))
                .getPersistedEntityWithIncludedFormValues();
        Entity product = salesPlanProduct.getBelongsToField(SalesPlanProductFields.PRODUCT);
        FieldComponent productField = (FieldComponent) view.getComponentByReference(SalesPlanProductFields.PRODUCT);
        productField
                .setFieldValue(product.getStringField(ProductFields.NUMBER) + ", " + product.getStringField(ProductFields.NAME));
        productField.requestComponentUpdateState();
        FieldComponent plannedQuantityField = (FieldComponent) view
                .getComponentByReference(SalesPlanProductFields.PLANNED_QUANTITY);
        plannedQuantityField
                .setFieldValue(numberService.format(salesPlanProduct.getDecimalField(SalesPlanProductFields.PLANNED_QUANTITY))
                        + " " + product.getStringField(ProductFields.UNIT));
        plannedQuantityField.requestComponentUpdateState();
        List<Entity> masterOrders = salesPlanProduct.getBelongsToField(SalesPlanProductFields.SALES_PLAN)
                .getHasManyField(SalesPlanFields.MASTER_ORDERS);
        List<Entity> salesPlanProducts = Lists.newArrayList();
        DataDefinition salesPlanProductDD = getSalesPlanProductDD();
        for (Entity child : product.getHasManyField(ProductFields.CHILDREN).stream()
                .sorted(Comparator.comparing(e -> e.getBelongsToField(ProductFields.SIZE) != null
                        ? e.getBelongsToField(ProductFields.SIZE).getIntegerField(SizeFields.SUCCESSION)
                        : 0))
                .collect(Collectors.toList())) {
            Entity salesPlanProductChild = salesPlanProductDD.create();
            salesPlanProductChild.setField(SalesPlanProductFields.PRODUCT, child);
            BigDecimal orderedQuantity = getSalesPlanProductChildOrderedQuantity(masterOrders, child, false);
            BigDecimal orderedToWarehouse = getSalesPlanProductChildOrderedQuantity(masterOrders, child, true);
            salesPlanProductChild.setField(SalesPlanProductFields.ORDERED_QUANTITY, orderedQuantity);
            salesPlanProductChild.setField(SalesPlanProductFields.ORDERED_TO_WAREHOUSE, orderedToWarehouse);
            salesPlanProducts.add(salesPlanProductChild);
        }
        GridComponent gridComponent = (GridComponent) view.getComponentByReference(SalesPlanFields.PRODUCTS);
        gridComponent.setEntities(salesPlanProducts);
    }

    private BigDecimal getSalesPlanProductChildOrderedQuantity(List<Entity> masterOrders, Entity child, boolean warehouseOrder) {
        BigDecimal quantity = BigDecimal.ZERO;
        for (Entity masterOrder : masterOrders) {
            if (warehouseOrder == masterOrder.getBooleanField(MasterOrderFields.WAREHOUSE_ORDER)) {
                List<Entity> masterOrderProducts = masterOrder.getHasManyField(MasterOrderFields.MASTER_ORDER_PRODUCTS);
                for (Entity masterOrderProduct : masterOrderProducts) {
                    if (child.getId().equals(masterOrderProduct.getBelongsToField(MasterOrderProductFields.PRODUCT).getId())) {
                        quantity = quantity
                                .add(masterOrderProduct.getDecimalField(MasterOrderProductFields.MASTER_ORDER_QUANTITY));
                        break;
                    }
                }
            }
        }
        return quantity;
    }

    private DataDefinition getSalesPlanProductDD() {
        return dataDefinitionService.get(MasterOrdersConstants.PLUGIN_IDENTIFIER, MasterOrdersConstants.MODEL_SALES_PLAN_PRODUCT);
    }

}
