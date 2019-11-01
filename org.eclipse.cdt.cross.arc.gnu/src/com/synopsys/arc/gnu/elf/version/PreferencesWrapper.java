// SPDX-License-Identifier: EPL-2.0

package com.synopsys.arc.gnu.elf.version;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.service.prefs.BackingStoreException;

/**
 * This class provides an access for both reading and writing to the preferences file.
 */
public class PreferencesWrapper {

  public static String get(final String nodeNamePrefix, final IProject project, final String name,
      final String preference){
    final ProjectScope projectScope = new ProjectScope(project);
    final IEclipsePreferences prefs = projectScope.getNode(nodeNamePrefix);
    return prefs.get(name, "");
  }

  /**
   * The <tt>CoreException</tt> occurs if could not flush info to the preferences file, it is
   * reported to the Eclipse's Error view and error log.
   */
  public static void set(final String nodeNamePrefix, final IProject project,
      final String name, final String preference){
    final ProjectScope projectScope = new ProjectScope(project);
    final IEclipsePreferences prefs =
        projectScope.getNode(nodeNamePrefix);
    prefs.put(name, preference);
    final WorkspaceJob job = new WorkspaceJob("Write " + nodeNamePrefix + " for the " +
    project.getName()) {

      @Override
      public IStatus runInWorkspace(IProgressMonitor monitor) {
        try {
          prefs.flush();
        } catch (BackingStoreException e) {
          CoreException coreException =
              new CoreException(new Status(Status.ERROR, "com.arc.embedded.cdt", e.getMessage()));
          StatusManager.getManager().handle(coreException, "com.arc.embedded.cdt");
        }
        return Status.OK_STATUS;
      }
    };

    job.setRule(project);
    job.schedule();
  }

}