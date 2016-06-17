/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.embeddedcdt.dsf;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.service.AbstractDsfService;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.tm.internal.terminal.control.ITerminalViewControl;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalConnector;
import org.eclipse.tm.terminal.view.core.TerminalServiceFactory;
import org.eclipse.tm.terminal.view.core.interfaces.ITerminalService;
import org.eclipse.tm.terminal.view.core.interfaces.constants.ITerminalsConnectorConstants;
import org.eclipse.tm.terminal.view.ui.interfaces.ILauncherDelegate;
import org.eclipse.tm.terminal.view.ui.launcher.LauncherDelegateManager;
import org.eclipse.tm.terminal.view.ui.manager.ConsoleManager;
import org.eclipse.tm.terminal.view.ui.tabs.TabFolderManager;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;

import com.arc.embeddedcdt.LaunchPlugin;
import com.arc.embeddedcdt.dsf.utils.Configuration;

/**
 * DSF service for connecting to the UART on the board and opening Terminal view in Eclipse.
 */
public class TerminalService extends AbstractDsfService {

    private static final String viewId = "org.eclipse.tm.terminal.view.ui.TerminalsView";
    private static final String delegateId = "org.eclipse.tm.terminal.connector.serial.launcher.serial";

    private Map<String, Object> properties;
    {
        properties = new HashMap<>();
        properties.put(ITerminalsConnectorConstants.PROP_SERIAL_BAUD_RATE, "115200");
        properties.put(ITerminalsConnectorConstants.PROP_TIMEOUT, "5");
        properties.put(ITerminalsConnectorConstants.PROP_SERIAL_DATA_BITS, "8");
        properties.put(ITerminalsConnectorConstants.PROP_SERIAL_STOP_BITS, "1");
        properties.put(ITerminalsConnectorConstants.PROP_SERIAL_PARITY, "N");
        properties.put(ITerminalsConnectorConstants.PROP_SERIAL_FLOW_CONTROL, "XON/XOFF");
        properties.put(ITerminalsConnectorConstants.PROP_DELEGATE_ID, delegateId);
    }
    private ITerminalService terminal = TerminalServiceFactory.getService();
    private ILauncherDelegate delegate = LauncherDelegateManager.getInstance()
            .getLauncherDelegate(delegateId, false);
    private ITerminalConnector connector;

    public TerminalService(DsfSession session) {
        super(session);
        ILaunch launch = (ILaunch) session.getModelAdapter(ILaunch.class);
        String serialPort = Configuration.getComPort(launch.getLaunchConfiguration());
        properties.put(ITerminalsConnectorConstants.PROP_SERIAL_DEVICE, serialPort);
        connector = delegate.createTerminalConnector(properties);
    }

    @Override
    public void initialize(final RequestMonitor requestMonitor) {
        super.initialize(new RequestMonitor(getExecutor(), requestMonitor) {
            @Override
            public void handleSuccess() {
                doInitialize(requestMonitor);
            }
        });
    }

    private void doInitialize(RequestMonitor requestMonitor) {
        register(new String[] { TerminalService.class.getName() }, new Hashtable<String, String>());
        startTerminal();
        requestMonitor.done();
    }

    @Override
    public void shutdown(RequestMonitor requestMonitor) {
        if (terminal != null) {
            terminal.terminateConsole(properties, null);
        }
        unregister();
        super.shutdown(requestMonitor);
    }

    /**
     * Opens the Terminal View and creates a connection using properties.
     */
    public void startTerminal() {
        try {
            Display display = PlatformUI.getWorkbench().getDisplay();
            display.asyncExec(new Runnable() {
                @Override
                public void run() {
                    String title = getTitleOfOpenConsole();
                    if (title != null) {
                        properties.put(ITerminalsConnectorConstants.PROP_TITLE, title);
                    }
                    delegate.execute(properties, null);
                }
            });
        } catch (Exception e) {
        }
    }

    /**
     * Searches for open console with properties equal to ours. If this console exists, return its
     * title.
     * 
     * This is needed because in TM Terminal there is no way to connect to open console knowing all
     * its properties, but not title. On the other hand, when Eclipse is closed, all the connection
     * states are saved, and all the properties of the open consoles, except for names. On Eclipse
     * launching all these saved connections are reopened, but with different console names (because
     * these names include terminal consoles' opening times). In order not to open new console with
     * the same properties (which would be immediately closed because the serial port is already
     * taken), but connect to existing one, we need to find out its name.
     * 
     * Must be run from the UI thread.
     * 
     * @return title of open console with properties equal to ours, or null if is does not exist
     */
    private String getTitleOfOpenConsole() {
        IViewPart view = ConsoleManager.getInstance().showConsoleView(viewId, null);
        TabFolderManager manager = view.getAdapter(TabFolderManager.class);
        manager.removeTerminatedItems();

        CTabFolder tabFolder = view.getAdapter(CTabFolder.class);
        if (tabFolder == null) {
            return null;
        }
        for (CTabItem item : tabFolder.getItems()) {
            // Disposed items cannot be matched
            if (item.isDisposed()) {
                continue;
            }
            ITerminalViewControl terminal = (ITerminalViewControl) item.getData();
            ITerminalConnector connector2 = terminal.getTerminalConnector();
            if (connector.getName().equals(connector2.getName())) {
                if (!connector.isInitialized()) {
                    return item.getText();
                }
                String summary = connector.getSettingsSummary();
                String summary2 = connector2.getSettingsSummary();
                if (summary.equals(summary2)) {
                    return item.getText();
                }
            }
        }
        return null;
    }

    @Override
    protected BundleContext getBundleContext() {
        return LaunchPlugin.getBundleContext();
    }

}
