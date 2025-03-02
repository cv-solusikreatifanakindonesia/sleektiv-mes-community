package com.sleektiv.mes.technologies.hooks;

import com.sleektiv.mes.basic.util.UnitService;
import com.sleektiv.mes.technologies.constants.OperationProductInComponentFields;
import com.sleektiv.mes.technologies.constants.ProductBySizeGroupFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductBySizeGroupDetailsHooks {

    @Autowired private UnitService unitService;

    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent formComponent = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity entity = formComponent.getEntity();
        FieldComponent quantityField = (FieldComponent) view.getComponentByReference(ProductBySizeGroupFields.QUANTITY);
        FieldComponent unitField = (FieldComponent) view.getComponentByReference(ProductBySizeGroupFields.UNIT);
        FieldComponent givenQuantityField = (FieldComponent) view.getComponentByReference(ProductBySizeGroupFields.GIVEN_QUANTITY);
        FieldComponent givenUnitField = (FieldComponent) view.getComponentByReference(ProductBySizeGroupFields.GIVEN_UNIT);

        if(entity.getBelongsToField(ProductBySizeGroupFields.OPERATION_PRODUCT_IN_COMPONENT).getBooleanField(
                OperationProductInComponentFields.VARIOUS_QUANTITIES_IN_PRODUCTS_BY_SIZE)) {
            givenQuantityField.setEnabled(true);
            givenUnitField.setEnabled(true);
        } else {
            givenQuantityField.setEnabled(false);
            givenUnitField.setEnabled(false);

        }
        unitService.fillProductUnitBeforeRenderIfEmpty(view, ProductBySizeGroupFields.UNIT);
        unitService.fillProductUnitBeforeRenderIfEmpty(view, ProductBySizeGroupFields.GIVEN_UNIT);

    }

}
