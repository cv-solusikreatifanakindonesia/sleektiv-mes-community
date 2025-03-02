package com.sleektiv.mes.productFlowThruDivision.hooks;

import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.basicProductionCounting.constants.ProductionCountingQuantityFields;
import com.sleektiv.mes.materialFlowResources.constants.ResourceFields;
import com.sleektiv.mes.productFlowThruDivision.constants.OrderProductResourceReservationFields;
import com.sleektiv.mes.productFlowThruDivision.constants.TrackingProductResourceReservationFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.NumberService;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OrderProductResourceReservationRegisterDetailsHooks {

    private static final String L_PLANED_QUANTITY_UNIT = "planedQuantityUnit";
    private static final String L_USED_QUANTITY_UNIT = "usedQuantityUnit";

    @Autowired
    private NumberService numberService;

    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Entity trackingProductResourceReservation = form.getPersistedEntityWithIncludedFormValues();
        Entity orderProductResourceReservationBT = trackingProductResourceReservation.getBelongsToField(TrackingProductResourceReservationFields.ORDER_PRODUCT_RESOURCE_RESERVATION);
        Entity orderProductResourceReservation = orderProductResourceReservationBT.getDataDefinition().get(orderProductResourceReservationBT.getId());
        String resourceNumer = orderProductResourceReservation.getStringField(OrderProductResourceReservationFields.RESOURCE_NUMBER);

        Entity pcq = orderProductResourceReservation.getBelongsToField(OrderProductResourceReservationFields.PRODUCTION_COUNTING_QUANTITY);
        Entity product = pcq.getBelongsToField(ProductionCountingQuantityFields.PRODUCT);

        ComponentState resourceLookup = view.getComponentByReference(OrderProductResourceReservationFields.RESOURCE);
        ComponentState resourcePlannedQuantity = view.getComponentByReference(OrderProductResourceReservationFields.PLANED_QUANTITY);
        ComponentState resourcePlannedQuantityUnit = view.getComponentByReference(L_PLANED_QUANTITY_UNIT);
        ComponentState usedQuantityUnit = view.getComponentByReference(L_USED_QUANTITY_UNIT);

        resourceLookup.setFieldValue(resourceNumer);
        resourcePlannedQuantity.setFieldValue(numberService.format(orderProductResourceReservation.getDecimalField(OrderProductResourceReservationFields.PLANED_QUANTITY)));
        resourcePlannedQuantityUnit.setFieldValue(product.getStringField(ProductFields.UNIT));
        usedQuantityUnit.setFieldValue(product.getStringField(ProductFields.UNIT));

    }

}
