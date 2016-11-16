/*******************************************************************************
 * This program and the accompanying materials are made available under the terms of the Common
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Copyright (c) 2016 Synopsys, Inc.
 *******************************************************************************/

package org.eclipse.cdt.cross.arc.gnu.common;

import org.eclipse.cdt.internal.core.WeakHashSet;
import org.eclipse.cdt.make.core.scannerconfig.IScannerInfoCollector;
import org.eclipse.cdt.make.core.scannerconfig.InfoContext;
import org.eclipse.cdt.make.internal.core.scannerconfig2.PerProjectSICollector;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * Class is intended for checking the compiler plug-in state number (it may be different for
 * the same plug-in version) and notifying user if his compiler plug-in has incompatible changes in
 * comparison with a compiler plug-in a project was created with. It makes user aware of possible
 * reason of his problems if they appear.
 *
 * When creating a new project it writes a compiler plug-in state number to the
 * $PROJECT/.settings/com.arc.cdt.prefs, warns user when importing, building, debugging (once per
 * project during a one IDE launch) a project created with an incompatible compiler plug-in state
 * number.
 */
public class StateChecker {

  public static final String PREFS_FILE_NAME_PREFIX = "com.arc.cdt";
  public static final String STATE_KEY = "com.arc.cdt_plugin.state";

  /* Version number should be incremented when incompatible changes appear in the compiler
     plug-ins. */
  public static final String CURRENT_STATE = "1";

  public static final String UNREAL_STATE = "";

  /* Set is used to identify the projects which are already handled. */
  @SuppressWarnings("restriction")
  private final WeakHashSet<IProject> seenProjects = new WeakHashSet<>();

  private static StateChecker INSTANCE = null;

  private StateChecker() {
  }

  public synchronized static StateChecker getInstance() {
    if (INSTANCE == null){
      INSTANCE = new StateChecker();
    }
    return INSTANCE;
  }

  /**
  * Show warning if versions are different (com.arc.cdt.prefs exists) or if there is no
  * com.arc.cdt.prefs file, but if the project is new, write to preferences file.
  */
  @SuppressWarnings("restriction")
  void checkPluginVersions(final IProject project, boolean isNew) {
      if (seenProjects.contains(project)) {
        return;
      }
      String projectState = PreferencesWrapper.get(PREFS_FILE_NAME_PREFIX, project, STATE_KEY, UNREAL_STATE);
      final IPath workspaceLocation = project.getWorkspace().getRoot().getLocation();
      final IPath projectLocation = project.getLocation().removeLastSegments(1);
      boolean isImported = !projectLocation.equals(workspaceLocation);
      if (!projectState.equals(CURRENT_STATE)
          && (!projectState.equals(UNREAL_STATE) || isImported)) {
        warnUser(project, CURRENT_STATE, projectState);
      } else if (projectState.equals(UNREAL_STATE)) {
           if (isNew){
             PreferencesWrapper.set(PREFS_FILE_NAME_PREFIX, project, STATE_KEY, CURRENT_STATE);
           }
           else{
             warnUser(project, CURRENT_STATE, UNREAL_STATE);
           }
      }
      seenProjects.add(project);
    }

  private void warnUser(final IProject project,final String currentState, String projectState) {
    final String warningMsg = String.format(
        "This project was created with an incompatible version of ARC plug-in."
        + " Supported version of a project file is %s, but it is %s.",
        currentState, 
        projectState.equals(UNREAL_STATE) ? "an older one" : projectState);

    /* Try to use eclipse.application property to figure out if this is GUI or console run and to
     * show user an error message in a proper way. Property may not be set, then it is unknown
     * whether the GUI mode or headless is launched, therefore show warning both by creating marker
     * and writing to the System.err. */
    final String applicationProperty = System.getProperty("eclipse.application");
    if (applicationProperty == null) {
      System.err.println(warningMsg);
      createMarker(project, warningMsg);
    } else {
      final boolean isHeadless =
          applicationProperty.equals("org.eclipse.cdt.managedbuilder.core.headlessbuild");
      if (isHeadless) {
        System.err.println(warningMsg);
      } else {
        createMarker(project, warningMsg);
      }
    }
  }

  /**
   * This method makes the warning appear in the Problems view.
   * If a project does not exist or is closed, a <tt>CoreException</tt> occurs and is reported
   * to the Eclipse's Error view and error log.
   */
  private void createMarker(final IProject project, final String warningMsg) {
    final WorkspaceJob job = new WorkspaceJob(
        "Create marker for an incompatible compiler plug-in version" + project.getName()) {

      @Override
      public IStatus runInWorkspace(IProgressMonitor monitor) {
        try {
          IMarker marker = project.createMarker(IMarker.PROBLEM);
          marker.setAttribute(IMarker.MESSAGE, warningMsg);
          marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
          marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
          marker.setAttribute(IMarker.LOCATION, project.getName());
          marker.setAttribute(IMarker.TRANSIENT, true);
        } catch (CoreException exception) {
          StatusManager.getManager().handle(exception, "com.arc.embeddedcdt");
        }
        return Status.OK_STATUS;
      }
    };
    job.setRule(project);
    job.schedule();
  }
}
