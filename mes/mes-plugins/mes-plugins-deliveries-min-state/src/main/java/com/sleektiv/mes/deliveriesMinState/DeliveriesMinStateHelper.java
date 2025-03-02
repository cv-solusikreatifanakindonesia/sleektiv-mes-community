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
package com.sleektiv.mes.deliveriesMinState;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.sleektiv.mes.basic.CompanyService;
import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.basic.constants.ParameterFields;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.basic.constants.UnitConversionItemFieldsB;
import com.sleektiv.mes.costNormsForProduct.constants.ProductFieldsCNFP;
import com.sleektiv.mes.deliveries.DeliveriesService;
import com.sleektiv.mes.deliveries.constants.CompanyProductFields;
import com.sleektiv.mes.deliveries.constants.DeliveriesConstants;
import com.sleektiv.mes.deliveries.constants.DeliveryFields;
import com.sleektiv.mes.deliveries.constants.OrderedProductFields;
import com.sleektiv.mes.deliveries.states.constants.DeliveryState;
import com.sleektiv.mes.emailNotifications.notifications.constants.StaffNotificationFieldsMS;
import com.sleektiv.mes.emailNotifications.notifications.service.MailingService;
import com.sleektiv.mes.emailNotifications.constants.EmailNotificationsConstants;
import com.sleektiv.mes.emailNotifications.constants.StaffNotificationFields;
import com.sleektiv.mes.materialFlow.constants.MaterialFlowConstants;
import com.sleektiv.mes.materialFlowResources.constants.MaterialFlowResourcesConstants;
import com.sleektiv.mes.warehouseMinimalState.WarehouseMinimalStateHelper;
import com.sleektiv.model.api.*;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.model.api.units.PossibleUnitConversions;
import com.sleektiv.model.api.units.UnitConversionService;
import com.sleektiv.view.api.utils.NumberGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DeliveriesMinStateHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveriesMinStateHelper.class);

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private NumberService numberService;

    @Autowired
    private NumberGeneratorService numberGeneratorService;

    @Autowired
    private UnitConversionService unitConversionService;

    @Autowired
    private ParameterService parameterService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private DeliveriesService deliveriesService;

    @Autowired
    private WarehouseMinimalStateHelper warehouseMinimalStateHelper;

    @Autowired
    private MailingService mailingService;

    private void sendEmailNotifications(final List<String> createdDeliveries) {
        if (!createdDeliveries.isEmpty()) {
            List<String> emails = getStaffNotificationDD().find().add(SearchRestrictions.eq(StaffNotificationFieldsMS.CREATE_DELIVERY_MIN_STATE, true))
                    .list().getEntities().stream().map(entity -> entity.getStringField(StaffNotificationFields.EMAIL))
                    .collect(Collectors.toList());

            mailingService.sendTemplateDeliveryInfoEmailsBySendinblue(emails, createdDeliveries);
        }
    }

    public Map<Long, Multimap<Long, Entity>> createDeliveriesFromMinimalState() {
        Map<Long, Multimap<Long, Entity>> minimalStatePerWarehousesAndSupplier = Maps.newHashMap();

        List<Entity> warehouses = getWarehousesFromMinimalState();

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("CreateDeliveriesFromMinimalState invoked with: %d warehouses.", warehouses.size()));
        }

        List<String> createdDeliveries = Lists.newArrayList();

        warehouses.forEach(warehouse -> fillPositions(warehouse, minimalStatePerWarehousesAndSupplier));

        minimalStatePerWarehousesAndSupplier.keySet().forEach(
                warehouse -> createdDeliveries.addAll(createDeliveries(warehouse, minimalStatePerWarehousesAndSupplier.get(warehouse))));

        sendEmailNotifications(createdDeliveries);

        return minimalStatePerWarehousesAndSupplier;
    }

    private void fillPositions(final Entity warehouse,
                               final Map<Long, Multimap<Long, Entity>> minimalStatePerWarehousesAndSupplier) {
        List<Entity> minimalStates = getMinimalStateGreaterThanZeroForWarehouse(warehouse);

        Map<Long, Entity> minimalStatesByProduct = minimalStates.stream().collect(
                Collectors.toMap(minimalState -> minimalState.getBelongsToField("product").getId(), minimalState -> minimalState));

        List<Entity> stocks = getWarehouseStockWithTooSmallMinState(warehouse,
                minimalStates.stream().map(minimalState -> minimalState.getBelongsToField("product")).collect(Collectors.toList()));

        Map<Long, Entity> stocksByProduct = stocks.stream()
                .collect(Collectors.toMap(stock -> stock.getIntegerField("product_id").longValue(), stock -> stock));

        Multimap<Long, Entity> positions = ArrayListMultimap.create();

        minimalStatesByProduct.keySet().forEach(
                productId -> createPositions(positions, warehouse.getId(), productId, minimalStatesByProduct, stocksByProduct));

        minimalStatePerWarehousesAndSupplier.put(warehouse.getId(), positions);
    }

    private void createPositions(final Multimap<Long, Entity> positions, final Long warehouseId, final Long productId,
                                 final Map<Long, Entity> minimalStatesByProduct, final Map<Long, Entity> stocksByProduct) {
        Entity stock = stocksByProduct.get(productId);
        Entity minimalState = minimalStatesByProduct.get(productId);

        if (Objects.isNull(stock)) {
            deliveriesService.getDefaultSupplier(productId).ifPresent(supplier -> {
                BigDecimal ordered = warehouseMinimalStateHelper.getOrderedQuantityForProductAndLocation(warehouseId, productId);

                if (warehouseMinimalStateHelper.checkIfLowerThanMinimum(productId, ordered,
                        minimalState.getDecimalField("minimumState"))) {
                    positions.put(supplier.getBelongsToField(CompanyProductFields.COMPANY).getId(), minimalState);
                }
            });
        } else {
            BigDecimal statePlusOrder = BigDecimalUtils.convertNullToZero(stock.getDecimalField("orderedQuantity"))
                    .add(BigDecimalUtils.convertNullToZero(stock.getDecimalField("quantity")), numberService.getMathContext());

            if (warehouseMinimalStateHelper.checkIfLowerThanMinimum(productId, statePlusOrder,
                    stock.getDecimalField("minimumState"))) {
                deliveriesService.getDefaultSupplier(productId).ifPresent(supplier -> positions
                        .put(supplier.getBelongsToField(CompanyProductFields.COMPANY).getId(), minimalState));
            }
        }
    }

    private List<String> createDeliveries(final Long warehouseId, final Multimap<Long, Entity> minimalStatePerWarehousesAndSupplier) {
        List<String> deliveriesNumbers = Lists.newArrayList();

        minimalStatePerWarehousesAndSupplier.keySet().forEach(supplierId -> deliveriesNumbers.add(createDelivery(warehouseId, supplierId, minimalStatePerWarehousesAndSupplier.get(supplierId))));

        return deliveriesNumbers;
    }

    private String createDelivery(final Long warehouseId, final Long supplierId, final Collection<Entity> minimalStates) {
        Entity warehouse = null;

        if (Objects.nonNull(warehouseId)) {
            warehouse = getLocationDD()
                    .get(warehouseId);
        }

        Entity supplier = companyService.getCompany(supplierId);

        return createDeliveries(warehouse, supplier, Lists.newArrayList(minimalStates));
    }

    private String createDeliveries(final Entity location, final Entity supplier, final List<Entity> warehouseMinimumStates) {
        DataDefinition deliveryDataDefinition = deliveriesService.getDeliveryDD();
        Entity delivery = deliveryDataDefinition.create();

        String generatedNumber = getNewDeliveryNumber();

        delivery.setField(DeliveryFields.NUMBER, generatedNumber);
        delivery.setField(DeliveryFields.SUPPLIER, supplier);
        delivery.setField(DeliveryFields.LOCATION, location);
        delivery.setField(DeliveryFields.STATE, DeliveryState.DRAFT);
        delivery.setField(DeliveryFields.CURRENCY, getNewDeliveryCurrency());
        delivery.setField(DeliveryFields.DELIVERY_ADDRESS, deliveriesService.getDeliveryAddressDefaultValue());
        delivery.setField(DeliveryFields.EXTERNAL_SYNCHRONIZED, true);

        delivery = deliveryDataDefinition.save(delivery);

        setDeliveryProducts(delivery, warehouseMinimumStates);

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Delivery created with number: %s", delivery.getField(DeliveryFields.NUMBER)));
        }

        return generatedNumber;
    }

    private Entity getNewDeliveryCurrency() {
        Entity parameter = parameterService.getParameter();

        Entity currency = parameter.getBelongsToField(ParameterFields.CURRENCY);

        return currency;
    }

    private String getNewDeliveryNumber() {
        return numberGeneratorService.generateNumber(DeliveriesConstants.PLUGIN_IDENTIFIER,
                DeliveriesConstants.MODEL_DELIVERY);
    }

    private List<Entity> setDeliveryProducts(final Entity delivery, final List<Entity> warehouseMinimumStates) {
        List<Entity> products = Lists.newArrayList();

        for (Entity minimumState : warehouseMinimumStates) {
            Entity deliveredProduct = deliveriesService.getOrderedProductDD().create();

            Entity product = minimumState.getBelongsToField("product");
            BigDecimal orderedQuantity = BigDecimalUtils.convertNullToZero(minimumState.getField("optimalOrderQuantity"));
            BigDecimal conversion = getConversion(product);

            deliveredProduct.setField(OrderedProductFields.DELIVERY, delivery);
            deliveredProduct.setField(OrderedProductFields.PRODUCT, product);
            deliveredProduct.setField(OrderedProductFields.ORDERED_QUANTITY, orderedQuantity);
            deliveredProduct.setField(OrderedProductFields.PRICE_PER_UNIT,
                    product.getDecimalField(ProductFieldsCNFP.LAST_PURCHASE_COST));
            deliveredProduct.setField(OrderedProductFields.CONVERSION, conversion);
            deliveredProduct.setField(OrderedProductFields.ADDITIONAL_QUANTITY,
                    orderedQuantity.multiply(conversion, numberService.getMathContext()));

            products.add(deliveriesService.getOrderedProductDD().save(deliveredProduct));
        }

        return products;
    }

    private BigDecimal getConversion(final Entity product) {
        String unit = product.getStringField(ProductFields.UNIT);
        String additionalUnit = product.getStringField(ProductFields.ADDITIONAL_UNIT);

        if (Objects.isNull(additionalUnit)) {
            return BigDecimal.ONE;
        }

        PossibleUnitConversions unitConversions = unitConversionService.getPossibleConversions(unit,
                searchCriteriaBuilder -> searchCriteriaBuilder.add(SearchRestrictions.belongsTo(
                        UnitConversionItemFieldsB.PRODUCT, product)));

        if (unitConversions.isDefinedFor(additionalUnit)) {
            return unitConversions.asUnitToConversionMap().get(additionalUnit);
        } else {
            return BigDecimal.ZERO;
        }
    }

    private List<Entity> getWarehousesFromMinimalState() {
        String query = "SELECT distinct state.location FROM #warehouseMinimalState_warehouseMinimumState state";

        return getWarehouseMinimumStateDD().find(query).list().getEntities();
    }

    public List<Entity> getMinimalStateGreaterThanZeroForWarehouse(final Entity warehouse) {
        String query = "SELECT state FROM #warehouseMinimalState_warehouseMinimumState AS state WHERE state.minimumState > 0"
                + " and state.location.id = :warehouseId";

        return getWarehouseMinimumStateDD().find(query).setParameter("warehouseId", warehouse.getId()).list().getEntities();
    }

    // WARNING unused argument is used in aspect in plugin integration
    public List<Entity> getWarehouseStockWithTooSmallMinState(final Entity warehouse, final List<Entity> products) {
        String query = "SELECT stock FROM #materialFlowResources_resourceStockDto AS stock WHERE stock.minimumState > 0"
                + " AND stock.location_id = :warehouseId";

        return getResourceStockDtoDD().find(query).setParameter("warehouseId", warehouse.getId().intValue()).list().getEntities();
    }

    private DataDefinition getStaffNotificationDD() {
        return dataDefinitionService
                .get(EmailNotificationsConstants.PLUGIN_IDENTIFIER, EmailNotificationsConstants.MODEL_STAFF_NOTIFICATION);
    }

    private DataDefinition getLocationDD() {
        return dataDefinitionService.get(MaterialFlowConstants.PLUGIN_IDENTIFIER, MaterialFlowConstants.MODEL_LOCATION);
    }

    private DataDefinition getResourceStockDtoDD() {
        return dataDefinitionService.get(MaterialFlowResourcesConstants.PLUGIN_IDENTIFIER,
                MaterialFlowResourcesConstants.MODEL_RESOURCE_STOCK_DTO);
    }

    private DataDefinition getWarehouseMinimumStateDD() {
        return dataDefinitionService.get("warehouseMinimalState", "warehouseMinimumState");
    }

}
