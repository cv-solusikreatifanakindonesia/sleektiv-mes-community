package com.sleektiv.mes.materialFlowResources.hooks;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.materialFlowResources.constants.DocumentPositionParametersFields;
import com.sleektiv.mes.materialFlowResources.constants.MaterialFlowResourcesConstants;
import com.sleektiv.mes.materialFlowResources.constants.ResourceDtoFields;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.CheckBoxComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class ResourcesListHooks {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void applyFilters(ViewDefinitionState view) {
        boolean isShortFilter = ((CheckBoxComponent) view.getComponentByReference(ResourceDtoFields.IS_SHORT_FILTER)).isChecked();
        boolean isDeadlineFilter = ((CheckBoxComponent) view.getComponentByReference(ResourceDtoFields.IS_DEADLINE_FILTER))
                .isChecked();
        GridComponent resourcesGrid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
        Integer shortExpiryDate = dataDefinitionService
                .get(MaterialFlowResourcesConstants.PLUGIN_IDENTIFIER,
                        MaterialFlowResourcesConstants.MODEL_DOCUMENT_POSITION_PARAMETERS)
                .find().setMaxResults(1).uniqueResult().getIntegerField(DocumentPositionParametersFields.SHORT_EXPIRY_DATE);
        if (isShortFilter) {
            if (shortExpiryDate == null) {
                resourcesGrid.addMessage("materialFlowResources.resource.missing.parameter.shortExpiryDate.error",
                        ComponentState.MessageType.FAILURE);
            } else {
                resourcesGrid.setCustomRestriction(searchBuilder -> searchBuilder.add(SearchRestrictions.and(
                        SearchRestrictions.ge(ResourceDtoFields.EXPIRATION_DATE, DateTime.now().withTimeAtStartOfDay().toDate()),
                        SearchRestrictions.le(ResourceDtoFields.EXPIRATION_DATE,
                                DateTime.now().withTimeAtStartOfDay().plusDays(shortExpiryDate).toDate()))));
            }
        } else if (isDeadlineFilter) {
            resourcesGrid.setCustomRestriction(searchBuilder -> searchBuilder.add(
                    SearchRestrictions.lt(ResourceDtoFields.EXPIRATION_DATE, DateTime.now().withTimeAtStartOfDay().toDate())));
        }
    }
}
