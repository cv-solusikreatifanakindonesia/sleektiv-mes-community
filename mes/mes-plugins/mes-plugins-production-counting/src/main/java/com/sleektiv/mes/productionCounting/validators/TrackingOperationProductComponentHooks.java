package com.sleektiv.mes.productionCounting.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.basic.constants.UnitConversionItemFieldsB;
import com.sleektiv.mes.productionCounting.constants.TrackingOperationProductInComponentFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.model.api.units.PossibleUnitConversions;
import com.sleektiv.model.api.units.UnitConversionService;

@Service
public class TrackingOperationProductComponentHooks {

    @Autowired
    private UnitConversionService unitConversionService;

    public boolean validatesWith(final DataDefinition dataDefinition, final Entity trackingOperationProduct) {
        String givenUnit = trackingOperationProduct.getStringField(TrackingOperationProductInComponentFields.GIVEN_UNIT);
        if (givenUnit == null) {
            return true;
        }
        Entity product = trackingOperationProduct.getBelongsToField(TrackingOperationProductInComponentFields.PRODUCT);
        String unit = product.getStringField(ProductFields.UNIT);
        if (unit.equals(givenUnit)) {
            return true;
        }
        PossibleUnitConversions unitConversions = unitConversionService.getPossibleConversions(unit,
                searchCriteriaBuilder -> searchCriteriaBuilder
                        .add(SearchRestrictions.belongsTo(UnitConversionItemFieldsB.PRODUCT, product)));
        if (!unitConversions.isDefinedFor(givenUnit)) {
            trackingOperationProduct.addError(dataDefinition.getField(TrackingOperationProductInComponentFields.GIVEN_QUANTITY),
                    "technologies.operationProductInComponent.validate.error.missingUnitConversion");
            return false;
        }
        return true;

    }
}
