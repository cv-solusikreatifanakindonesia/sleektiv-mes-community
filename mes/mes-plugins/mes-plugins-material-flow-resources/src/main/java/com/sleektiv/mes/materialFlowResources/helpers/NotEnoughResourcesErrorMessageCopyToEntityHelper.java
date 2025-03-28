package com.sleektiv.mes.materialFlowResources.helpers;

import static com.sleektiv.mes.states.messages.util.MessagesUtil.joinArgs;
import static org.apache.commons.lang3.ArrayUtils.toArray;

import com.sleektiv.mes.materialFlow.constants.LocationFields;
import com.sleektiv.model.api.Entity;

public final class NotEnoughResourcesErrorMessageCopyToEntityHelper {

    public static void addError(final Entity entity, final Entity warehouseFrom,
            NotEnoughResourcesErrorMessageHolder errorMessageHolder) {

        String warehouseName = warehouseFrom.getStringField(LocationFields.NAME);
        String longMessage = errorMessageHolder.toString();

        if (joinArgs(toArray(longMessage, warehouseName)).length() < 255) {
            entity.addGlobalError("materialFlow.error.position.quantity.notEnoughResources", false, longMessage, warehouseName);
        } else {
            for (String message : errorMessageHolder.getErrorMessages()) {
                entity.addGlobalError("materialFlow.error.position.quantity.notEnoughResources", false, message, warehouseName);
            }
        }
    }

}
