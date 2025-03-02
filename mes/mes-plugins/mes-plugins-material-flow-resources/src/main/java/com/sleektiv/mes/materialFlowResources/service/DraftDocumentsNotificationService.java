package com.sleektiv.mes.materialFlowResources.service;

import static com.sleektiv.model.api.search.SearchRestrictions.eq;
import static com.sleektiv.model.api.search.SearchRestrictions.in;
import static com.sleektiv.model.api.search.SearchRestrictions.isNull;
import static com.sleektiv.model.api.search.SearchRestrictions.or;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.materialFlow.constants.UserFieldsMF;
import com.sleektiv.mes.materialFlow.constants.UserLocationFields;
import com.sleektiv.mes.materialFlowResources.constants.DocumentFields;
import com.sleektiv.mes.materialFlowResources.constants.DocumentState;
import com.sleektiv.mes.materialFlowResources.constants.MaterialFlowResourcesConstants;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.EntityList;
import com.sleektiv.model.api.search.JoinType;
import com.sleektiv.model.api.search.SearchConjunction;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.security.api.SecurityService;
import com.sleektiv.security.constants.SleektivSecurityConstants;

@Service
public class DraftDocumentsNotificationService {

    static final String ROLE_DOCUMENTS_NOTIFICATION = "ROLE_DOCUMENTS_NOTIFICATION";

    private final SecurityService securityService;

    private final DataDefinitionService dataDefinitionService;

    @Autowired
    public DraftDocumentsNotificationService(SecurityService securityService, DataDefinitionService dataDefinitionService) {
        this.securityService = securityService;
        this.dataDefinitionService = dataDefinitionService;
    }

    private DataDefinition userDataDefinition() {
        return dataDefinitionService.get(SleektivSecurityConstants.PLUGIN_IDENTIFIER, SleektivSecurityConstants.MODEL_USER);
    }

    private DataDefinition documentDataDefinition() {
        return dataDefinitionService.get(MaterialFlowResourcesConstants.PLUGIN_IDENTIFIER,
                MaterialFlowResourcesConstants.MODEL_DOCUMENT);
    }

    public boolean shouldNotifyCurrentUser() {
        Long currentUserId = securityService.getCurrentUserId();
        return currentUserId != null && securityService.hasCurrentUserRole(ROLE_DOCUMENTS_NOTIFICATION)
                && countDraftDocumentsForUser(currentUserId) > 0;
    }

    int countDraftDocumentsForUser(Long currentUserId) {
        EntityList userLocations = userDataDefinition().get(currentUserId).getHasManyField(UserFieldsMF.USER_LOCATIONS);
        SearchConjunction conjunction = SearchRestrictions.conjunction();
        conjunction.add(eq(DocumentFields.STATE, DocumentState.DRAFT.getStringValue()));
        conjunction.add(eq(DocumentFields.ACTIVE, Boolean.TRUE));
        conjunction.add(isNull("order.id"));

        SearchCriteriaBuilder criteriaBuilder = documentDataDefinition().find();
        if (!userLocations.isEmpty()) {
            criteriaBuilder.createAlias(DocumentFields.LOCATION_FROM, "locFrom", JoinType.LEFT);
            criteriaBuilder.createAlias(DocumentFields.LOCATION_TO, "locTo", JoinType.LEFT);

            Set<Long> locationIds = userLocations.stream().map(ul -> ul.getBelongsToField(UserLocationFields.LOCATION))
                    .map(Entity::getId).collect(Collectors.toSet());
            conjunction.add(or(in("locFrom.id", locationIds), in("locTo.id", locationIds)));
        }
        criteriaBuilder.add(conjunction);
        return criteriaBuilder.list().getTotalNumberOfEntities();
    }

}
