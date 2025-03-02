package com.sleektiv.mes.productFlowThruDivision.warehouseIssue.criteriaModifiers;

import com.sleektiv.mes.materialFlow.constants.UserFieldsMF;
import com.sleektiv.mes.materialFlow.constants.UserLocationFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.EntityList;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.security.api.SecurityService;
import com.sleektiv.security.constants.SleektivSecurityConstants;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class IssueCriteriaModifiers {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void restrictToUserLocations(final SearchCriteriaBuilder scb, final FilterValueHolder filter) {
        Long currentUserId = securityService.getCurrentUserId();
        if (Objects.nonNull(currentUserId)) {
            EntityList userLocations = userDataDefinition().get(currentUserId).getHasManyField(UserFieldsMF.USER_LOCATIONS);
            if (!userLocations.isEmpty()) {
                Set<Integer> locationIds = userLocations.stream().map(ul -> ul.getBelongsToField(UserLocationFields.LOCATION))
                        .mapToInt(e -> e.getId().intValue()).boxed().collect(Collectors.toSet());
                scb.add(SearchRestrictions.in("locationId", locationIds));
            }
        }
    }

    private DataDefinition userDataDefinition() {
        return dataDefinitionService.get(SleektivSecurityConstants.PLUGIN_IDENTIFIER, SleektivSecurityConstants.MODEL_USER);
    }
}
