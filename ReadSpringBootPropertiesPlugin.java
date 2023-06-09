import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.Properties;

public class ReadSpringBootPropertiesPlugin extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            PsiManager psiManager = PsiManager.getInstance(project);
            PsiFile[] psiFiles = FilenameIndex.getFilesByName(project, "application.properties",
                    GlobalSearchScope.projectScope(project));
            if (psiFiles.length > 0) {
                PsiFile psiFile = psiFiles[0];
                VirtualFile virtualFile = psiFile.getVirtualFile();
                if (virtualFile != null && virtualFile.exists()) {
                    FileDocumentManager.getInstance().saveAllDocuments();

                    Properties properties = new Properties();
                    try {
                        properties.load(virtualFile.getInputStream());

                        String serverPort = properties.getProperty("server.port");
                        String contextPath = properties.getProperty("server.servlet.context-path");

                        if (serverPort != null) {
                            System.out.println("Valor de server.port: " + serverPort);
                        }

                        if (contextPath != null) {
                            System.out.println("Valor de server.servlet.context-path: " + contextPath);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            boolean isSpringBootApp = isSpringBootApp(project);
            e.getPresentation().setEnabledAndVisible(isSpringBootApp);
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }

    private boolean isSpringBootApp(Project project) {
        return project.getBasePath() != null
                && project.getBasePath().contains("src/main/resources")
                && FilenameIndex.getFilesByName(project, "application.properties",
                GlobalSearchScope.projectScope(project)).length > 0;
    }
}
