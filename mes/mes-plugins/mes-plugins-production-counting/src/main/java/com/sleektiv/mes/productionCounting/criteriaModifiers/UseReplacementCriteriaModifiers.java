package com.sleektiv.mes.productionCounting.criteriaModifiers;

import com.sleektiv.mes.basic.constants.BasicConstants;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.basic.constants.SubstituteComponentFields;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UseReplacementCriteriaModifiers {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void filter(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {

        if (filterValue.has("BASIC_PRODUCT_ID")) {
            Entity basicProduct = dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_PRODUCT).get(
                    filterValue.getLong("BASIC_PRODUCT_ID"));
            List<Entity> replacements = basicProduct.getHasManyField(ProductFields.SUBSTITUTE_COMPONENTS);
            List<Long> ids = replacements.stream()
                    .filter(r -> Objects.nonNull(r.getBelongsToField(SubstituteComponentFields.PRODUCT)))
                    .map(r -> r.getBelongsToField(SubstituteComponentFields.PRODUCT).getId())
                    .collect(Collectors.toList());
            scb.add(SearchRestrictions.in("id", ids));
        } else {
            scb.add(SearchRestrictions.idEq(-1));
        }
    }
}
