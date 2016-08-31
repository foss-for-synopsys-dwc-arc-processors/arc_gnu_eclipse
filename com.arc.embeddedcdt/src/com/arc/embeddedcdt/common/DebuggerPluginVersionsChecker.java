package com.arc.embeddedcdt.common;

import java.util.HashSet;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.arc.embeddedcdt.LaunchConfigurationConstants;
import com.arc.embeddedcdt.dsf.utils.Configuration;

public class DebuggerPluginVersionsChecker {

    private HashSet<String> seenLaunchConfigurationNames = new HashSet<>();

    private static DebuggerPluginVersionsChecker INSTANCE = null;

    private ILaunchConfiguration launchConfiguration;

    private DebuggerPluginVersionsChecker() {
    }

    public static DebuggerPluginVersionsChecker getDebuggerPluginVersionsChecker() {
        if (INSTANCE == null) {
            INSTANCE = new DebuggerPluginVersionsChecker();
        }
        return INSTANCE;
    }

    public void checkPluginVersion(ILaunchConfiguration launchConfiguration) {
        String launchConfigurationName = Configuration.getProgramName(launchConfiguration);
        if (!seenLaunchConfigurationNames.contains(launchConfigurationName)) {
            seenLaunchConfigurationNames.add(launchConfigurationName);
            final int currentLaunchConfigurationDebuggerPluginVersion = Configuration
                    .getDebuggerPluginVersion(launchConfiguration);
            if (currentLaunchConfigurationDebuggerPluginVersion != LaunchConfigurationConstants.UNREAL_DEBUGGER_PLUGIN_VERSION_NUMBER
                    && currentLaunchConfigurationDebuggerPluginVersion != LaunchConfigurationConstants.DEBUGGER_PLUGIN_VERSION_NUMBER) {
                // Otherwise, a debugger plug-in version was not written to configuration' .launch
                // file.
                showWarningWindow(String.format(
                        "Your debugger plug-in version is %d, but the launch configuration was created with the"
                                + " plug-in version %d.\n",
                        LaunchConfigurationConstants.DEBUGGER_PLUGIN_VERSION_NUMBER,
                        currentLaunchConfigurationDebuggerPluginVersion));
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
