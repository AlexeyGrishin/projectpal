package io.github.alexeygrishin.pal.ideaplugin.ui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.util.Key;
import io.github.alexeygrishin.pal.ideaplugin.components.ApplicationComponent;
import io.github.alexeygrishin.pal.ideaplugin.model.PalClass;
import io.github.alexeygrishin.pal.ideaplugin.model.PalClassListener;
import io.github.alexeygrishin.pal.ideaplugin.remote.PalServerListener;
import org.w3c.dom.UserDataHandler;


public class Notifier implements PalServerListener, PalClassListener {
    private final String groupName;

    public static final Key<Notifier> PAL_NOTIFIER = Key.create("pal.notifier");

    //have holder for the future - for now it is 'singletone'
    public static Notifier getInstance(UserDataHandler holder) {
        return ApplicationManager.getApplication().getUserData(PAL_NOTIFIER);
    }

    public Notifier(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public void onConnectionStart() {

    }

    @Override
    public void onConnectionEnd() {

    }

    @Override
    public void onConnectionFail(String error) {
        Notifications.Bus.notify(new Notification(groupName, "No connection to Pal server", "Did you forget to start it?<br><br>Error details: <pre>" + error + "</pre>", NotificationType.ERROR));
    }

    @Override
    public void onConnectionRestore() {
        Notifications.Bus.notify(new Notification(groupName, "Connection to Pal server have been restored", "You may include pal functions now", NotificationType.INFORMATION));
    }

    @Override
    public void onPalClassCreation(PalClass source) {
        Notifications.Bus.notify(new Notification(groupName, "Pal class has been created", "<b>" + source.getPath() + "</b>", NotificationType.INFORMATION));
    }

    @Override
    public void onPalClassChange(PalClass source) {

    }
}
