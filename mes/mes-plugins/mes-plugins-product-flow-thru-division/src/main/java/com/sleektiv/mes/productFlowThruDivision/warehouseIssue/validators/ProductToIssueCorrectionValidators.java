package com.sleektiv.mes.productFlowThruDivision.warehouseIssue.validators;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.constans.ProductToIssueCorrectionFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

@Service
public class ProductToIssueCorrectionValidators {

    public boolean validate(final DataDefinition dataDefinition, final Entity correction) {
        BigDecimal quantityToIssue = correction.getDecimalField(ProductToIssueCorrectionFields.QUANTITY_TO_ISSUE);
        BigDecimal correctionQuantity = correction.getDecimalField(ProductToIssueCorrectionFields.CORRECTION_QUANTITY);

        if (quantityToIssue != null && correctionQuantity != null) {
            if (quantityToIssue.compareTo(correctionQuantity) < 0) {
                correction.addError(dataDefinition.getField(ProductToIssueCorrectionFields.CORRECTION_QUANTITY),
                        "productFlowThruDivision.productToIssueCorrection.validation.error.quantityTooLow");
                return false;
            }
        }
        return true;
    }
}
