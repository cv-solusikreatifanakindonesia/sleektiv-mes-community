package com.sleektiv.mes.productFlowThruDivision.warehouseIssue.hooks;

import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.constans.ProductsToIssueFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class ProductToIssueDetailsHooks {

    public void onBeforeRender(final ViewDefinitionState view) {
        fillUnit(view);
        fillAdditionalUnit(view);
    }

    private void fillUnit(final ViewDefinitionState view) {
        FormComponent productToIssueForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        FieldComponent demandQuantityUnitField = (FieldComponent) view.getComponentByReference("demandQuantityUnit");
        FieldComponent correctionUnitField = (FieldComponent) view.getComponentByReference("correctionUnit");

        Entity productToIssue = productToIssueForm.getPersistedEntityWithIncludedFormValues();

        Entity product = productToIssue.getBelongsToField(ProductsToIssueFields.PRODUCT);

        if (Objects.nonNull(product)) {
            String unit = product.getStringField(ProductFields.UNIT);

            demandQuantityUnitField.setFieldValue(unit);
            correctionUnitField.setFieldValue(unit);
        } else {
            demandQuantityUnitField.setFieldValue(StringUtils.EMPTY);
        }
    }

    private void fillAdditionalUnit(final ViewDefinitionState view) {
        FormComponent productToIssueForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        FieldComponent conversionField = (FieldComponent) view.getComponentByReference("conversion");
        FieldComponent additionalDemandQuantityUnitField = (FieldComponent) view.getComponentByReference("additionalDemandQuantityUnit");

        Entity productToIssue = productToIssueForm.getPersistedEntityWithIncludedFormValues();

        Entity product = productToIssue.getBelongsToField(ProductsToIssueFields.PRODUCT);

        if (Objects.nonNull(product)) {
            String additionalUnit = product.getStringField(ProductFields.ADDITIONAL_UNIT);

            if (StringUtils.isEmpty(additionalUnit)) {
                conversionField.setFieldValue(BigDecimal.ONE);
                conversionField.setEnabled(false);
                conversionField.requestComponentUpdateState();

                additionalUnit = product.getStringField(ProductFields.UNIT);
            }

            additionalDemandQuantityUnitField.setFieldValue(additionalUnit);
            additionalDemandQuantityUnitField.requestComponentUpdateState();
        }
    }

}
