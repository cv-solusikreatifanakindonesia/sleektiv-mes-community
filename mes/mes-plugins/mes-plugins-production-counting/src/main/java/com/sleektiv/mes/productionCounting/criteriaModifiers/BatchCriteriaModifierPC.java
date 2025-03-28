package com.sleektiv.mes.productionCounting.criteriaModifiers;

import com.sleektiv.mes.advancedGenealogy.constants.AdvancedGenealogyConstants;
import com.sleektiv.mes.advancedGenealogy.constants.BatchFields;
import com.sleektiv.mes.basic.constants.BasicConstants;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.plugin.api.PluginUtils;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BatchCriteriaModifierPC {

    public static final String ORDER_ID = "orderId";

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void filterByOrder(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        if (filterValue.has(ORDER_ID)) {
            Long orderId = filterValue.getLong(ORDER_ID);
            if (PluginUtils.isEnabled("advancedGenealogyForOrders")) {
                List<Entity> entities = dataDefinitionService
                        .get(AdvancedGenealogyConstants.PLUGIN_IDENTIFIER, AdvancedGenealogyConstants.MODEL_TRACKING_RECORD)
                        .find("select producedBatch.id as batchId from #advancedGenealogy_trackingRecord where order.id = :orderId ")
                        .setLong("orderId", orderId).list().getEntities();
                if (!entities.isEmpty()) {
                    scb.add(SearchRestrictions.in("id",
                            entities.stream().map(e -> e.getLongField("batchId")).collect(Collectors.toList())));
                } else {
                    scb.add(SearchRestrictions.idEq(-1));
                }
            } else {
                long productId = dataDefinitionService
                        .get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_ORDER).get(orderId).getBelongsToField(OrderFields.PRODUCT).getId();
                scb.add(SearchRestrictions.belongsTo(BatchFields.PRODUCT, BasicConstants.PLUGIN_IDENTIFIER,
                        BasicConstants.MODEL_PRODUCT, productId));
            }
        }
    }
}
