import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class AddSpringBootPropertiesPlugin extends AnAction {

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

                        // Adicionar novos atributos ou atualizar valores existentes
                        properties.setProperty("custom.attribute1", "value1");
                        properties.setProperty("custom.attribute2", "value2");

                        // Salvar as alterações no arquivo
                        FileOutputStream outputStream = new FileOutputStream(virtualFile.getPath());
                        properties.store(outputStream, null);
                        outputStream.close();

                        ApplicationManager.getApplication().invokeLater(() ->
                                virtualFile.refresh(false, false));
                    } catch (IOException ex) {
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
