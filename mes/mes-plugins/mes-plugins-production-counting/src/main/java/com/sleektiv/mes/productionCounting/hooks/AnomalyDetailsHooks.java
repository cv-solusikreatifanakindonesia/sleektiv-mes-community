package com.sleektiv.mes.productionCounting.hooks;

import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.productionCounting.constants.AnomalyFields;
import com.sleektiv.mes.productionCounting.constants.ProductionTrackingFields;
import com.sleektiv.mes.productionLines.constants.ProductionLineFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AnomalyDetailsHooks {

    

    public void onBeforeRender(final ViewDefinitionState view) {
        fillFields(view);
    }

    private void fillFields(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity anomaly = form.getEntity().getDataDefinition().get(form.getEntityId());

        FieldComponent productionTrackingNumberField = (FieldComponent) view.getComponentByReference("productionTrackingNumber");
        FieldComponent createDateField = (FieldComponent) view.getComponentByReference("createDate");
        createDateField.setEnabled(false);
        FieldComponent orderNumberField = (FieldComponent) view.getComponentByReference("orderNumber");
        FieldComponent masterProductNumberField = (FieldComponent) view.getComponentByReference("masterProductNumber");
        FieldComponent productionLineNumberField = (FieldComponent) view.getComponentByReference("productionLineNumber");
        FieldComponent productNumberField = (FieldComponent) view.getComponentByReference("productNumber");
        FieldComponent reasonsField = (FieldComponent) view.getComponentByReference("reasons");
        FieldComponent unitField = (FieldComponent) view.getComponentByReference("unit");
        FieldComponent locationField = (FieldComponent) view.getComponentByReference("location");

        Entity productionTracking = anomaly.getBelongsToField("productionTracking");

        productionTrackingNumberField.setFieldValue(productionTracking.getStringField(ProductionTrackingFields.NUMBER));
        orderNumberField.setFieldValue(
                productionTracking.getBelongsToField(ProductionTrackingFields.ORDER).getStringField(OrderFields.NUMBER));

        productionLineNumberField.setFieldValue(productionTracking.getBelongsToField(ProductionTrackingFields.ORDER)
                .getBelongsToField(OrderFields.PRODUCTION_LINE).getStringField(ProductionLineFields.NUMBER));
        masterProductNumberField.setFieldValue(anomaly.getBelongsToField("masterProduct").getStringField(ProductFields.NUMBER));
        productNumberField.setFieldValue(anomaly.getBelongsToField("product").getStringField(ProductFields.NUMBER));
        unitField.setFieldValue(anomaly.getBelongsToField("product").getStringField(ProductFields.UNIT));
        reasonsField.setFieldValue(anomaly.getHasManyField("anomalyReasons").stream().map(a -> a.getStringField("name"))
                .collect(Collectors.joining(", ")));
        if (Objects.nonNull(anomaly.getBelongsToField(AnomalyFields.LOCATION))) {
            locationField.setFieldValue(anomaly.getBelongsToField(AnomalyFields.LOCATION).getStringField("number"));
            locationField.requestComponentUpdateState();
        }

        if (anomaly.getStringField(AnomalyFields.STATE).equals("03completed")) {
            view.getComponentByReference("anomalyExplanations").setEnabled(false);
        }

        productionTrackingNumberField.requestComponentUpdateState();
        orderNumberField.requestComponentUpdateState();
        productionLineNumberField.requestComponentUpdateState();
        masterProductNumberField.requestComponentUpdateState();
        productNumberField.requestComponentUpdateState();
        unitField.requestComponentUpdateState();
        reasonsField.requestComponentUpdateState();
    }

}
