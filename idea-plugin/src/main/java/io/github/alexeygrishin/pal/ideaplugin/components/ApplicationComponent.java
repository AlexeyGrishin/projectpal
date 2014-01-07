package io.github.alexeygrishin.pal.ideaplugin.components;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.Notifications;
import org.jetbrains.annotations.NotNull;

public class ApplicationComponent implements com.intellij.openapi.components.ApplicationComponent {
    @Override
    public void initComponent() {
        Notifications.Bus.register("pal_group", NotificationDisplayType.BALLOON);
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
