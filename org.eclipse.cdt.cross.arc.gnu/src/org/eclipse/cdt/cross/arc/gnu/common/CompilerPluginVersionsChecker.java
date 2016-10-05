package org.eclipse.cdt.cross.arc.gnu.common;

import java.util.HashSet;

import org.eclipse.cdt.make.core.scannerconfig.IScannerInfoCollector;
import org.eclipse.cdt.make.core.scannerconfig.InfoContext;
import org.eclipse.cdt.make.internal.core.scannerconfig2.PerProjectSICollector;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.prefs.BackingStoreException;

public class CompilerPluginVersionsChecker {

    private final String FILENAME_WITH_PROJECT_SCOPED_PREFERENCES = "com.arc.cdt";
    private final String UCLIBC_PLUGIN_VERSION = "arc.uclibc_plugin.version";
    private final String ELF_PLUGIN_VERSION = "arc.elf32_plugin.version";

    // These version numbers should be incremented when incompatible changes appear in these
    // plug-ins.
    private final int UCLIBC_PLUGIN_VERSION_NUMBER = 1;
    private final int ELF_PLUGIN_VERSION_NUMBER = 1;

    private final int UNREAL_VERSION_NUMBER = -1;

    private HashSet<IProject> seenProjects = new HashSet<>();

    private static CompilerPluginVersionsChecker INSTANCE = null;

    private CompilerPluginVersionsChecker() {
    }

    public static CompilerPluginVersionsChecker getCompilerPluginVersionsChecker() {
        if (INSTANCE == null) {
            INSTANCE = new CompilerPluginVersionsChecker();
        }
        return INSTANCE;
    }

    void checkPluginVersions(IScannerInfoCollector collector) {
        // Write plug-in versions to .settings/com.arc.cdt file if they are not
        // present, check them otherwise and show warning if there is a
        // mismatch.
        InfoContext oC;
        if ((collector instanceof PerProjectSICollector)) {
            oC = ((PerProjectSICollector) collector).getContext();
            IProject oProject = oC.getProject();
            if (seenProjects.contains(oProject)) {
                return;
            }
            ProjectScope projectScope = new ProjectScope(oProject);
            IEclipsePreferences pref = projectScope
                    .getNode(FILENAME_WITH_PROJECT_SCOPED_PREFERENCES);
            int projectElfPluginVersionNumber = pref.getInt(ELF_PLUGIN_VERSION,
                    UNREAL_VERSION_NUMBER);
            int projectUclibcPluginVersionNumber = pref.getInt(UCLIBC_PLUGIN_VERSION,
                    UNREAL_VERSION_NUMBER);
            String warningMsg = "";
            String warningMsgPattern = "Your %s plug-in version is %d, but the project %s was created with the"
                    + " plug-in version %d.\n";
            if (projectElfPluginVersionNumber != UNREAL_VERSION_NUMBER) {
                if (projectElfPluginVersionNumber != ELF_PLUGIN_VERSION_NUMBER) {
                    warningMsg += String.format(warningMsgPattern, "elf", ELF_PLUGIN_VERSION_NUMBER,
                            oProject.getName(), projectElfPluginVersionNumber);
                }
                if (projectUclibcPluginVersionNumber != UCLIBC_PLUGIN_VERSION_NUMBER) {
                    warningMsg += String.format(warningMsgPattern, "uclibc",
                            UCLIBC_PLUGIN_VERSION_NUMBER, oProject.getName(),
                            projectUclibcPluginVersionNumber);
                }
            }
            if (!warningMsg.equals("")) {
                showWarningWindow(warningMsg);
            } else {
                pref.putInt(ELF_PLUGIN_VERSION, ELF_PLUGIN_VERSION_NUMBER);
                pref.putInt(UCLIBC_PLUGIN_VERSION, UCLIBC_PLUGIN_VERSION_NUMBER);
            }
            try {
                pref.flush();
                seenProjects.add(oProject);
            } catch (BackingStoreException e) {
                // If attempting to write to the existing project (when importing project) this
                // exception is also thrown.
                e.printStackTrace();
            }
        }
    }

    private void showWarningWindow(final String warningMsg) {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Warning",
                        "Compatibility issues are possible.\n" + warningMsg);
            }

        });
    }

}
