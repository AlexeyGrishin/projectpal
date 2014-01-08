package io.github.alexeygrishin.pal.ideaplugin.components;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import io.github.alexeygrishin.pal.ideaplugin.model.PalService;
import io.github.alexeygrishin.pal.ideaplugin.model.UserFileManipulator;
import io.github.alexeygrishin.pal.ideaplugin.model.lang.BaseJava;
import io.github.alexeygrishin.pal.ideaplugin.model.lang.BaseRuby;
import org.jetbrains.annotations.NotNull;

public class PalServiceComponent implements ProjectComponent {

    private Project project;

    public PalServiceComponent(Project project) throws Exception {
        this.project = project;
        PalService service = new PalService(project);
        UserFileManipulator userFileManipulator = new UserFileManipulator();
        project.putUserData(UserFileManipulator.USER_FILE, userFileManipulator);
        project.putUserData(PalService.PAL_SERVICE, service);
        service.registerLangAndPlatform(BaseJava.class);
        service.registerLangAndPlatform(BaseRuby.class);

    }

    @Override
    public void projectOpened() {

    }

    @Override
    public void projectClosed() {

    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "pal";
    }
}
