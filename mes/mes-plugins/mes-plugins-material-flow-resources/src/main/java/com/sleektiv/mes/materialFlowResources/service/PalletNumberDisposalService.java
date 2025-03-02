package com.sleektiv.mes.materialFlowResources.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.sleektiv.mes.basic.constants.BasicConstants;
import com.sleektiv.mes.basic.constants.PalletNumberFields;
import com.sleektiv.mes.materialFlowResources.constants.MaterialFlowResourcesConstants;
import com.sleektiv.mes.materialFlowResources.constants.ResourceFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchRestrictions;

@Service
public class PalletNumberDisposalService {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    private DataDefinition resourceDataDefinition() {
        return dataDefinitionService.get(MaterialFlowResourcesConstants.PLUGIN_IDENTIFIER,
                MaterialFlowResourcesConstants.MODEL_RESOURCE);
    }

    public void tryToDispose(Entity palletNumber) {
        if (palletNumber != null) {
            DataDefinition palletNumberDataDefinition = palletNumber.getDataDefinition();
            Assert.isTrue(palletNumberDataDefinition.getPluginIdentifier().equals(BasicConstants.PLUGIN_IDENTIFIER));
            Assert.isTrue(palletNumberDataDefinition.getName().equals(BasicConstants.MODEL_PALLET_NUMBER));

            if (canDisposePalletNumber(palletNumber)) {
                palletNumber.setField(PalletNumberFields.ISSUE_DATE_TIME, new Date());
                palletNumberDataDefinition.save(palletNumber);
            }
        }
    }

    private boolean thereAreNoResourcesAssociatedWithGivenPalletNumber(Entity palletNumber) {
        return resourceDataDefinition().count(SearchRestrictions.belongsTo(ResourceFields.PALLET_NUMBER, palletNumber)) == 0;
    }

    private boolean canDisposePalletNumber(Entity palletNumber) {
        return thereAreNoResourcesAssociatedWithGivenPalletNumber(palletNumber);
    }
}
