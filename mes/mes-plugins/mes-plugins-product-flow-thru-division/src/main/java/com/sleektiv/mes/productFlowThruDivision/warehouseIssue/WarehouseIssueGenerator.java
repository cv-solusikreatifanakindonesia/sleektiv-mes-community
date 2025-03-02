package com.sleektiv.mes.productFlowThruDivision.warehouseIssue;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.basicProductionCounting.constants.BasicProductionCountingConstants;
import com.sleektiv.mes.basicProductionCounting.constants.ProductionCountingQuantityFields;
import com.sleektiv.mes.basicProductionCounting.constants.ProductionCountingQuantityRole;
import com.sleektiv.mes.basicProductionCounting.constants.ProductionCountingQuantityTypeOfMaterial;
import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.mes.orders.states.constants.OrderState;
import com.sleektiv.mes.productFlowThruDivision.constants.ParameterFieldsPFTD;
import com.sleektiv.mes.productFlowThruDivision.constants.ProductFlowThruDivisionConstants;
import com.sleektiv.mes.productFlowThruDivision.constants.ProductsToIssue;
import com.sleektiv.mes.productFlowThruDivision.service.WarehouseIssueService;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.constans.CollectionProducts;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.constans.WarehouseIssueFields;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.states.aop.WarehouseIssueStateChangeAspect;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.states.constants.WarehouseIssueState;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.states.constants.WarehouseIssueStringValues;
import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.mes.states.service.StateChangeContextBuilder;
import com.sleektiv.mes.technologies.constants.TechnologiesConstants;
import com.sleektiv.mes.technologies.constants.TechnologyFields;
import com.sleektiv.mes.technologies.states.constants.TechnologyState;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.*;
import com.sleektiv.tenant.api.MultiTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class WarehouseIssueGenerator {

    @Autowired
    private MultiTenantService multiTenantService;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private ParameterService parameterService;

    @Autowired
    private WarehouseIssueService warehouseIssueService;

    @Autowired
    private WarehouseIssueParameterService warehouseIssueParameterService;

    @Autowired
    private WarehouseIssueStateChangeAspect warehouseIssueStateChangeAspect;

    @Autowired
    private StateChangeContextBuilder stateChangeContextBuilder;

    public void generateWarehouseIssuesTrigger() {
        multiTenantService.doInMultiTenantContext(this::generateWarehouseIssues);
    }

    public void generateWarehouseIssues() {
        Entity parameter = parameterService.getParameter();

        boolean generateWarehouseIssueToOrders = parameter
                .getBooleanField(ParameterFieldsPFTD.GENERATE_WAREHOUSE_ISSUES_TO_ORDERS);

        if (generateWarehouseIssueToOrders) {
            int daysBeforeOrderStart = parameter.getIntegerField(ParameterFieldsPFTD.DAYS_BEFORE_ORDER_START);

            Entity issueLocation = parameter.getBelongsToField(ParameterFieldsPFTD.ISSUE_LOCATION);

            List<Entity> orderDtos = getOrderDtos(daysBeforeOrderStart);

            for (Entity orderDto : orderDtos) {
                if (checkIfCanGenerateIssue(orderDto)) {
                    generateIssue(parameter, issueLocation, orderDto);

                }
            }
        }
    }

    public void generateWarehouseIssue(final Entity order) {
        Entity parameter = parameterService.getParameter();
        Entity issueLocation = parameter.getBelongsToField(ParameterFieldsPFTD.ISSUE_LOCATION);
        generateIssue(parameter, issueLocation, order);
    }

    private void generateIssue(Entity parameter, Entity issueLocation, Entity orderDto) {
        Entity newWarehouseIssue = createNewWarehouseIssue(orderDto, issueLocation);

        newWarehouseIssue = getWarehouseIssueDD().save(newWarehouseIssue);

        warehouseIssueService.fillProductsToIssue(newWarehouseIssue.getId(), CollectionProducts.ON_ORDER, orderDto,
                issueLocation);
        newWarehouseIssue = getWarehouseIssueDD().get(newWarehouseIssue.getId());

        if (newWarehouseIssue.getHasManyField(WarehouseIssueFields.PRODUCTS_TO_ISSUES).isEmpty()) {
            getWarehouseIssueDD().delete(newWarehouseIssue.getId());
        } else if (parameter.getBooleanField(ParameterFieldsPFTD.AUTOMATIC_RELEASE_AFTER_GENERATION)) {
            warehouseIssueService.copyProductsToIssue(newWarehouseIssue);

            newWarehouseIssue = getWarehouseIssueDD().get(newWarehouseIssue.getId());

            final StateChangeContext stateChangeContext = stateChangeContextBuilder.build(
                    warehouseIssueStateChangeAspect.getChangeEntityDescriber(), newWarehouseIssue, WarehouseIssueStringValues.IN_PROGRESS);
            warehouseIssueStateChangeAspect.changeState(stateChangeContext);
        }
    }

    private boolean checkIfCanGenerateIssue(final Entity orderDto) {
        Entity order = getOrderDD().get(orderDto.getId());

        List<Entity> coverageProducts = getProductionCountingQuantityDD().find()
                .add(SearchRestrictions.belongsTo(ProductionCountingQuantityFields.ORDER, order))
                .add(SearchRestrictions.eq(ProductionCountingQuantityFields.ROLE,
                        ProductionCountingQuantityRole.USED.getStringValue()))
                .add(SearchRestrictions.eq(ProductionCountingQuantityFields.TYPE_OF_MATERIAL,
                        ProductionCountingQuantityTypeOfMaterial.COMPONENT.getStringValue()))
                .list().getEntities();

        List<Entity> filteredCoverageProducts = Lists.newArrayList();

        if (warehouseIssueParameterService.getProductsToIssue().getStrValue()
                .equals(ProductsToIssue.ONLY_MATERIALS.getStrValue())) {
            for (Entity cProduct : coverageProducts) {
                SearchCriteriaBuilder scb = getTechnologyDD().find().setProjection(SearchProjections.id())
                        .add(SearchRestrictions.belongsTo(TechnologyFields.PRODUCT,
                                cProduct.getBelongsToField(ProductionCountingQuantityFields.PRODUCT)))
                        .add(SearchRestrictions.eq(TechnologyFields.ACTIVE, true))
                        .add(SearchRestrictions.eq(TechnologyFields.STATE, TechnologyState.ACCEPTED.getStringValue()))
                        .add(SearchRestrictions.eq(TechnologyFields.MASTER, true));

                if (scb.setMaxResults(1).uniqueResult() == null) {
                    filteredCoverageProducts.add(cProduct);
                }
            }

            coverageProducts = filteredCoverageProducts;
        }

        if (coverageProducts == null || coverageProducts.isEmpty()) {
            return false;
        }

        return true;
    }

    private List<Entity> getOrderDtos(final int daysBeforeOrderStart) {
        String query = "SELECT order FROM #orders_order order  "
                + "WHERE order.state IN (:states) AND startDate BETWEEN :startDate AND :endDate AND order.warehouseIssues IS EMPTY";

        SearchQueryBuilder searchQueryBuilder = getOrderDtoDD().find(query);

        Date startDate = Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(LocalDate.now().plusDays(daysBeforeOrderStart + 1).atStartOfDay().minusSeconds(1)
                .atZone(ZoneId.systemDefault()).toInstant());

        searchQueryBuilder.setParameter("startDate", startDate);
        searchQueryBuilder.setParameter("endDate", endDate);
        searchQueryBuilder.setParameterList("states",
                Arrays.asList(OrderState.ACCEPTED.getStringValue(), OrderState.IN_PROGRESS.getStringValue()));
        SearchResult orders = searchQueryBuilder.list();

        return orders.getEntities();
    }

    private Entity createNewWarehouseIssue(final Entity orderDto, final Entity placeOfIssue) {
        Entity warehouseIssue = getWarehouseIssueDD().create();

        warehouseIssue.setField(WarehouseIssueFields.COLLECTION_PRODUCTS, CollectionProducts.ON_ORDER.getStringValue());
        warehouseIssue.setField(WarehouseIssueFields.NUMBER, setNumberFromSequence());
        warehouseIssue.setField(WarehouseIssueFields.ORDER, orderDto);
        warehouseIssue.setField(WarehouseIssueFields.PLACE_OF_ISSUE, placeOfIssue);
        warehouseIssue.setField(WarehouseIssueFields.STATE, WarehouseIssueState.DRAFT.getStringValue());
        warehouseIssue.setField(WarehouseIssueFields.PRODUCTS_TO_ISSUE_MODE,
                warehouseIssueParameterService.getProductsToIssue().getStrValue());

        return warehouseIssue;
    }

    public String setNumberFromSequence() {
        return jdbcTemplate.queryForObject("SELECT generate_warehouseissue_number()", Maps.newHashMap(), String.class);
    }

    private DataDefinition getTechnologyDD() {
        return dataDefinitionService.get(TechnologiesConstants.PLUGIN_IDENTIFIER, TechnologiesConstants.MODEL_TECHNOLOGY);
    }

    private DataDefinition getOrderDD() {
        return dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_ORDER);
    }

    private DataDefinition getOrderDtoDD() {
        return dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_ORDER_DTO);
    }

    private DataDefinition getWarehouseIssueDD() {
        return dataDefinitionService.get(ProductFlowThruDivisionConstants.PLUGIN_IDENTIFIER,
                ProductFlowThruDivisionConstants.MODEL_WAREHOUSE_ISSUE);
    }

    private DataDefinition getProductionCountingQuantityDD() {
        return dataDefinitionService.get(BasicProductionCountingConstants.PLUGIN_IDENTIFIER,
                BasicProductionCountingConstants.MODEL_PRODUCTION_COUNTING_QUANTITY);
    }

}
