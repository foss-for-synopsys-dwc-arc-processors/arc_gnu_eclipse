/*******************************************************************************
 * This program and the accompanying materials are made available under the terms of the Common
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Copyright (c) 2016 Synopsys, Inc.
 *******************************************************************************/

package org.eclipse.cdt.cross.arc.gnu.common;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;

/**
 * This class is intended for tracking the projects' changes and notifying StateChecker.
 */
public class StateListener implements IResourceChangeListener {

  private static StateListener INSTANCE = null;

  private StateChecker checker = StateChecker.getInstance();
  public synchronized static StateListener getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new StateListener();
    }
    return INSTANCE;
  }

  private StateListener() {
  }

  @Override
  public void resourceChanged(IResourceChangeEvent event) {
    final IResourceDelta delta = event.getDelta();
    if (delta != null && delta.getKind() == IResourceDelta.CHANGED) {
      IResourceDelta[] addedResources = delta.getAffectedChildren(IResourceDelta.ADDED);
      IResourceDelta[] changedResources = delta.getAffectedChildren(IResourceDelta.CHANGED);
      for (IResourceDelta resourceDelta : changedResources) {
        final IResource resource = resourceDelta.getResource();
        if (resource instanceof IProject) {
          final IProject project = (IProject) resource;
          checker.checkPluginVersions(project, false);
        }
      }
      for (IResourceDelta resourceDelta : addedResources) {
        final IResource resource = resourceDelta.getResource();
        if (resource instanceof IProject) {
          final IProject project = (IProject) resource;
          checker.checkPluginVersions(project, true);
        }
      }
    }
  }

}
