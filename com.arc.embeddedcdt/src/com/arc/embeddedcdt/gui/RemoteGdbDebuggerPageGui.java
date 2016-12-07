package com.arc.embeddedcdt.gui;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.arc.embeddedcdt.LaunchConfigurationConstants;
import com.arc.embeddedcdt.common.ArcGdbServer;
import com.arc.embeddedcdt.common.FtdiCore;
import com.arc.embeddedcdt.common.FtdiDevice;

public class RemoteGdbDebuggerPageGui {

  public Combo externalToolsCombo;
  public ArcGdbServer gdbServer;
  
  public Combo ftdiDeviceCombo;
  public Combo ftdiCoreCombo;
  public Text targetText;
  public Text gdbServerPortNumberText;
  public Text gdbServerIpAddressText;
  public Button searchExternalToolsPathButton;
  public Label searchExternalToolsLabel;
  public Text externalToolsPathText;
  public FileFieldEditor openOcdBinPathEditor;
  public FileFieldEditor openOcdConfigurationPathEditor;
  public String openOcdBinaryPath;
  public String openOcdConfigurationPath;
  public FtdiDevice ftdiDevice = LaunchConfigurationConstants.DEFAULT_FTDI_DEVICE;
  public FtdiCore ftdiCore = LaunchConfigurationConstants.DEFAULT_FTDI_CORE;
  public Button nsimPropertiesBrowseButton;
  public Button launchTcf;
  public Button launchTcfPropertiesButton;
  public Button launchNsimJitProperties;
  public Button launchHostLinkProperties;
  public Button launchMemoryExceptionProperties;
  public Button launchInvalidInstructionExceptionProperties;
  public Button launchEnableExceptionProperties;
  public Button nsimTcfBrowseButton;

  public Spinner jitThreadSpinner;
}
