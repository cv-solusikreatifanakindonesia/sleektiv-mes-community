package com.sleektiv.mes.basic.criteriaModifiers;

import com.sleektiv.mes.basic.constants.BasicConstants;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.basic.constants.SubstituteComponentFields;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReplacementsCriteriaModifiers {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void filter(final SearchCriteriaBuilder scb, final FilterValueHolder filter) {
        if (filter.has("PRODUCT_ID")) {
            Long productId = filter.getLong("PRODUCT_ID");
            Entity product = dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_PRODUCT).get(
                    productId);
            scb.add(SearchRestrictions.idNe(productId));
            product.getHasManyField(ProductFields.SUBSTITUTE_COMPONENTS).forEach(sc -> {
                scb.add(SearchRestrictions.idNe(sc.getBelongsToField(SubstituteComponentFields.PRODUCT).getId()));
            });
        }
    }
}
