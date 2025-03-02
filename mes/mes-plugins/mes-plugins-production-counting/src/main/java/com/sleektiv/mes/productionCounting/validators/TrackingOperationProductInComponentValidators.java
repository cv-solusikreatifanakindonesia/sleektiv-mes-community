package com.sleektiv.mes.productionCounting.validators;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.sleektiv.mes.productionCounting.constants.TrackingOperationProductInComponentFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.FieldDefinition;

@Component
public class TrackingOperationProductInComponentValidators {

    public boolean validateWasteUsedQuantity(final DataDefinition dataDefinition, final FieldDefinition fieldDefinition,
            final Entity entity, final Object fieldOldValue, final Object fieldNewValue) {
        BigDecimal wasteUsedQuantity = entity.getDecimalField(TrackingOperationProductInComponentFields.WASTE_USED_QUANTITY);
        boolean wasteUsed = entity.getBooleanField(TrackingOperationProductInComponentFields.WASTE_USED);
        return !(wasteUsed && wasteUsedQuantity == null);
    }

}
