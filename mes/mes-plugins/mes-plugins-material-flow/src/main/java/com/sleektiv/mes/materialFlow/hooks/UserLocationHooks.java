package com.sleektiv.mes.materialFlow.hooks;

import static com.sleektiv.model.api.search.SearchRestrictions.belongsTo;
import static com.sleektiv.model.api.search.SearchRestrictions.ne;
import static java.util.Objects.requireNonNull;

import org.springframework.stereotype.Component;

import com.sleektiv.mes.materialFlow.constants.UserLocationFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchConjunction;
import com.sleektiv.model.api.search.SearchRestrictions;

@Component
public class UserLocationHooks {

    public boolean validatesWith(final DataDefinition userLocationDD, final Entity userLocation) {
        return !isLocationNull(userLocationDD, userLocation) && !checkIfUserLocationAlreadyExists(userLocationDD, userLocation);
    }

    private boolean checkIfUserLocationAlreadyExists(final DataDefinition userLocationDD, final Entity userLocation) {
        Entity location = requireNonNull(userLocation.getBelongsToField(UserLocationFields.LOCATION));
        Entity user = requireNonNull(userLocation.getBelongsToField(UserLocationFields.USER));

        SearchConjunction conjunction = SearchRestrictions.conjunction();
        conjunction.add(belongsTo(UserLocationFields.LOCATION, location));
        conjunction.add(belongsTo(UserLocationFields.USER, user));

        if (userLocation.getId() != null) {
            conjunction.add(ne("id", userLocation.getId()));
        }

        boolean exists = 0 != userLocationDD.count(conjunction);
        if (exists) {
            userLocation.addError(userLocationDD.getField(UserLocationFields.LOCATION),
                    "sleektivView.validate.field.error.invalidUniqueType");
        }
        return exists;
    }

    private boolean isLocationNull(final DataDefinition userLocationDD, final Entity userLocation) {
        boolean result = userLocation.getBelongsToField(UserLocationFields.LOCATION) == null;
        if (result) {
            userLocation.addError(userLocationDD.getField(UserLocationFields.LOCATION),
                    "sleektivView.validate.field.error.missing");
        }
        return result;
    }

}
