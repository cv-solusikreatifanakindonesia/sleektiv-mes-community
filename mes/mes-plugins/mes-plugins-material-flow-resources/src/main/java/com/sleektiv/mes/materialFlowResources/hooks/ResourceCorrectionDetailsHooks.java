package com.sleektiv.mes.materialFlowResources.hooks;

import com.sleektiv.mes.advancedGenealogy.criteriaModifier.BatchCriteriaModifier;
import com.sleektiv.mes.materialFlowResources.MaterialFlowResourcesService;
import com.sleektiv.mes.materialFlowResources.constants.ResourceCorrectionFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ResourceCorrectionDetailsHooks {

    @Autowired
    private BatchCriteriaModifier batchCriteriaModifier;

    @Autowired
    private MaterialFlowResourcesService materialFlowResourcesService;

    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent resourceCorrectionForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Entity resourceCorrection = resourceCorrectionForm.getPersistedEntityWithIncludedFormValues();

        materialFlowResourcesService.fillUnitFieldValues(view);

        setBatchLookupsProductFilterValue(view, resourceCorrection);
    }

    private void setBatchLookupsProductFilterValue(final ViewDefinitionState view, final Entity resourceCorrection) {
        LookupComponent oldBatchLookup = (LookupComponent) view.getComponentByReference(ResourceCorrectionFields.OLD_BATCH);
        LookupComponent newBatchLookup = (LookupComponent) view.getComponentByReference(ResourceCorrectionFields.NEW_BATCH);

        Entity product = resourceCorrection.getBelongsToField(ResourceCorrectionFields.PRODUCT);

        if (Objects.nonNull(product)) {
            batchCriteriaModifier.putProductFilterValue(oldBatchLookup, product);
            batchCriteriaModifier.putProductFilterValue(newBatchLookup, product);
        }
    }

}
