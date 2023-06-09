import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.ex.WindowManagerEx;
import com.intellij.openapi.wm.impl.IdeFrameImpl;
import com.intellij.ui.content.Content;
import com.intellij.util.PlatformUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpringBootAppContextPortPlugin extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            WindowManagerEx windowManagerEx = (WindowManagerEx) WindowManager.getInstance();
            IdeFrameImpl frame = (IdeFrameImpl) windowManagerEx.getFrame(project);
            Content content = frame.getSelectedContent();
            if (content != null) {
                String contentName = content.getTabName();
                Pattern pattern = Pattern.compile(".*\\[([\\w-]+):(\\d+)\\].*");
                Matcher matcher = pattern.matcher(contentName);
                if (matcher.matches()) {
                    String context = matcher.group(1);
                    String port = matcher.group(2);
                    System.out.println("Contexto: " + context);
                    System.out.println("Porta: " + port);
                }
            }
        }
    }

    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            boolean isSpringBootApp = PlatformUtils.isIntelliJ() && isSpringBootApp(project);
            e.getPresentation().setEnabledAndVisible(isSpringBootApp);
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }

    private boolean isSpringBootApp(Project project) {
        return ApplicationManager.getApplication().isReadAccessAllowed()
                && project.getBasePath() != null
                && project.getBasePath().contains("src/main/resources/META-INF/spring-boot");
    }
}
