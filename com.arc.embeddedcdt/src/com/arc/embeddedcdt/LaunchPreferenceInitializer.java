package com.arc.embeddedcdt;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class LaunchPreferenceInitializer extends AbstractPreferenceInitializer {

    public LaunchPreferenceInitializer() {
        super();
    }

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore prefs = LaunchPlugin.getDefault().getPreferenceStore();

        prefs.setDefault(ILaunchPreferences.SERVER_STARTUP_DELAY,
                ILaunchPreferences.DEFAULT_SERVER_STARTUP_DELAY);
        prefs.setDefault(ILaunchPreferences.SERVER_USE_ADAPTIVE_DELAY,
                ILaunchPreferences.DEFAULT_SERVER_USE_ADAPTIVE_DELAY);
        prefs.setDefault(ILaunchPreferences.SERVER_STARTUP_TIMEOUT,
                ILaunchPreferences.DEFAULT_SERVER_STARTUP_TIMEOUT);
        prefs.setDefault(ILaunchPreferences.SERVER_STARTUP_TIMEOUT_STEP,
                ILaunchPreferences.DEFAULT_SERVER_STARTUP_TIMEOUT_STEP);
    }
}
