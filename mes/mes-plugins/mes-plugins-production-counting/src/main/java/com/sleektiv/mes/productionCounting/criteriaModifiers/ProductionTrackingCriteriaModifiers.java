package com.sleektiv.mes.productionCounting.criteriaModifiers;

import com.sleektiv.mes.basic.constants.UserFieldsB;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.security.api.SecurityService;
import com.sleektiv.security.constants.SleektivSecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ProductionTrackingCriteriaModifiers {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void onlyMyRecords(final SearchCriteriaBuilder scb) {
        Long currentUserId = securityService.getCurrentUserId();

        if (Objects.nonNull(currentUserId)) {
            boolean showOnlyMyRegistrationRecords = userDataDefinition().get(currentUserId).getBooleanField(UserFieldsB.SHOW_ONLY_MY_REGISTRATION_RECORDS);

            if (showOnlyMyRegistrationRecords) {
                scb.add(SearchRestrictions.eq("createUser", securityService.getCurrentUserName()));
            }
        }
    }

    private DataDefinition userDataDefinition() {
        return dataDefinitionService.get(SleektivSecurityConstants.PLUGIN_IDENTIFIER, SleektivSecurityConstants.MODEL_USER);
    }

}
