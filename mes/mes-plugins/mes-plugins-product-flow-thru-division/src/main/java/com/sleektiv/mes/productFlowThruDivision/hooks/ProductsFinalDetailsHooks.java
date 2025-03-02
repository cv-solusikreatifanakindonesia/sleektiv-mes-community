package com.sleektiv.mes.productFlowThruDivision.hooks;

import com.sleektiv.mes.productFlowThruDivision.constants.OperationProductOutComponentFieldsPFTD;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ProductsFinalDetailsHooks {

    public void onBeforeRender(final ViewDefinitionState view) {
        List<String> references = Collections.singletonList(OperationProductOutComponentFieldsPFTD.PRODUCTS_INPUT_LOCATION);
        setFieldsRequired(view, references);
    }

    private void setFieldsRequired(final ViewDefinitionState view, final List<String> references) {
        for (String reference : references) {
            FieldComponent field = (FieldComponent) view.getComponentByReference(reference);
            field.setRequired(true);
            field.requestComponentUpdateState();
        }
    }
}
