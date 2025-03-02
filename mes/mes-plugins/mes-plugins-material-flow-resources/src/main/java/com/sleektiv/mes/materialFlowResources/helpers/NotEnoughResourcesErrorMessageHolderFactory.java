package com.sleektiv.mes.materialFlowResources.helpers;

import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.model.api.NumberService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotEnoughResourcesErrorMessageHolderFactory {

    @Autowired
    private NumberService numberService;

    @Autowired
    private TranslationService translationService;

    public NotEnoughResourcesErrorMessageHolder create() {
        return new NotEnoughResourcesErrorMessageHolder(numberService, translationService);
    }

}
