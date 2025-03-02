package com.sleektiv.mes.orderSupplies.hooks;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.orderSupplies.constants.CoverageProductFields;
import com.sleektiv.mes.orderSupplies.constants.CoverageProductGeneratedFields;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class CoverageProductLoggingDetailsHooks {

    

    public void updateCriteriaModifiersState(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        LookupComponent companyLookup = (LookupComponent) view.getComponentByReference("company");

        FilterValueHolder filterValueHolder = companyLookup.getFilterValue();
        filterValueHolder.put(CoverageProductGeneratedFields.PRODUCT_ID,
                form.getPersistedEntityWithIncludedFormValues().getBelongsToField(CoverageProductFields.PRODUCT).getId());
        companyLookup.setFilterValue(filterValueHolder);
    }
}
