package com.sleektiv.mes.basic.hooks;

import com.sleektiv.mes.basic.constants.UserFieldsB;
import com.sleektiv.security.api.SecurityService;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.stream.Stream;

@Service
public class ProfileHooksB {

    @Autowired
    private SecurityService securityService;

    public void setFieldsEnabledForAdmin(final ViewDefinitionState view) {
        if (securityService.hasCurrentUserRole("ROLE_ADMIN")) {
            Stream.of(UserFieldsB.SHOW_ONLY_MY_OPERATIONAL_TASKS_AND_ORDERS).forEach(fieldName -> {
                FieldComponent fieldComponent = (FieldComponent) view.getComponentByReference(fieldName);

                if (Objects.nonNull(fieldComponent)) {
                    fieldComponent.setEnabled(true);
                    fieldComponent.requestComponentUpdateState();
                }
            });
        }
    }

}
