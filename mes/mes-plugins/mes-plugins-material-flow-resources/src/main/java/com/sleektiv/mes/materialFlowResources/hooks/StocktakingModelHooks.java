package com.sleektiv.mes.materialFlowResources.hooks;

import com.sleektiv.mes.materialFlowResources.constants.StocktakingFields;
import com.sleektiv.mes.materialFlowResources.constants.StorageLocationMode;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class StocktakingModelHooks {

    public void onSave(final DataDefinition stocktakingDD, final Entity stocktaking) {
        if (StorageLocationMode.ALL.getStringValue().equals(stocktaking.getStringField(StocktakingFields.STORAGE_LOCATION_MODE))) {
            stocktaking.setField(StocktakingFields.STORAGE_LOCATIONS, null);
        }
        if (Objects.nonNull(stocktaking.getId())) {
            Entity stocktakingDb = stocktakingDD.get(stocktaking.getId());
            if (!stocktaking.getBelongsToField("location").getId()
                    .equals(stocktakingDb.getBelongsToField("location").getId())) {
                stocktaking.setField("storageLocations", null);
            }
        }
    }
}
