package com.sleektiv.mes.masterOrders.hooks;

import com.sleektiv.mes.masterOrders.constants.OrderedProductConfiguratorFields;
import com.sleektiv.mes.masterOrders.constants.ProductFieldsMO;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ProductHooksMO {

    public boolean validatesWith(final DataDefinition productDD, final Entity product) {
        Long productId = product.getId();

        if (Objects.nonNull(productId)) {
            Entity orderedProductConfigurator = product.getBelongsToField(ProductFieldsMO.ORDERED_PRODUCT_CONFIGURATOR);

            Entity productFromDB = productDD.get(productId);
            Entity orderedProductConfiguratorFromDB = productFromDB.getBelongsToField(ProductFieldsMO.ORDERED_PRODUCT_CONFIGURATOR);

            if (Objects.nonNull(orderedProductConfigurator) && Objects.nonNull(orderedProductConfiguratorFromDB) &&
                    !orderedProductConfigurator.getId().equals(orderedProductConfiguratorFromDB.getId())) {
                product.addError(productDD.getField(ProductFieldsMO.ORDERED_PRODUCT_CONFIGURATOR),"basic.product.orderedProductConfigurator.usedInAnother",
                        orderedProductConfiguratorFromDB.getStringField(OrderedProductConfiguratorFields.NUMBER));

                return false;
            }
        }

        return true;
    }

}
