package com.sleektiv.mes.cmmsMachineParts.notification;

import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.mes.cmmsMachineParts.MaintenanceEventService;
import com.sleektiv.security.api.SecurityService;
import com.sleektiv.view.api.notifications.Notification;
import com.sleektiv.view.api.notifications.NotificationDataComponent;
import com.sleektiv.view.api.notifications.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MaintenanceEnentsNotification implements NotificationDataComponent {

    @Autowired
    private MaintenanceEventService maintenanceEventService;

    @Autowired
    private TranslationService translationService;

    @Autowired
    private SecurityService securityService;

    @Override
    public Optional<Notification> registerNotification() {
        if (securityService.hasCurrentUserRole("ROLE_EVENTS_NOTIFICATION")
                && maintenanceEventService.existsNewEventsToNotification(securityService.getCurrentUserId())) {
            Notification notification = new Notification(NotificationType.information, translationService.translate(
                    "cmmsMachineParts.maintenanceEvent.notification.newEventNotification", LocaleContextHolder.getLocale()),
                    true, true);
            return Optional.ofNullable(notification);
        }
        return Optional.empty();
    }
}
