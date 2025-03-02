package com.sleektiv.mes.basic.hooks;

import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.UserFieldsB;
import com.sleektiv.security.api.SecurityService;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.LookupComponent;

@Service
public class UserDetailsHooksB {

    @Autowired
    private SecurityService securityService;

    public void setFieldsEnabledForAdmin(final ViewDefinitionState view) {
        if (securityService.hasCurrentUserRole("ROLE_ADMIN")) {
            Stream.of(UserFieldsB.STAFF, UserFieldsB.FACTORY).forEach(fieldName -> {
                LookupComponent lookupComponent = (LookupComponent) view.getComponentByReference(fieldName);

                if (Objects.nonNull(lookupComponent)) {
                    lookupComponent.setEnabled(true);
                    lookupComponent.requestComponentUpdateState();
                }
            });
        }
    }

}
