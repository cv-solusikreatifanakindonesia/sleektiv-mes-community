package com.sleektiv.mes.productFlowThruDivision.warehouseIssue.hooks;

import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.constans.IssueFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class IssueDetailsHooks {

    public void onBeforeRender(final ViewDefinitionState view) {
        fillUnit(view);
    }

    private void fillUnit(final ViewDefinitionState view) {
        FormComponent issueForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        FieldComponent issueUnitField = (FieldComponent) view.getComponentByReference("issueQuantityUnit");
        FieldComponent demandUnitField = (FieldComponent) view.getComponentByReference("demandQuantityUnit");

        Entity issue = issueForm.getPersistedEntityWithIncludedFormValues();

        if (issue.getBooleanField(IssueFields.ISSUED)) {
            issueForm.setFormEnabled(false);
            return;
        }

        Entity product = issue.getBelongsToField(IssueFields.PRODUCT);

        if (Objects.nonNull(product)) {
            String unit = product.getStringField(ProductFields.UNIT);

            issueUnitField.setFieldValue(unit);
            demandUnitField.setFieldValue(unit);
        } else {
            issueUnitField.setFieldValue(StringUtils.EMPTY);
            demandUnitField.setFieldValue(StringUtils.EMPTY);
        }
    }

}
