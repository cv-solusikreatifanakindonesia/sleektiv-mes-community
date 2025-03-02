package com.sleektiv.mes.technologies.imports.productToProductGroupTechnology;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.ProductFamilyElementType;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.basic.imports.services.XlsxImportService;
import com.sleektiv.mes.technologies.constants.ProductToProductGroupFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

@Service
public class ProductToProductGroupTechnologyXlsxImportService extends XlsxImportService {

    @Override
    public void validateEntity(final Entity entity, final DataDefinition dataDefinition) {
        if (entity.getBelongsToField(ProductToProductGroupFields.PRODUCT_FAMILY) != null
                && !ProductFamilyElementType.PRODUCTS_FAMILY.getStringValue()
                        .equals(entity.getBelongsToField(ProductToProductGroupFields.PRODUCT_FAMILY)
                                .getStringField(ProductFields.ENTITY_TYPE))) {
            entity.addError(dataDefinition.getField(ProductToProductGroupFields.PRODUCT_FAMILY),
                    "technologies.productToProductGroupTechnology.validate.error.productIsntProductFamily");
        }
        if (entity.getBelongsToField(ProductToProductGroupFields.PRODUCT_FAMILY) != null
                && entity.getBelongsToField(ProductToProductGroupFields.ORDER_PRODUCT) != null
                && !entity.getBelongsToField(ProductToProductGroupFields.PRODUCT_FAMILY).equals(entity
                        .getBelongsToField(ProductToProductGroupFields.ORDER_PRODUCT).getBelongsToField(ProductFields.PARENT))) {
            entity.addError(dataDefinition.getField(ProductToProductGroupFields.ORDER_PRODUCT),
                    "technologies.productToProductGroupTechnology.validate.error.productBelongsToAnotherFamily");
        }
    }
}
