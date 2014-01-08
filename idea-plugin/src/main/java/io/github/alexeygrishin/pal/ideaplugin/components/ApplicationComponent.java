package io.github.alexeygrishin.pal.ideaplugin.components;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import io.github.alexeygrishin.pal.ideaplugin.model.PalService;
import io.github.alexeygrishin.pal.ideaplugin.ui.Notifier;
import org.jetbrains.annotations.NotNull;

public class ApplicationComponent implements com.intellij.openapi.components.ApplicationComponent {
    @Override
    public void initComponent() {
        Notifications.Bus.register("pal_group", NotificationDisplayType.BALLOON);
        Notifier notifier = new Notifier("pal_group");
        ApplicationManager.getApplication().putUserData(Notifier.PAL_NOTIFIER, notifier);
        PalService.addServerListener(notifier);
        PalService.addPalClassesListener(notifier);
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "pal app";
    }
}
