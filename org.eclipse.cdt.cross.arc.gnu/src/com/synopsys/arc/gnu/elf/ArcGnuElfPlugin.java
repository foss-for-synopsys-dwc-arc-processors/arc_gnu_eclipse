// SPDX-License-Identifier: EPL-2.0

package com.synopsys.arc.gnu.elf;

import java.util.Optional;

import org.eclipse.cdt.cross.arc.gnu.common.StateListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.BundleContext;

public final class ArcGnuElfPlugin extends Plugin
{
    public static final String PLUGIN_ID = ArcGnuElfPlugin.class.getPackageName();

    private static ArcGnuElfPlugin plugin;

    public static ArcGnuElfPlugin getDefault()
    {
        return plugin;
    }

    /**
     * Expand variable string without throwing an exception - in case of error it will be logged and
     * original string will be returned.
     */
    public static String safeVariableExpansion(String expression)
    {
        IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
        try {
            return manager.performStringSubstitution(expression);
        } catch (CoreException e) {
            var status = new Status(IStatus.WARNING, PLUGIN_ID, e.getLocalizedMessage(), e);
            StatusManager.getManager().handle(status, StatusManager.SHOW);
            return expression;
        }
    }

    /**
     * Expand variable string without throwing an exception - in case of error it will be logged and
     * original string will be returned.
     */
    public static Optional<String> variableExpansion(String expression)
    {
        if (expression == null) {
            return Optional.empty();
        }
        try {
            var manager = VariablesPlugin.getDefault().getStringVariableManager();
            return Optional.of(manager.performStringSubstitution(expression));
        } catch (CoreException e) {
            var status = new Status(IStatus.WARNING, PLUGIN_ID, e.getLocalizedMessage(), e);
            StatusManager.getManager().handle(status, StatusManager.SHOW);
            return Optional.empty();
        }
    }

    public void log(String message)
    {
        var status = new Status(IStatus.INFO, PLUGIN_ID, message);
        StatusManager.getManager().handle(status, StatusManager.LOG);
    }

    public void logError(String message, Exception err)
    {
        var status = new Status(IStatus.ERROR, PLUGIN_ID, message, err);
        StatusManager.getManager().handle(status, StatusManager.LOG);
    }

    public void showError(String message)
    {
        var status = new Status(IStatus.ERROR, PLUGIN_ID, message);
        StatusManager.getManager().handle(status, StatusManager.SHOW);
    }

    public void showError(String message, Exception err)
    {
        var status = new Status(IStatus.ERROR, PLUGIN_ID, message, err);
        StatusManager.getManager().handle(status, StatusManager.SHOW);
    }

    /* (non-Javadoc)
     *
     * @see org.eclipse.ui.plugin.Plugin#start(org.osgi.framework.BundleContext) */
    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        plugin = this;
        ResourcesPlugin.getWorkspace().addResourceChangeListener(StateListener.getInstance());
    }

    /* (non-Javadoc)
     *
     * @see org.eclipse.ui.plugin.Plugin#stop(org.osgi.framework.BundleContext) */
    @Override
    public void stop(BundleContext context) throws Exception
    {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(StateListener.getInstance());
        plugin = null;
        super.stop(context);
    }
}
