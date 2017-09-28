package com.arc.embeddedcdt;

/**
 * Various constants to work with preferences for this plugin.
 */
public interface ILaunchPreferences {
    /**
     * Delay in milliseconds that this plugin wait for after starting gdbserver and before starting
     * the GDB, thus allowing server to start listening on TCP port.
     */
    public static final String SERVER_STARTUP_DELAY = "gdbserver_startup_delay";
    public static final int DEFAULT_SERVER_STARTUP_DELAY = 500;

    /**
     * Whether to try to use adaptive server startup delay or use only default fixed delay time.
     */
    public static final String SERVER_USE_ADAPTIVE_DELAY = "gdbserver_use_adaptive_delay";
    public static final boolean DEFAULT_SERVER_USE_ADAPTIVE_DELAY = true;

    /**
     * Amount of time in milliseconds given to gdbserver to start in adaptive startup procedure.
     */
    public static final String SERVER_STARTUP_TIMEOUT = "gdbserver_startup_timeout";
    public static final int DEFAULT_SERVER_STARTUP_TIMEOUT = 30000;

    /**
     * Amount of time to sleep in milliseconds after adaptive gdbserver startup delay procedure
     * failed to connect to the server. In practice this can be very small, because Socket.connect()
     * itself waits for 1 second. However I've measured this value on my machine, so I'm not sure it
     * is equally valid everywhere, so I leave this is a possible parameter to modify if needed.
     */
    public static final String SERVER_STARTUP_TIMEOUT_STEP = "gdbserver_startup_delay_step";
    public static final int DEFAULT_SERVER_STARTUP_TIMEOUT_STEP = 1;

    /**
     * Whether to start nSIM with option -reconnect. This is required for adaptive delay to work with nSIM.
     */
    public static final String NSIM_PASS_RECONNECT_OPTION = "nsim_pass_reconnect_option";
    public static final boolean DEFAULT_NSIM_PASS_RECONNECT_OPTION = true;
}
