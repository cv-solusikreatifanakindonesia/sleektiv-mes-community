package com.sleektiv.mes.materialFlowResources.notification;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.mes.materialFlowResources.service.DraftDocumentsNotificationService;
import com.sleektiv.view.api.notifications.Notification;
import com.sleektiv.view.api.notifications.NotificationDataComponent;
import com.sleektiv.view.api.notifications.NotificationType;

@Component
public class DraftDocumentsNotification implements NotificationDataComponent {

    @Autowired
    private TranslationService translationService;

    @Autowired
    private DraftDocumentsNotificationService draftDocumentsNotificationService;

    @Override
    public Optional<Notification> registerNotification() {
        if (draftDocumentsNotificationService.shouldNotifyCurrentUser()) {
            String code = "materialFlowResources.notification.document.draftNotification";
            return Optional.of(new Notification(NotificationType.information,
                    translationService.translate(code, LocaleContextHolder.getLocale()), true, true));
        }
        return Optional.empty();
    }

}
