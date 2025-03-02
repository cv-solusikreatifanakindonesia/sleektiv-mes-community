package com.sleektiv.mes.materialFlow.hooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.security.api.SecurityService;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.AwesomeDynamicListComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class UserDetailsHooksMF {

    private static final String L_USER_LOCATIONS = "userLocations";

    private static final String L_LOCATION = "location";

    private static final String L_USER_LOCATIONS_BORDER_LAYOUT = "userLocationsBorderLayout";

    @Autowired
    private SecurityService securityService;

    public void setupRibbonForAdmin(final ViewDefinitionState view) {
        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
        if(!securityService.hasCurrentUserRole("ROLE_USERS_EDIT") && securityService.hasCurrentUserRole("ROLE_ADMIN")){
            RibbonActionItem saveActionItem = window.getRibbon().getGroupByName("actions").getItemByName("save");
            saveActionItem.setEnabled(true);
            saveActionItem.requestUpdate(true);
        }
    }
    public void setupUserLocationsSection(final ViewDefinitionState view) {
        view.getComponentByReference(L_USER_LOCATIONS_BORDER_LAYOUT).setVisible(true);

        // Override effect of com.sleektiv.plugins.users.internal.UserService.disableFormForAdmin
        if (securityService.hasCurrentUserRole("ROLE_ADMIN")) {
            view.getComponentByReference(L_USER_LOCATIONS).setEnabled(true);
            AwesomeDynamicListComponent userLocations = (AwesomeDynamicListComponent) view
                    .getComponentByReference(L_USER_LOCATIONS);
            userLocations.getFormComponents().forEach(fc -> fc.findFieldComponentByName(L_LOCATION).setEnabled(true));
        }
    }

}
