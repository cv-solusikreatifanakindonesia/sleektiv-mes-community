package com.sleektiv.mes.basic.logging.aspects;

import com.beust.jcommander.internal.Lists;
import com.sleektiv.model.api.Entity;
import com.sleektiv.security.constants.UserFields;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Aspect
public class PasswordChangeLoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Pointcut("execution(void com.sleektiv.plugins.users.listeners.UserChangePasswordListeners.changePassword(..)) && args(view, state, args)")
    public void changePasswordExecution(final ViewDefinitionState view, final ComponentState state, final String[] args) {
    }

    @AfterReturning(pointcut = "changePasswordExecution(view, state, args)")
    public void afterReturningChangePasswordExecution(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        if (logger.isInfoEnabled()) {
            FormComponent userForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

            Entity user = userForm.getPersistedEntityWithIncludedFormValues();

            List<String> messageStatements = Lists.newArrayList();

            messageStatements.add("ATTEMPTED PASSWORD CHANGE");
            messageStatements.add("FOR USER:" + user.getStringField(UserFields.USER_NAME));
            messageStatements.add("STATUS:" + (user.isValid() ? "SUCCESS" : "FAILURE"));
            messageStatements.add("AUTHENTICATION:" + SecurityContextHolder.getContext().getAuthentication());

            logger.info(String.join(" ", messageStatements));
        }
    }

}
