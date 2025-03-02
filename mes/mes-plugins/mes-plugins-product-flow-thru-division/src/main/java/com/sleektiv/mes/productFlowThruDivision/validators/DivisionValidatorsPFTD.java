package com.sleektiv.mes.productFlowThruDivision.validators;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.productFlowThruDivision.constants.DivisionFieldsPFTD;
import com.sleektiv.mes.productFlowThruDivision.constants.ProductionFlowComponent;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

@Service
public class DivisionValidatorsPFTD {

    public boolean checkRequiredFields(final DataDefinition dataDefinition, final Entity division) {
        String productionFlow = division.getStringField(DivisionFieldsPFTD.PRODUCTION_FLOW);
        Entity productsFlowLocation = division.getBelongsToField(DivisionFieldsPFTD.PRODUCTS_FLOW_LOCATION);
        if (ProductionFlowComponent.WAREHOUSE.getStringValue().equals(productionFlow) && productsFlowLocation == null) {
            division.addError(dataDefinition.getField(DivisionFieldsPFTD.PRODUCTS_FLOW_LOCATION),
                    "sleektivView.validate.field.error.missing");
            return false;
        }
        return checkIfWarehousesAreDifferent(division);
    }

    private boolean checkIfWarehousesAreDifferent(final Entity division) {
        Entity componentsLocation = division.getBelongsToField(DivisionFieldsPFTD.COMPONENTS_LOCATION);
        Entity componentsOutLocation = division.getBelongsToField(DivisionFieldsPFTD.COMPONENTS_OUTPUT_LOCATION);

        if (componentsLocation != null && componentsOutLocation != null
                && componentsLocation.getId().equals(componentsOutLocation.getId())) {
            division.addGlobalError("technologies.technology.error.componentsLocationsAreSame", false);
            return false;
        }
        return true;
    }
}
