package com.sleektiv.mes.productionLines.hooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.productionLines.constants.UserFieldsPL;
import com.sleektiv.security.api.SecurityService;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.LookupComponent;

@Service
public class UserDetailsHooksPL {

    @Autowired
    private SecurityService securityService;

    public void setFieldsEnabledForAdmin(final ViewDefinitionState view) {
        if (securityService.hasCurrentUserRole("ROLE_ADMIN")) {
            LookupComponent lookupComponent = (LookupComponent) view.getComponentByReference(UserFieldsPL.PRODUCTION_LINE);
            lookupComponent.setEnabled(true);
            lookupComponent.requestComponentUpdateState();
        }
    }

}
