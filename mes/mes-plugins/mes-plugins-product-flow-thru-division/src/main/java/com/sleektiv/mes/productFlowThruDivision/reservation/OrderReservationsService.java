package com.sleektiv.mes.productFlowThruDivision.reservation;

import com.google.common.collect.Lists;
import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.basicProductionCounting.constants.ProductionCountingQuantityFields;
import com.sleektiv.mes.materialFlowResources.constants.MaterialFlowResourcesConstants;
import com.sleektiv.mes.materialFlowResources.constants.ReservationFields;
import com.sleektiv.mes.materialFlowResources.service.ResourceReservationsService;
import com.sleektiv.mes.productFlowThruDivision.constants.OrderProductResourceReservationFields;
import com.sleektiv.mes.productFlowThruDivision.constants.ProductionCountingQuantityFieldsPFTD;
import com.sleektiv.mes.productFlowThruDivision.constants.TrackingProductResourceReservationFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class OrderReservationsService {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private ResourceReservationsService resourceReservationsService;

    public void createOrUpdateReservation(Entity orderProductResourceReservation) {
        Entity existingReservation = getReservation(orderProductResourceReservation);

        if (Objects.nonNull(existingReservation)) {
            BigDecimal plannedQuantity = orderProductResourceReservation.getDecimalField(OrderProductResourceReservationFields.PLANED_QUANTITY);
            Entity resource = orderProductResourceReservation.getBelongsToField(OrderProductResourceReservationFields.RESOURCE);
            if(resource == null) {
                return;
            }
            existingReservation.setField(ReservationFields.QUANTITY, plannedQuantity);
            existingReservation.setField(ReservationFields.RESOURCE, resource);
            existingReservation.getDataDefinition().save(existingReservation);
        } else {
            if (Objects.isNull(orderProductResourceReservation.getId()))
                createReservation(orderProductResourceReservation);
        }
    }


    public void clearReservationsForOrder(Entity order) {
        List<Entity> reservationsForOrder = getReservationsForOrder(order);
        reservationsForOrder.forEach(rfo -> {
            resourceReservationsService.updateResourceQuantitiesOnRemoveReservation(rfo.getBelongsToField(ReservationFields.RESOURCE), rfo.getDecimalField(ReservationFields.QUANTITY));
            rfo.getDataDefinition().delete(rfo.getId());
        });
    }


    public void updateReservationOnDocumentCreation(final Entity trackingProductResourceReservation) {
        Entity orderProductResourceReservation = trackingProductResourceReservation.getBelongsToField(TrackingProductResourceReservationFields.ORDER_PRODUCT_RESOURCE_RESERVATION);
        Entity existingReservation = getReservation(orderProductResourceReservation);
        if (Objects.nonNull(existingReservation)) {
            BigDecimal reservationQuantity = existingReservation.getDecimalField(ReservationFields.QUANTITY);
            BigDecimal newReservationQuantity = reservationQuantity.subtract(trackingProductResourceReservation.getDecimalField(TrackingProductResourceReservationFields.USED_QUANTITY));
            if (newReservationQuantity.compareTo(BigDecimal.ZERO) > 0) {
                existingReservation.setField(ReservationFields.QUANTITY, newReservationQuantity);
            } else {
                existingReservation.setField(ReservationFields.QUANTITY, BigDecimal.ZERO);
            }
            existingReservation.getDataDefinition().save(existingReservation);
        }

    }


    private void createReservation(Entity orderProductResourceReservation) {
        Entity productionCountingQuantity = orderProductResourceReservation.getBelongsToField(OrderProductResourceReservationFields.PRODUCTION_COUNTING_QUANTITY);
        Entity resource = orderProductResourceReservation.getBelongsToField(OrderProductResourceReservationFields.RESOURCE);
        BigDecimal plannedQuantity = orderProductResourceReservation.getDecimalField(OrderProductResourceReservationFields.PLANED_QUANTITY);
        Date date = orderProductResourceReservation.getDateField(OrderProductResourceReservationFields.CREATION_DATE);
        Entity product = productionCountingQuantity.getBelongsToField(ProductionCountingQuantityFields.PRODUCT);
        Entity componentsLocation = productionCountingQuantity.getBelongsToField(ProductionCountingQuantityFieldsPFTD.COMPONENTS_LOCATION);
        Entity order = productionCountingQuantity.getBelongsToField(ProductionCountingQuantityFields.ORDER);

        Entity reservation = getReservationDD().create();

        reservation.setField(ReservationFields.LOCATION, componentsLocation);
        reservation.setField(ReservationFields.ORDER_PRODUCT_RESOURCE_RESERVATION, orderProductResourceReservation);
        reservation.setField(ReservationFields.PRODUCT, product);
        reservation.setField(ReservationFields.QUANTITY, plannedQuantity);
        reservation.setField(ReservationFields.ORDER, order);
        reservation.setField(ReservationFields.RESOURCE, resource);
        reservation.setField(ReservationFields.DATE, date);

        orderProductResourceReservation.setField(OrderProductResourceReservationFields.RESERVATIONS, Lists.newArrayList(reservation));
    }

    private DataDefinition getReservationDD() {
        return dataDefinitionService.get(MaterialFlowResourcesConstants.PLUGIN_IDENTIFIER,
                MaterialFlowResourcesConstants.MODEL_RESERVATION);
    }

    private List<Entity> getReservationsForOrder(final Entity order) {

        SearchCriteriaBuilder scb = getReservationDD().find();
        List<Entity> reservations = scb.add(SearchRestrictions.belongsTo(ReservationFields.ORDER, order))
                .list().getEntities();


        return reservations;
    }

    private Entity getReservation(final Entity orderProductResourceReservation) {
        if (orderProductResourceReservation == null || orderProductResourceReservation.getId() == null) {
            return null;
        }
        SearchCriteriaBuilder find = getReservationDD().find();
        return find.add(SearchRestrictions.belongsTo(ReservationFields.ORDER_PRODUCT_RESOURCE_RESERVATION, orderProductResourceReservation)).setMaxResults(1).uniqueResult();
    }

}
