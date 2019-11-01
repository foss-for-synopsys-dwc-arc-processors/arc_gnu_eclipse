// SPDX-License-Identifier: EPL-2.0

package com.synopsys.arc.gnu.elf.version;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;

/**
 * This class is intended for tracking the projects' changes and notifying StateChecker.
 */
public final class StateListener implements IResourceChangeListener
{
    private static StateListener INSTANCE = null;

    /**
     * Return the only instance of this class.
     */
    public static synchronized StateListener getInstance()
    {
        if (INSTANCE == null) {
            INSTANCE = new StateListener();
        }
        return INSTANCE;
    }

    private final StateChecker checker = StateChecker.getInstance();

    private StateListener()
    {
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event)
    {
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
