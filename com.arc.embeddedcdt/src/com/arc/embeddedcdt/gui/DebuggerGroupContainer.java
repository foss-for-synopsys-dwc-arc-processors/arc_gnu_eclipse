/*******************************************************************************
 * This program and the accompanying materials are made available under the terms of the Common
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Copyright (c) 2016 Synopsys, Inc.
 *******************************************************************************/

package com.arc.embeddedcdt.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Observable;

import org.eclipse.cdt.internal.launch.remote.Messages;
import org.eclipse.cdt.launch.remote.IRemoteConnectionConfigurationConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.Workbench;

import com.arc.embeddedcdt.LaunchConfigurationConstants;
import com.arc.embeddedcdt.common.ArcGdbServer;
import com.arc.embeddedcdt.common.FtdiCore;
import com.arc.embeddedcdt.common.FtdiDevice;
import com.arc.embeddedcdt.dsf.utils.ConfigurationReader;
import com.arc.embeddedcdt.dsf.utils.ConfigurationWriter;

/**
 * This class provides the content and control elements for the Gdbserver Settings tab in the
 * Debug Configurations' Debugger tab.
 */
@SuppressWarnings("restriction")
public class DebuggerGroupContainer extends Observable{

  public static final String DEFAULT_OOCD_BIN;
  public static final String DEFAULT_OOCD_CFG;
  private Combo jtagFrequencyCombo;
  private Combo ftdiDeviceCombo;
  private Combo ftdiCoreCombo;
  private Combo externalToolsCombo;
  private ArcGdbServer gdbServer = ArcGdbServer.DEFAULT_GDB_SERVER;
  private Spinner jitThreadSpinner;
  private FileFieldEditor ashlingXmlPathEditor;
  private FileFieldEditor ashlingTdescXmlPathEditor;
  private FileFieldEditor ashlingBinaryPathEditor;
  private FileFieldEditor customGdbBinaryPathEditor;
  private FileFieldEditor openOcdBinaryPathEditor;
  private FileFieldEditor openOcdConfigurationPathEditor;
  private FileFieldEditor nsimBinaryPathEditor;
  private FileFieldEditor nsimTcfPathEditor;
  private FileFieldEditor nsimPropertiesPathEditor;
  private Button loadElfButton;
  private FtdiDevice ftdiDevice = LaunchConfigurationConstants.DEFAULT_FTDI_DEVICE;
  private FtdiCore ftdiCore = LaunchConfigurationConstants.DEFAULT_FTDI_CORE;
  private Text gdbServerPortNumberText;
  private Text gdbServerIpAddressText;
  private Text customGdbCommandLineArgumentsText;
  private String customGdbCommandLineArguments = null;
  private Button launchEnableExceptionProperties;
  private Button launchInvalidInstructionExceptionProperties;
  private Button launchTcfProperties;
  private Button launchTcf;
  private Button launchNsimJitProperties;
  private Button launchMemoryExceptionProperties;
  private Button launchHostLinkProperties;
  private String jtagFrequency = null;
  private String hostName = "";
  private String portNumber = "";
  private String externalToolsNsimPath = "";
  private String jitThread = "1";
  private String nsimTcfFilesLast = "";
  private String nsimPropertiesFilesLast = "";
  private String ashlingTdescPath = "";
  private String ashlingXmlPath = "";
  private String externalToolsAshlingPath = "";
  private String customGdbPath;
  private String openOcdBinaryPath;
  private String openOcdConfigurationPath;
  private String gdbPath = null;
  private boolean launchExternalNsimInvalidInstructionException = true;
  private boolean externalNsimEnableExceptionToolsEnabled = true;
  private boolean externalNsimTcfToolsEnabled = true;
  private boolean externalNsimMemoryExceptionToolsEnabled = true;
  private boolean externalNsimHostLinkToolsEnabled = true;
  private boolean externalNsimJitEnabled = true;
  private boolean externalNsimPropertiesEnabled = true;
  private boolean createTabItemCom = false;
  private boolean createTabItemNsim = false;
  private boolean createTabItemGenericGdbServer = false;
  private boolean createTabItemComAshling = false;
  private boolean createTabItemCustomGdb = false;
  static Group groupComCustomGdb;
  static Group groupGenericGdbServer;
  static Group groupNsim;
  static Group groupComAshling;
  static Group groupCom;
  private ARCWorkingDirectoryBlock workingDirectoryBlockNsim = new ARCWorkingDirectoryBlock();

  public ARCWorkingDirectoryBlock getWorkingDirectoryBlockNsim(){
    return workingDirectoryBlockNsim;
  }

  public ArcGdbServer getGdbServer(){
    return gdbServer;
  }

  public Button getLaunchTcf(){
    return launchTcf;
  }

  public Button getLaunchTcfProperties(){
    return launchTcfProperties;
  }

  public FileFieldEditor getNsimPropertiesPathEditor(){
    return nsimPropertiesPathEditor;
  }

  public FileFieldEditor getNsimTcfPathEditor(){
    return nsimTcfPathEditor;
  }

  public String getGdbPath(){
    return gdbPath;
  }

  public void setGdbPath(String path){
    gdbPath = path;
  }

  public FileFieldEditor getNsimBinaryPathEditor(){
    return nsimBinaryPathEditor;
  }

  public FileFieldEditor getOpenOcdConfigurationPathEditor(){
    return openOcdConfigurationPathEditor;
  }

  public FileFieldEditor getOpenOcdBinaryPathEditor(){
    return openOcdBinaryPathEditor;
  }

  public FtdiDevice getFtdiDevice(){
    return ftdiDevice;
  }

  public String getOpenOcdConfigurationPath(){
    return openOcdConfigurationPath;
  }

  public FileFieldEditor getCustomGdbBinaryPathEditor(){
    return customGdbBinaryPathEditor;
  }

  public void setPortNumberText(String defaultText) {
    if (portNumber.isEmpty())
      gdbServerPortNumberText.setText(defaultText);
    else
      gdbServerPortNumberText.setText(portNumber);
  }

  public FileFieldEditor getAshlingTdescXmlPathEditor(){
    return ashlingTdescXmlPathEditor;
  }

  public FileFieldEditor getAshlingXmlPathEditor(){
    return ashlingXmlPathEditor;
  }

  public FileFieldEditor getAshlingBinaryPathEditor(){
    return ashlingBinaryPathEditor;
  }

  public static String getDefaultGdbPath() {
    String gdbPath = "arc-elf32-gdb";
    String predefinedPath = getIdeBinDir();
    File predefinedPathFile = new File(predefinedPath);

    if (predefinedPathFile.isDirectory()) {
        File gdbFile = new File(predefinedPath + "arc-elf32-gdb");
        if (gdbFile.canExecute()) {
            gdbPath = gdbFile.getAbsolutePath();
        }
    }
    return gdbPath;
  }

  public void initializeFrom(ILaunchConfiguration configuration){
    ConfigurationReader configurationReader = new ConfigurationReader(configuration);

    createTabItemCom = false;
    createTabItemComAshling = false;
    createTabItemNsim = false;
    createTabItemGenericGdbServer = false;
    createTabItemCustomGdb = false;

    gdbPath = configurationReader.getOrDefault(getDefaultGdbPath(), "",
        configurationReader.getGdbPath());
    openOcdBinaryPath = configurationReader.getOrDefault(
        DEFAULT_OOCD_BIN, "", configurationReader.getOpenOcdPath());
    jtagFrequency = configurationReader.getAshlingJtagFrequency();
    ftdiDevice = configurationReader.getFtdiDevice();
    ftdiCore = configurationReader.getFtdiCore();
    gdbServer = configurationReader.getGdbServer();
    openOcdConfigurationPath = configurationReader.getOrDefault(
        DEFAULT_OOCD_CFG, "", configurationReader.getOpenOcdConfig());
    String defaultAshlingPath =
        isWindowsOs() ? LaunchConfigurationConstants.ASHLING_DEFAULT_PATH_WINDOWS
            : LaunchConfigurationConstants.ASHLING_DEFAULT_PATH_LINUX;
    externalToolsAshlingPath = configurationReader.getOrDefault(defaultAshlingPath, "",
        configurationReader.getAshlingPath());
    String ashlingXmlFile = new File(defaultAshlingPath).getParentFile().getPath()
        + java.io.File.separator + LaunchConfigurationConstants.ASHLING_DEFAULT_XML_FILE;
    ashlingXmlPath = configurationReader.getOrDefault(ashlingXmlFile, "",
        configurationReader.getAshlingXmlPath());
    String defaultTDescPath = new File(defaultAshlingPath).getParentFile().getPath()
        + java.io.File.separator + LaunchConfigurationConstants.ASHLING_DEFAULT_TDESC_FILE;
    ashlingTdescPath = configurationReader.getOrDefault(defaultTDescPath, "",
        configurationReader.getAshlingTDescPath());
    externalToolsNsimPath = configurationReader.getOrDefault(
        getNsimdrvDefaultPath(), "", configurationReader.getNsimPath());
    customGdbPath = configurationReader.getCustomGdbServerPath();
    customGdbCommandLineArguments = configurationReader.getCustomGdbServerArgs();

    workingDirectoryBlockNsim.initializeFrom(configuration);
    externalNsimJitEnabled = configurationReader.getNsimUseJit();
    externalNsimHostLinkToolsEnabled = configurationReader.getNsimUseNsimHostlink();
    externalNsimEnableExceptionToolsEnabled = configurationReader.getNsimSimulateExceptions();
    launchExternalNsimInvalidInstructionException =
        configurationReader.getNsimSimulateInvalidInstructionExceptions();
    externalNsimMemoryExceptionToolsEnabled =
        configurationReader.getNsimSimulateMemoryExceptions();
    externalNsimPropertiesEnabled = configurationReader.getNsimUseProps();
    externalNsimTcfToolsEnabled = configurationReader.getNsimUseTcf();
    nsimPropertiesFilesLast = configurationReader.getNsimPropsPath();
    nsimTcfFilesLast = configurationReader.getNsimTcfPath();
    jitThread = configurationReader.getNsimJitThreads();

    this.loadElfButton.setSelection(configurationReader.getLoadElf());

    externalToolsCombo.setText(gdbServer.toString());

    if (!isJtagFrequencyComboDisposed() && !jtagFrequency.isEmpty())
      selectJtagFrequencyInCombo(jtagFrequency);
    setTextForJtagFrequencyCombo(configurationReader);
    if (!ftdiDeviceCombo.isDisposed())
      ftdiDeviceCombo.setText(ftdiDevice.toString());

    if (!ftdiCoreCombo.isDisposed())
      ftdiCoreCombo.setText(ftdiCore.toString());
    // Set host and IP.
    portNumber = configurationReader.getGdbServerPort();
    gdbServerPortNumberText.setText(portNumber);
    hostName = configurationReader.getHostAddress();
    if (groupGenericGdbServer != null && !groupGenericGdbServer.isDisposed())
      gdbServerIpAddressText.setText(hostName);

    int previous = externalToolsCombo.indexOf(gdbServer.toString());
    if (previous > -1)
        externalToolsCombo.remove(previous);
    /*
     * Reading gdbServer again from configuration because field gdbServer might have been
     * changed by event handler called by extTools.remove(previous)
     */
    gdbServer = configurationReader.getGdbServer();
    externalToolsCombo.add(gdbServer.toString(), 0);
    externalToolsCombo.select(0);

  }

  public void createTabItemComAshling(Composite subComp){
    createTabItemComAshling = true;

    groupComAshling = SWTFactory.createGroup(subComp, externalToolsCombo.getItem(0), 3, 5,
        GridData.FILL_HORIZONTAL);
    final Composite compositeCom = SWTFactory.createComposite(groupComAshling, 3, 5,
            GridData.FILL_BOTH);

    createAshlingBinaryPathEditor(compositeCom);
    createAshlingXmlPathEditor(compositeCom);
    createAshlingTdescXmlPathEditor(compositeCom);
    createJtagFrequencyCombo(compositeCom);
  }

  private void createAshlingXmlPathEditor(Composite compositeCom){
    // Path to Ashling XMl file
    ashlingXmlPathEditor = new FileFieldEditor("ashlingXmlPathEditor", "Ashling XML File", false,
            StringButtonFieldEditor.VALIDATE_ON_KEY_STROKE, compositeCom);
    ashlingXmlPathEditor.setStringValue(ashlingXmlPath);

    ashlingXmlPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getProperty() == "field_editor_value") {
                ashlingXmlPath = (String) event.getNewValue();
                sendNotification(null);
            }
        }
    });
  }

  private void sendNotification(Object argumentToNotifyMethod){
    setChanged();
    notifyObservers(argumentToNotifyMethod);
  }

  public boolean isValid(ILaunchConfiguration configuration) {
    if (gdbServer != null) {

      switch (gdbServer) {
        case JTAG_OPENOCD:
          if (groupCom.isDisposed()) {
            return true;
          }
          if (!isValidFileFieldEditor(openOcdBinaryPathEditor)) {
            return false;
          }
          if (ftdiDevice == FtdiDevice.CUSTOM) {
            if (!isValidFileFieldEditor(openOcdConfigurationPathEditor)) {
              return false;
            }
          } else {
            File configurationFile = new File(openOcdConfigurationPath);
            if (!configurationFile.exists()) {
              sendNotification("Default OpenOCD configuration file for this development system \'"
                  + openOcdConfigurationPathEditor + "\' must exist");
              return false;
            }
          }
          break;
        case JTAG_ASHLING:
          if (groupComAshling.isDisposed()) {
            return true;
          }
          if (!isValidFileFieldEditor(ashlingBinaryPathEditor)
              || !isValidFileFieldEditor(ashlingXmlPathEditor)
              || !isValidFileFieldEditor(ashlingTdescXmlPathEditor)) {
            return false;
          }
          break;
        case NSIM:
          if (groupNsim.isDisposed()) {
            return true;
          }
          if (!isValidFileFieldEditor(nsimBinaryPathEditor)
              || (launchTcf.getSelection()
                  && !isValidFileFieldEditor(nsimTcfPathEditor))
              || (launchTcfProperties.getSelection()
                  && !isValidFileFieldEditor(nsimPropertiesPathEditor))
              || !workingDirectoryBlockNsim.isValid(configuration)) {
            return false;
          }
          break;
        case CUSTOM_GDBSERVER:
          if (groupComCustomGdb.isDisposed()) {
            return true;
          }
          if (!isValidFileFieldEditor(customGdbBinaryPathEditor)) {
            return false;
          }
          break;
        case GENERIC_GDBSERVER:
          break;
        default:
          throw new IllegalArgumentException("Unknown enum value has been used");
      }
    }

    return true;
  }

  private boolean isValidFileFieldEditor(FileFieldEditor editor) {
    String validity = checkFileFieldEditorValidity(editor);
    if (validity == null)
      return true;
    else {
      sendNotification(validity);
      return false;
    }
  }

  /**
   *
   * @return Null if the fileFieldEditor is valid, otherwise error message.
   */
  private String checkFileFieldEditorValidity(FileFieldEditor editor) {
    if (editor != null) {
      String label = editor.getLabelText();
      if (editor.getStringValue().isEmpty()) {
        return label + "'s value cannot be empty";
      }
      if (!editor.isValid()) {
        return label + "'s value must be an existing file";
      }
    }
    return null;
  }

  private void addSelectionListenerForLaunchNsimJitProperties(final Label jitLabel) {
    launchNsimJitProperties.addSelectionListener(new SelectionListener() {
      public void widgetSelected(SelectionEvent event) {
        if (launchNsimJitProperties.getSelection() == true) {
          externalNsimJitEnabled = true;
          jitLabel.setEnabled(true);
          jitThreadSpinner.setEnabled(true);
        } else {
          externalNsimJitEnabled = false;
          jitLabel.setEnabled(false);
          jitThreadSpinner.setEnabled(false);
        }
        sendNotification(null);;
      }

      public void widgetDefaultSelected(SelectionEvent event) {}
    });
  }

  public void createJitThreadSpinner(Composite compositeNsim, GridData gridData){
    jitThreadSpinner = new Spinner(compositeNsim, SWT.NONE | SWT.BORDER);
    jitThreadSpinner.setToolTipText(
        "Specify number of threads to use in JIT simulation mode (-p nsim_fast-num-threads=N)");
    final Label jitLabel = new Label(compositeNsim, SWT.BEGINNING);
    jitLabel.setText("JIT threads");
    jitThreadSpinner.setValues(1, 1, 100, 10, 1, 0);

    addSelectionListenerForLaunchNsimJitProperties(jitLabel);
    if (externalNsimJitEnabled) {
        jitLabel.setEnabled(true);
        jitThreadSpinner.setEnabled(true);
    }
    else {
        jitLabel.setEnabled(false);
        jitThreadSpinner.setEnabled(false);
    }

    if (!jitThread.equals("1"))
        jitThreadSpinner.setSelection(Integer.parseInt(jitThread));
    else
        jitThreadSpinner.setSelection(1);

    jitThreadSpinner.addModifyListener(new ModifyListener() {
        public void modifyText(ModifyEvent event) {
            jitThread = jitThreadSpinner.getText();
            sendNotification(null);
        }
    });

    jitLabel.setLayoutData(createGridData(2));
  }

  public void createLaunchHostLinkProperties(Composite compositeNsim, GridData gridDataNsim) {
    launchHostLinkProperties = new Button(compositeNsim, SWT.CHECK); // $NON-NLS-1$ //6-3
    launchHostLinkProperties.setToolTipText(
        "Enable or disable nSIM GNU host I/O support (-p nsim_emt={0,1}). The nsim_emt property"
            + " works only if the application that is being simulated is compiled with the ARC GCC"
            + " compiler.");
    launchHostLinkProperties.setSelection(externalNsimHostLinkToolsEnabled);
    launchHostLinkProperties.setText("GNU host I/O support");
    launchHostLinkProperties.addSelectionListener(new SelectionListener() {
      public void widgetSelected(SelectionEvent event) {
        externalNsimHostLinkToolsEnabled = launchHostLinkProperties.getSelection();
        sendNotification(null);
      }

      public void widgetDefaultSelected(SelectionEvent event) {}

    });

    launchHostLinkProperties.setLayoutData(gridDataNsim);
  }

  public void createLaunchNsimJitProperties(Composite compositeNsim, GridData gridData){
    launchNsimJitProperties = new Button(compositeNsim, SWT.CHECK); //$NON-NLS-1$ //6-3
    launchNsimJitProperties.setSelection(externalNsimJitEnabled);
    launchNsimJitProperties.setText("JIT");
    launchNsimJitProperties.setToolTipText("Enable (1) or disable (0) JIT simulation mode (-p nsim_fast={0,1})");
    launchNsimJitProperties.setLayoutData(gridData);
  }

  public void createLaunchTcfPropertiesButton(final Composite compositeNsim, GridData gridData){
    launchTcfProperties = new Button(compositeNsim, SWT.CHECK); //$NON-NLS-1$ //6-3
    launchTcfProperties.setToolTipText("-propsfile=path");
    launchTcfProperties.setSelection(externalNsimPropertiesEnabled);
    launchTcfProperties.setLayoutData(gridData);
    launchTcfProperties.setText("Use nSIM properties file?");
  }

  public void createLaunchTcfButton(final Composite compositeNsim, GridData gridData){
    launchTcf = new Button(compositeNsim, SWT.CHECK); //$NON-NLS-1$ //6-3
    launchTcf.setToolTipText(
        "Pass specified TCF file to nSIM for parsing of nSIM properties (-tcf=path)" );
    launchTcf.setSelection(externalNsimTcfToolsEnabled);
    launchTcf.setLayoutData(gridData);
    launchTcf.setText("Use TCF?");
  }

  public void createNsimTcfPathEditor(Composite compositeNsim){
    nsimTcfPathEditor = new FileFieldEditor("nsimTcfPath", "nSIM TCF path", false,
        StringButtonFieldEditor.VALIDATE_ON_KEY_STROKE, compositeNsim);
    nsimTcfPathEditor.setStringValue(nsimTcfFilesLast);
    nsimTcfPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty() == "field_editor_value") {
          nsimTcfFilesLast = (String) event.getNewValue();
          sendNotification(null);
        }
      }
    });
  }

  public void createNsimPropertiesPathEditor(Composite compositeNsim){
    nsimPropertiesPathEditor = new FileFieldEditor("nsimPropertiesPath", "nSIM properties file",
        false, StringButtonFieldEditor.VALIDATE_ON_KEY_STROKE, compositeNsim);
    nsimPropertiesPathEditor.setStringValue(nsimPropertiesFilesLast);
    nsimPropertiesPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty() == "field_editor_value") {
          nsimPropertiesFilesLast = (String) event.getNewValue();
          sendNotification(null);
        }
      }
    });
  }

  private void updateFtdiCoreCombo() {
    ftdiCoreCombo.removeAll();
    java.util.List<FtdiCore> cores = ftdiDevice.getCores();
    String text = cores.get(0).toString();
    for (FtdiCore core : cores) {
        ftdiCoreCombo.add(core.toString());
        if (ftdiCore == core) {
            /*
             * Should select current ftdiCore if it is present in cores list in order to be able
             * to initialize from configuration. Otherwise ftdiCore field will be rewritten to
             * the selected core when we initialize FTDI_DeviceCombo
             */
            text = core.toString();
        }
    }
    ftdiCoreCombo.setText(text);
  }

  private String determineOpenOcdConfigurationPath() {
    final File rootDirectory = new File(openOcdBinaryPath)
        .getParentFile().getParentFile();
    final File scriptsDirectory = new File(rootDirectory,
            "share" + File.separator + "openocd" + File.separator + "scripts");
    String openOcdConfiguration = scriptsDirectory + File.separator + "board" + File.separator;

    switch (ftdiDevice) {
    case EM_SK_v1x:
        openOcdConfiguration += "snps_em_sk_v1.cfg";
        break;
    case EM_SK_v2:
        openOcdConfiguration += "snps_em_sk.cfg";
        break;
    case EM_SK_v21:
        openOcdConfiguration += "snps_em_sk_v2.1.cfg";
        break;
    case EM_SK_v22:
        openOcdConfiguration += "snps_em_sk_v2.2.cfg";
        break;
    case EM_SK_v23:
        openOcdConfiguration += "snps_em_sk_v2.3.cfg";
        break;
    case AXS101:
        openOcdConfiguration += "snps_axs101.cfg";
        break;
    case AXS102:
        openOcdConfiguration += "snps_axs102.cfg";
        break;
    case AXS103:
        if (ftdiCore == FtdiCore.HS36) {
            openOcdConfiguration += "snps_axs103_hs36.cfg";
        } else if (ftdiCore == FtdiCore.HS38_0 || ftdiCore == FtdiCore.HS38_1) {
            openOcdConfiguration += "snps_axs103_hs38.cfg";
        } else if (ftdiCore == FtdiCore.HS47D) {
            openOcdConfiguration += "snps_axs103_hs47D.cfg";
        } else {
            openOcdConfiguration += "snps_axs103_hs48.cfg";
        }
        break;
    case HSDK:
        openOcdConfiguration += "snps_hsdk.cfg";
        break;
    case IOTDK:
        openOcdConfiguration += "snps_iotdk.cfg";
        break;
    case EMSDP:
        openOcdConfiguration += "snps_em_sk_v2.3.cfg";
        break;
    case CUSTOM:
        break;
    default:
        throw new IllegalArgumentException("Unknown enum value has been used");
    }
    return openOcdConfiguration;
  }

  private void createTabItemHostAddress(Composite subComp) {
    final int screenPpi = java.awt.Toolkit.getDefaultToolkit().getScreenResolution();
    final int minTextWidth = 2 * screenPpi;
    createTabItemGenericGdbServer = true;
    groupGenericGdbServer = SWTFactory.createGroup(subComp,
            ArcGdbServer.GENERIC_GDBSERVER.toString(), 3, 5, GridData.FILL_HORIZONTAL);
    final Composite compCOM = SWTFactory.createComposite(groupGenericGdbServer, 3, 5,
            GridData.FILL_BOTH);

    Label label = new Label(compCOM, SWT.LEFT);
    label.setText("Host Address:");

    createGdbServerIpAddressText(compCOM, minTextWidth);
  }

  public void createGdbServerSettingsTab(TabFolder tabFolder) {
    // Lets set minimal width of text field to 2 inches. If more required text fields will
    // stretch.
    final int screenPpi = java.awt.Toolkit.getDefaultToolkit().getScreenResolution();
    final int minTextWidth = 2 * screenPpi;

    TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
    tabItem.setText(Messages.Gdbserver_Settings_Tab_Name);

    Composite comp = new Composite(tabFolder, SWT.NULL);
    comp.setLayout(new GridLayout(1, true));
    comp.setLayoutData(new GridData(GridData.FILL_BOTH));
    ((GridLayout) comp.getLayout()).makeColumnsEqualWidth = false;
    comp.setFont(tabFolder.getFont());
    tabItem.setControl(comp);

    final Composite subComp = new Composite(comp, SWT.NULL);
    subComp.setLayout(new GridLayout(5, true));
    subComp.setLayoutData(new GridData(GridData.FILL_BOTH));
    ((GridLayout) subComp.getLayout()).makeColumnsEqualWidth = false;
    subComp.setFont(tabFolder.getFont());

    Label label = new Label(subComp, SWT.LEFT);
    label.setText("ARC GDB Server:");
    GridData gd = new GridData();
    label.setLayoutData(gd);

    GridData serverTypeComboGridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);
    serverTypeComboGridData.horizontalSpan = 4;
    serverTypeComboGridData.minimumWidth = minTextWidth;
    externalToolsCombo = new Combo(subComp, SWT.None | SWT.READ_ONLY);
    externalToolsCombo.setLayoutData(serverTypeComboGridData);
    for (ArcGdbServer server: ArcGdbServer.values()) {
        externalToolsCombo.add(server.toString());
    }

    externalToolsCombo.addModifyListener(new ModifyListener() {
        public void modifyText(ModifyEvent event) {
            Combo combo = (Combo) event.widget;
            try {
                gdbServer = ArcGdbServer.fromString(combo.getText());
            } catch (IllegalArgumentException e) {
                gdbServer = ArcGdbServer.DEFAULT_GDB_SERVER;
            }

            if (gdbServer == ArcGdbServer.JTAG_OPENOCD) {
                setPortNumberText(LaunchConfigurationConstants.DEFAULT_OPENOCD_PORT);

                groupNsim.dispose();
                if (groupGenericGdbServer != null) {
                    groupGenericGdbServer.dispose();
                }
                groupComAshling.dispose();
                groupComCustomGdb.dispose();

                if (!createTabItemCom) {
                    if (!groupCom.isDisposed())
                        groupCom.dispose();

                    createTabItemCom(subComp);
                }
                groupCom.setText(gdbServer.toString());
                createTabItemNsim = false;
                createTabItemComAshling = false;
                groupCom.setVisible(true);
                createTabItemGenericGdbServer = false;
                createTabItemCustomGdb = false;
            } else if (gdbServer == ArcGdbServer.JTAG_ASHLING) {
                setPortNumberText(LaunchConfigurationConstants.DEFAULT_OPELLAXD_PORT);

                groupNsim.dispose();
                if (groupGenericGdbServer != null) {
                    groupGenericGdbServer.dispose();
                }
                groupCom.dispose();
                groupComCustomGdb.dispose();
                createTabItemNsim = false;
                createTabItemGenericGdbServer = false;
                createTabItemCom = false;
                createTabItemCustomGdb = false;

                if (!createTabItemComAshling) {
                    if (!groupComAshling.isDisposed())
                        groupComAshling.dispose();

                    createTabItemComAshling(subComp);
                }

                groupComAshling.setText(gdbServer.toString());
                groupComAshling.setVisible(true);
            } else if (gdbServer == ArcGdbServer.NSIM) {
                setPortNumberText(LaunchConfigurationConstants.DEFAULT_NSIM_PORT);

                if (!CommandTab.initcom.isEmpty())
                    CommandTab.initcom = "";

                IWorkbenchPage page = Workbench.getInstance().getActiveWorkbenchWindow()
                        .getActivePage();

                String viewId = "org.eclipse.tm.terminal.view.ui.TerminalsView";

                if (page != null) {
                    IViewReference[] viewReferences = page.getViewReferences();
                    for (IViewReference ivr : viewReferences) {
                        if (ivr.getId().equalsIgnoreCase(viewId)
                                || ivr.getId()
                                        .equalsIgnoreCase(
                                                "more view id if you want to close more than one at"
                                                + " a time")) {
                            page.hideView(ivr);
                        }
                    }
                }

                groupCom.dispose();
                groupComAshling.dispose();
                groupComCustomGdb.dispose();
                if (groupGenericGdbServer != null) {
                    groupGenericGdbServer.dispose();
                }
                if (!createTabItemNsim) {
                    if (!groupNsim.isDisposed())
                        groupNsim.dispose();
                    createTabItemNsim(subComp);

                    launchTcfProperties.setSelection(externalNsimPropertiesEnabled);
                    launchTcf.setSelection(externalNsimTcfToolsEnabled);
                    launchNsimJitProperties.setSelection(externalNsimJitEnabled);
                    launchHostLinkProperties.setSelection(externalNsimHostLinkToolsEnabled);
                    launchMemoryExceptionProperties.setSelection(
                        externalNsimMemoryExceptionToolsEnabled);
                    launchEnableExceptionProperties.setSelection(
                        externalNsimEnableExceptionToolsEnabled);
                    launchInvalidInstructionExceptionProperties.setSelection(
                        launchExternalNsimInvalidInstructionException);
                }
                groupNsim.setText(gdbServer.toString());
                createTabItemCom = false;
                createTabItemComAshling = false;
                groupNsim.setVisible(true);
                createTabItemGenericGdbServer = false;
                createTabItemCustomGdb = false;

            } else if (gdbServer == ArcGdbServer.GENERIC_GDBSERVER) {
                groupCom.dispose();
                groupComAshling.dispose();
                groupNsim.dispose();
                groupComCustomGdb.dispose();
                if (!createTabItemGenericGdbServer) {
                    if (groupGenericGdbServer != null && !groupGenericGdbServer.isDisposed())
                        groupGenericGdbServer.dispose();

                    createTabItemHostAddress(subComp);
                }
                createTabItemCom = false;
                createTabItemComAshling = false;
                createTabItemNsim = false;
                createTabItemCustomGdb = false;
                groupGenericGdbServer.setText(gdbServer.toString());
                groupGenericGdbServer.setVisible(true);

                IWorkbenchPage page = Workbench.getInstance().getActiveWorkbenchWindow()
                        .getActivePage();

                String viewId = "org.eclipse.tm.terminal.view.ui.TerminalsView";

                if (page != null) {
                    IViewReference[] viewReferences = page.getViewReferences();
                    for (IViewReference ivr : viewReferences) {
                        if (ivr.getId().equalsIgnoreCase(viewId)
                                || ivr.getId()
                                        .equalsIgnoreCase(
                                                "more view id if you want to close more than one at"
                                                + " a time")) {
                            page.hideView(ivr);
                        }
                    }
                }

                if (!groupCom.isDisposed())
                    groupCom.setVisible(false);
                if (!groupNsim.isDisposed())
                    groupNsim.setVisible(false);
                if (!groupComAshling.isDisposed())
                    groupComAshling.setVisible(false);
                if (!groupComCustomGdb.isDisposed()) {
                    groupComCustomGdb.setVisible(false);
                }

            } else if (gdbServer == ArcGdbServer.CUSTOM_GDBSERVER) {
                setPortNumberText(LaunchConfigurationConstants.DEFAULT_OPELLAXD_PORT);

                groupNsim.dispose();
                groupCom.dispose();
                groupComAshling.dispose();
                groupGenericGdbServer.dispose();
                createTabItemNsim = false;
                createTabItemCom = false;
                createTabItemComAshling = false;
                createTabItemGenericGdbServer = false;
                if (!createTabItemCustomGdb) {
                    if (!groupComCustomGdb.isDisposed())
                        groupComCustomGdb.dispose();

                    createTabCustomGdb(subComp);
                }

                groupComCustomGdb.setText(gdbServer.toString());
                if (!groupComCustomGdb.isVisible())
                    groupComCustomGdb.setVisible(true);
            }

            subComp.layout();
            sendNotification(null);
        }
    });

    // GDB port label
    label = new Label(subComp, SWT.LEFT);
    label.setText(Messages.Port_number_textfield_label);
    GridData gdbPortLabelGridData = new GridData();
    gdbPortLabelGridData.horizontalSpan = 1;
    label.setLayoutData(gdbPortLabelGridData);

    createGdbServerPortNumberText(subComp, minTextWidth);

    // Load elf?
    this.loadElfButton = new Button(subComp, SWT.CHECK);
    this.loadElfButton.setText("Load application into target?");
    this.loadElfButton.addSelectionListener( new SelectionListener() {
        @Override
        public void widgetDefaultSelected(SelectionEvent event) {
        }
        @Override
        public void widgetSelected(SelectionEvent event) {
            sendNotification(null);
        }
    });

    if (!createTabItemNsim)
        createTabItemNsim(subComp);
    if (!createTabItemCom)
        createTabItemCom(subComp);
    if (!createTabItemComAshling)
        createTabItemComAshling(subComp);
    if (!createTabItemGenericGdbServer)
        createTabItemHostAddress(subComp);
    if (!createTabItemCustomGdb)
        createTabCustomGdb(subComp);
  }

  public void createTabItemNsim(Composite subComp) {
    createTabItemNsim = true;
    groupNsim = SWTFactory.createGroup(subComp, externalToolsCombo.getItem(0), 3, 5,
            GridData.FILL_HORIZONTAL);
    final Composite compositeNsim = SWTFactory.createComposite(groupNsim, 3, 5, GridData.FILL_BOTH);
    createNsimBinaryPathEditor(compositeNsim);
    createLaunchTcfButton(compositeNsim, createGridData(3));
    createNsimTcfPathEditor(compositeNsim);
    nsimTcfPathEditor.setEnabled(externalNsimTcfToolsEnabled, compositeNsim);
    createLaunchTcfPropertiesButton(compositeNsim, createGridData(3));
    createNsimPropertiesPathEditor(compositeNsim);
    nsimPropertiesPathEditor.setEnabled(externalNsimPropertiesEnabled, compositeNsim);
    addSelectionListenerForLaunchTcf(compositeNsim);
    addSelectionListenerForLaunchTcfPropertiesButton(compositeNsim);
    createLaunchNsimJitProperties(compositeNsim, createGridData(3));
    createJitThreadSpinner(compositeNsim, createGridData(3));
    GridData gridDataNsim = createGridData(2);
    createLaunchHostLinkProperties(compositeNsim, gridDataNsim);
    createLaunchMemoryExceptionProperties(compositeNsim, gridDataNsim);
    createLaunchEnableExceptionPropertiesButton(compositeNsim, gridDataNsim);
    createlaunchInvalidInstructionExceptionProperties(compositeNsim, gridDataNsim);
    workingDirectoryBlockNsim.createControl(compositeNsim);
  }

  private void addSelectionListenerForLaunchTcf(final Composite compositeNsim) {
    launchTcfProperties.addSelectionListener(new SelectionListener() {
      public void widgetSelected(SelectionEvent event) {
        if (launchTcfProperties.getSelection()) {
          externalNsimPropertiesEnabled = true;
          nsimPropertiesPathEditor.setEnabled(true, compositeNsim);
        } else {
          externalNsimPropertiesEnabled = false;
          nsimPropertiesPathEditor.setEnabled(false, compositeNsim);
        }
        sendNotification(null);
      }

      public void widgetDefaultSelected(SelectionEvent event) {}
    });

  }

  private void addSelectionListenerForLaunchTcfPropertiesButton(final Composite compositeNsim) {
    launchTcf.addSelectionListener(new SelectionListener() {
      public void widgetSelected(SelectionEvent event) {
        if (launchTcf.getSelection()) {
          externalNsimTcfToolsEnabled = true;
          nsimTcfPathEditor.setEnabled(true, compositeNsim);
        } else {
          externalNsimTcfToolsEnabled = false;
          launchTcf.setSelection(false);
          nsimTcfPathEditor.setEnabled(false, compositeNsim);
        }
        sendNotification(null);
      }

      public void widgetDefaultSelected(SelectionEvent event) {}
    });
  }

  private GridData createGridData(int horizontalSpan){
    GridData gridData = new GridData(SWT.BEGINNING);
    gridData.horizontalSpan = horizontalSpan;
    return gridData;
  }

  public void createTabItemCom(final Composite subComp) {
    groupCom = SWTFactory.createGroup(subComp, externalToolsCombo.getItem(0), 3, 5,
            GridData.FILL_HORIZONTAL);
    final Composite compositeCom = SWTFactory.createComposite(groupCom, 3, 5, GridData.FILL_BOTH);

    createTabItemCom = true;

    createOpenOcdBinaryPathEditor(compositeCom);
    Label label = new Label(compositeCom, SWT.LEFT);
    label.setText("Development system:");
    ftdiDeviceCombo = new Combo(compositeCom, SWT.None | SWT.READ_ONLY);// 1-2 and 1-3

    GridData gridDataJtag = new GridData(GridData.BEGINNING);
    gridDataJtag.widthHint = 220;
    gridDataJtag.horizontalSpan = 2;
    ftdiDeviceCombo.setLayoutData(gridDataJtag);

    for (FtdiDevice i : FtdiDevice.values())
        ftdiDeviceCombo.add(i.toString());
    ftdiDeviceCombo.setText(ftdiDevice.toString());

    ftdiDeviceCombo.addModifyListener(new ModifyListener() {
        public void modifyText(ModifyEvent event) {
            Combo combo = (Combo) event.widget;
            ftdiDevice = FtdiDevice.fromString(combo.getText());

            if (ftdiDevice == FtdiDevice.CUSTOM)
                openOcdConfigurationPathEditor.setEnabled(true, compositeCom);
            else
                openOcdConfigurationPathEditor.setEnabled(false, compositeCom);

            if (ftdiDevice.getCores().size() <= 1)
                ftdiCoreCombo.setEnabled(false);
            else
                ftdiCoreCombo.setEnabled(true);

            updateFtdiCoreCombo();
            sendNotification(null);
        }
    });

    Label coreLabel = new Label(compositeCom, SWT.LEFT);
    coreLabel.setText("Target Core");
    ftdiCoreCombo = new Combo(compositeCom, SWT.None | SWT.READ_ONLY);
    ftdiCoreCombo.setLayoutData(gridDataJtag);

    if (ftdiDevice.getCores().size() <= 1)
      ftdiCoreCombo.setEnabled(false);
    else
      ftdiCoreCombo.setEnabled(true);

    updateFtdiCoreCombo();

    ftdiCoreCombo.addModifyListener(new ModifyListener() {
        public void modifyText(ModifyEvent event) {
            Combo combo = (Combo) event.widget;
            if (!combo.getText().isEmpty()) {
                ftdiCore = FtdiCore.fromString(combo.getText());
                if (getFtdiDevice() != FtdiDevice.CUSTOM) {
                    openOcdConfigurationPath = determineOpenOcdConfigurationPath();
                    openOcdConfigurationPathEditor.setStringValue(openOcdConfigurationPath);
                }
            }
            sendNotification(null);
        }
    });

    openOcdConfigurationPathEditor = new FileFieldEditor("openocdConfigurationPathEditor",
        "OpenOCD configuration file",
            false, StringButtonFieldEditor.VALIDATE_ON_KEY_STROKE, compositeCom);
    openOcdConfigurationPathEditor.setEnabled(false, compositeCom);
    openOcdConfigurationPathEditor.setStringValue(openOcdConfigurationPath);
    openOcdConfigurationPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getProperty() == "field_editor_value") {
                openOcdConfigurationPath = event.getNewValue().toString();
                sendNotification(null);
            }
        }
    });

    if (openOcdConfigurationPathEditor != null) {
        if (!ftdiDeviceCombo.getText().equalsIgnoreCase(
                FtdiDevice.CUSTOM.toString())) {
            openOcdConfigurationPathEditor.setEnabled(false, compositeCom);
        } else {
            openOcdConfigurationPathEditor.setEnabled(true, compositeCom);
        }
    }
  }

  public void createTabCustomGdb(Composite subComp) {
    createTabItemCustomGdb = true;

    groupComCustomGdb = SWTFactory.createGroup(subComp, externalToolsCombo.getItem(0), 3, 5,
          GridData.FILL_HORIZONTAL);
    final Composite compositeCustomGdb = SWTFactory.createComposite(
        groupComCustomGdb, 3, 5, GridData.FILL_BOTH);

    // GDB server executable path
    customGdbBinaryPathEditor = new FileFieldEditor("GDB server executable path",
        "GDB server executable path", compositeCustomGdb);
    customGdbBinaryPathEditor.setStringValue(customGdbPath);
    customGdbBinaryPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getProperty() == "field_editor_value") {
                customGdbPath = (String) event.getNewValue();
                sendNotification(null);
            }
        }
    });

    createCustomGdbServerArgs(compositeCustomGdb);
  }

  public void createLaunchMemoryExceptionProperties(Composite compositeNsim, GridData gridDataNsim){
    launchMemoryExceptionProperties = new Button(compositeNsim, SWT.CHECK); //$NON-NLS-1$ //6-3
    launchMemoryExceptionProperties.setToolTipText(
        "Simulate (1) or break (0) on memory exception (-p memory_exception_interrupt={0,1})");
    launchMemoryExceptionProperties.setSelection(externalNsimMemoryExceptionToolsEnabled);
    launchMemoryExceptionProperties.setText("Memory Exception");
    launchMemoryExceptionProperties.addSelectionListener(new SelectionListener() {
        public void widgetSelected(SelectionEvent event) {
            externalNsimMemoryExceptionToolsEnabled = launchMemoryExceptionProperties.getSelection();
            sendNotification(null);
        }

        public void widgetDefaultSelected(SelectionEvent event) {
        }

    });

    launchMemoryExceptionProperties.setLayoutData(gridDataNsim);
  }

  public void createGdbServerIpAddressText(Composite compCOM, int minTextWidth){
    // GDB host text field
    gdbServerIpAddressText = new Text(compCOM, SWT.SINGLE | SWT.BORDER | SWT.BEGINNING);
    GridData gdbHostFieldGridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);
    gdbHostFieldGridData.minimumWidth = minTextWidth;
    gdbServerIpAddressText.setLayoutData(gdbHostFieldGridData);
    if (hostName.isEmpty())
        gdbServerIpAddressText.setText(LaunchConfigurationConstants.DEFAULT_GDB_HOST);
    else
        gdbServerIpAddressText.setText(hostName);
    gdbServerIpAddressText.addModifyListener(new ModifyListener() {
        public void modifyText(ModifyEvent event) {
            sendNotification(null);
        }
    });
  }

  public void createGdbServerPortNumberText(Composite subComp, int minTextWidth){
    // GDB port text field
    gdbServerPortNumberText = new Text(subComp, SWT.SINGLE | SWT.BORDER | SWT.BEGINNING);
    GridData gdbPortTextGridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);
    gdbPortTextGridData.horizontalSpan = 4;
    gdbPortTextGridData.minimumWidth = minTextWidth;
    gdbServerPortNumberText.setLayoutData(gdbPortTextGridData);
    gdbServerPortNumberText.addModifyListener(new ModifyListener() {
        public void modifyText(ModifyEvent event) {
            sendNotification(null);
        }
    });
  }

  public void createAshlingBinaryPathEditor(Composite compositeCom){
    // Path to Ashling binary
    ashlingBinaryPathEditor = new FileFieldEditor("ashlingBinaryPath", "Ashling binary path", false,
            StringButtonFieldEditor.VALIDATE_ON_KEY_STROKE, compositeCom);
    ashlingBinaryPathEditor.setStringValue(externalToolsAshlingPath);

    ashlingBinaryPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getProperty() == "field_editor_value") {
                externalToolsAshlingPath = (String) event.getNewValue();
                setChanged();
                notifyObservers();
            }
        }
    });
  }

  public void createNsimBinaryPathEditor(Composite compositeNsim){
    nsimBinaryPathEditor = new FileFieldEditor("nsimBinPath", "nSIM executable", false,
        StringButtonFieldEditor.VALIDATE_ON_KEY_STROKE, compositeNsim);

    nsimBinaryPathEditor.setStringValue(externalToolsNsimPath);
    nsimBinaryPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty() == "field_editor_value") {
            externalToolsNsimPath = (String) event.getNewValue();
            sendNotification(null);
        }
    }
});
  }

  public void createOpenOcdBinaryPathEditor(Composite compositeCom){
    // Path to OpenOCD binary
    openOcdBinaryPathEditor = new FileFieldEditor("openocdBinaryPathEditor", "OpenOCD executable",
        false, StringButtonFieldEditor.VALIDATE_ON_KEY_STROKE, compositeCom);
    openOcdBinaryPathEditor.setStringValue(openOcdBinaryPath);
    openOcdBinaryPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getProperty() == "field_editor_value") {
                openOcdBinaryPath = (String) event.getNewValue();
                if (ftdiDevice != FtdiDevice.CUSTOM) {
                    openOcdConfigurationPath = determineOpenOcdConfigurationPath();
                    openOcdConfigurationPathEditor.setStringValue(openOcdConfigurationPath);
                }
                setChanged();
                notifyObservers();
            }
        }
    });
  }

  public void createAshlingTdescXmlPathEditor(Composite compositeCom){
    // Path to ashling target description file
    ashlingTdescXmlPathEditor = new FileFieldEditor("ashlingTdescXmlPath",
            "Target description XML file", false,
            StringButtonFieldEditor.VALIDATE_ON_KEY_STROKE, compositeCom);
    ashlingTdescXmlPathEditor.setStringValue(ashlingTdescPath);

    ashlingTdescXmlPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getProperty() == "field_editor_value") {
                ashlingTdescPath = (String) event.getNewValue();
                setChanged();
                notifyObservers();
            }
        }
    });
  }

  public boolean isJtagFrequencyComboDisposed(){
    return jtagFrequencyCombo.isDisposed();
  }

  public void setTextForJtagFrequencyCombo(ConfigurationReader configurationReader){
    if (!isJtagFrequencyComboDisposed()) {
      if (configurationReader.getAshlingJtagFrequency().isEmpty())
        jtagFrequencyCombo.setText(jtagFrequencyCombo.getItem(0));
      else
        jtagFrequencyCombo.setText(jtagFrequency);
    }
  }

  public void performApply(ConfigurationWriter configurationWriter,
      ILaunchConfigurationWorkingCopy configuration) {
    if (!groupNsim.isDisposed()) {
      workingDirectoryBlockNsim.performApply(configuration);
    }
    String str = gdbServerPortNumberText.getText();
    str = str.trim();

    configurationWriter.setGdbServerPort(str);
    if (jtagFrequency != null)
      configurationWriter.setAshlingJtagFrequency(getAttributeValueFromString(jtagFrequency));

    configurationWriter.setFileFormatVersion(
        LaunchConfigurationConstants.CURRENT_FILE_FORMAT_VERSION);
    /* Because there is no setAttribute(String, long) method. */
    configurationWriter.setTimeStamp(String.format("%d", System.currentTimeMillis()));
    configurationWriter.setFtdiDevice(getAttributeValueFromString(ftdiDevice.name()));
    configurationWriter.setFtdiCore(getAttributeValueFromString(ftdiCore.name()));
    configurationWriter.setGdbPath(gdbPath);
    configurationWriter.setGdbServer(getAttributeValueFromString(gdbServer.toString()));
    configurationWriter.setOpenOcdConfig(openOcdConfigurationPath);
    configurationWriter.setOpenOcdPath(openOcdBinaryPath);
    configurationWriter.setAshlingPath(externalToolsAshlingPath);
    configurationWriter.setAshlingXmlPath(ashlingXmlPath);
    configurationWriter.setAshlingTDescPath(ashlingTdescPath);
    configurationWriter.setNsimPath(externalToolsNsimPath);
    configurationWriter.setCustomGdbServerPath(customGdbPath);
    if (customGdbCommandLineArguments != null)
      configurationWriter.setCustomGdbServerArgs(customGdbCommandLineArguments);

    configurationWriter.setNsimUseTcf(externalNsimTcfToolsEnabled);
    configurationWriter.setNsimUseJit(externalNsimJitEnabled);
    configurationWriter.setNsimUseNsimHostLink(externalNsimHostLinkToolsEnabled);
    configurationWriter.setNsimSimulateMemoryExceptions(externalNsimMemoryExceptionToolsEnabled);
    configurationWriter.setNsimSimulateExceptions(externalNsimEnableExceptionToolsEnabled);
    configurationWriter.setNsimSimulateInvalidInstructionExceptions(
        launchExternalNsimInvalidInstructionException);
    configurationWriter.setNsimUseProps(externalNsimPropertiesEnabled);
    configurationWriter.setNsimJitThreads(jitThread);
    configurationWriter.setNsimPropsPath(nsimPropertiesFilesLast);
    configurationWriter.setNsimTcfPath(nsimTcfFilesLast);
    if (groupGenericGdbServer != null && !groupGenericGdbServer.isDisposed()) {
        hostName = gdbServerIpAddressText.getText();
        configurationWriter.setHostAddress(getAttributeValueFromString(hostName));
    }

    configurationWriter.setLoadElf(this.loadElfButton.getSelection());
  }

  public String getErrorMessage(String errorMessage) {
    if (errorMessage == null && !groupNsim.isDisposed()) {
        return workingDirectoryBlockNsim.getErrorMessage();
    }
    return errorMessage;
  }

  private void createCustomGdbServerArgs(Composite compositeCustomGdb){
    // GDB server command line arguments
    Label label = new Label(compositeCustomGdb, SWT.LEFT);
    label.setText("GDB server command line arguments:");
    customGdbCommandLineArgumentsText = new Text(compositeCustomGdb, SWT.SINGLE | SWT.BORDER |
        SWT.BEGINNING);

    GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
    layoutData.widthHint = 220;
    layoutData.horizontalSpan = 2;
    customGdbCommandLineArgumentsText.setLayoutData(layoutData);
    if (customGdbCommandLineArguments != null)
        customGdbCommandLineArgumentsText.setText(customGdbCommandLineArguments);

    customGdbCommandLineArgumentsText.addModifyListener(new ModifyListener() {
        public void modifyText(ModifyEvent event) {
            customGdbCommandLineArguments = customGdbCommandLineArgumentsText.getText();
            sendNotification(null);
        }
    });
  }

  public static String getAttributeValueFromString(String string) {
    if (string != null && string.length() > 0) {
      return string;
    }
    return null;
  }

  public void createLaunchEnableExceptionPropertiesButton(final Composite compositeNsim,
      final GridData gridDataNsim){
    launchEnableExceptionProperties = new Button(compositeNsim, SWT.CHECK); //$NON-NLS-1$ //6-3
    launchEnableExceptionProperties.setSelection(externalNsimEnableExceptionToolsEnabled);
    launchEnableExceptionProperties.setText("Enable Exception");
    launchEnableExceptionProperties.setToolTipText("Simulate (1) or break (0) on any exception "
        + "(-p enable_exceptions={0,1})");
    launchEnableExceptionProperties.addSelectionListener(new SelectionListener() {
        public void widgetSelected(SelectionEvent event) {
            externalNsimEnableExceptionToolsEnabled = launchEnableExceptionProperties.getSelection();
            sendNotification(null);
        }

        public void widgetDefaultSelected(SelectionEvent event) {
        }

    });

    launchEnableExceptionProperties.setLayoutData(gridDataNsim);
  }

  public void createlaunchInvalidInstructionExceptionProperties(final Composite compositeNsim,
      final GridData gridDataNsim){
    launchInvalidInstructionExceptionProperties = new Button(compositeNsim, SWT.CHECK); //$NON-NLS-1$ //6-3
    launchInvalidInstructionExceptionProperties.setToolTipText("Simulate (1) or break (0) on"
        + " invalid instruction exception (-p invalid_instruction_interrupt={0,1})");
    launchInvalidInstructionExceptionProperties.setSelection(
        launchExternalNsimInvalidInstructionException);
    launchInvalidInstructionExceptionProperties.setText("Invalid Instruction  Exception");
    launchInvalidInstructionExceptionProperties.addSelectionListener(new SelectionListener() {
        public void widgetSelected(SelectionEvent event) {
            launchExternalNsimInvalidInstructionException =
                launchInvalidInstructionExceptionProperties.getSelection();
            sendNotification(null);
        }

        public void widgetDefaultSelected(SelectionEvent event) {
        }

    });
    launchInvalidInstructionExceptionProperties.setLayoutData(gridDataNsim);
  }

  public void selectJtagFrequencyInCombo(String jtagFrequency){
    int previous = jtagFrequencyCombo.indexOf(jtagFrequency);
    if (previous > -1)
        jtagFrequencyCombo.remove(previous);
    jtagFrequencyCombo.add(jtagFrequency, 0);
    jtagFrequencyCombo.select(0);
  }

  public void createJtagFrequencyCombo(Composite composite) {
    Label label = new Label(composite, SWT.LEFT);
    label.setText("JTAG frequency:");
    jtagFrequencyCombo = new Combo(composite, SWT.None);// 1-2 and 1-3

    GridData gridDataJtag = new GridData(GridData.BEGINNING);
    gridDataJtag.widthHint = 100;
    jtagFrequencyCombo.setLayoutData(gridDataJtag);

    jtagFrequencyCombo.add("100MHz");
    jtagFrequencyCombo.add("90MHz");
    jtagFrequencyCombo.add("80MHz");
    jtagFrequencyCombo.add("70MHz");
    jtagFrequencyCombo.add("60MHz");
    jtagFrequencyCombo.add("50MHz");
    jtagFrequencyCombo.add("40MHz");
    jtagFrequencyCombo.add("30MHz");
    jtagFrequencyCombo.add("25MHz");
    jtagFrequencyCombo.add("20MHz");
    jtagFrequencyCombo.add("18MHz");
    jtagFrequencyCombo.add("15MHz");
    jtagFrequencyCombo.add("12MHz");
    jtagFrequencyCombo.add("10MHz");
    jtagFrequencyCombo.add("9MHz");
    jtagFrequencyCombo.add("8MHz");
    jtagFrequencyCombo.add("7MHz");
    jtagFrequencyCombo.add("6MHz");
    jtagFrequencyCombo.add("5MHz");
    jtagFrequencyCombo.add("4MHz");
    jtagFrequencyCombo.add("3MHz");
    jtagFrequencyCombo.add("2500KHz");
    jtagFrequencyCombo.add("2000KHz");
    jtagFrequencyCombo.add("1800KHz");
    jtagFrequencyCombo.add("1500KHz");
    jtagFrequencyCombo.add("1200KHz");
    jtagFrequencyCombo.add("1000KHz");

    jtagFrequencyCombo.addModifyListener(
        new ModifyListener() {
          public void modifyText(ModifyEvent event) {
              Combo combo = (Combo) event.widget;
              jtagFrequency = combo.getText();
              setChanged();
              notifyObservers();
          }
        });
    //Setting text after adding listener to make sure jtagFreq field value is updated
    if (jtagFrequency != null) {
        if (jtagFrequencyCombo.getText().isEmpty() && jtagFrequency.isEmpty())
            jtagFrequencyCombo.setText("10MHz");
        else if (jtagFrequencyCombo.getText().isEmpty() && !jtagFrequency.isEmpty())
            jtagFrequencyCombo.setText(jtagFrequency);
    } else {
        jtagFrequencyCombo.setText("10MHz");
    }
  }

  static {
    if (isWindowsOs()) {
        DEFAULT_OOCD_BIN = getIdeRootDirPath() + "\\bin\\openocd.exe";
        DEFAULT_OOCD_CFG = getIdeRootDirPath()
                + "\\share\\openocd\\scripts\\board\\snps_em_sk.cfg";
    } else {
        String predefinedPath = getIdeBinDir();
        // Checking for OpenOCD binary presence in default path
        if (new File(predefinedPath).isDirectory()) {
            DEFAULT_OOCD_BIN = predefinedPath + "openocd";
            DEFAULT_OOCD_CFG = getIdeRootDir() + "share/openocd/scripts/board/snps_em_sk.cfg";
        } else {
            DEFAULT_OOCD_BIN = LaunchConfigurationConstants.DEFAULT_OPENOCD_BIN_PATH_LINUX;
            DEFAULT_OOCD_CFG = LaunchConfigurationConstants.DEFAULT_OPENOCD_CFG_PATH_LINUX;
        }
    }
  }

  public static boolean isWindowsOs() {
    boolean isWindowsOs = false;
    String osName = System.getProperty("os.name");
    /* This code may have local issues without "Locale.ENGLISH" specified, e.g. in Turkey,
     * "I" becomes lower case undotted "i" ("ı"), and "i" becomes upper case dotted "i" ("İ").
     * So "WINDOWS".toLowerCase().indexOf("win") will return -1 in Turkey.
     */
    if (osName != null && osName.toLowerCase(Locale.ENGLISH).indexOf("windows") > -1) {
        isWindowsOs = true;
    }
    return isWindowsOs;
  }

  /**
   * Get default path to nSIM application nsimdrv.
   */
  public static String getNsimdrvDefaultPath() {
      String nsimHome = System.getenv("NSIM_HOME");
      if (nsimHome == null)
          return "";
      else {
          String path = nsimHome + java.io.File.separator + "bin" + java.io.File.separator
                  + "nsimdrv";
          if (isWindowsOs()) {
              return path + ".exe";
          } else {
              return path;
          }
      }
  }

  private static String getIdeRootDirPath() {
      String s = System.getProperty("eclipse.home.location");
      s = s.substring("file:/".length()).replace("/", "\\");
      String path = s + "\\..";
      try {
          return Paths.get(path).toRealPath().toString();
      } catch (IOException e) {
          e.printStackTrace();
          return "";
      }
  }

  private static String getIdeRootDir() {
    String eclipsehome = Platform.getInstallLocation().getURL().getPath();
    File predefinedPathToDirectory = new File(eclipsehome).getParentFile();
    return predefinedPathToDirectory + File.separator;
  }

  public static String getIdeBinDir() {
    return getIdeRootDir() + "bin" + File.separator;
  }

  public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
    ConfigurationWriter configurationWriter = new ConfigurationWriter(configuration);
    configurationWriter.setGdbServerCommand(
        IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_COMMAND_DEFAULT);
    configurationWriter
        .setGdbServerPort(IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_PORT_DEFAULT);
    configurationWriter.setGdbServer(ArcGdbServer.DEFAULT_GDB_SERVER.toString());
    configurationWriter.setOpenOcdConfig(DEFAULT_OOCD_CFG);
    configurationWriter.setAshlingPath("");
    configurationWriter.setNsimPath(getNsimdrvDefaultPath());
    configurationWriter.setDoLaunchTerminal(false);
    configurationWriter.setOpenOcdPath(DEFAULT_OOCD_BIN);
    configurationWriter.setAshlingJtagFrequency("");
    configurationWriter.setFtdiDevice(LaunchConfigurationConstants.DEFAULT_FTDI_DEVICE_NAME);
    configurationWriter.setFtdiCore(LaunchConfigurationConstants.DEFAULT_FTDI_CORE_NAME);
    configurationWriter.setLoadElf(LaunchConfigurationConstants.DEFAULT_LOAD_ELF);

    // Following assignments were not needed in Eclipse Mars, as default ARC values would be used.
    // But startign with Eclipse Neon, generic CDT code assigns default values to those attributes,
    // so we need to override them with our custom defaults. Probably other ARC code, that assumes
    // that those defaults might not be set can be removed now, but I leave it as is for now.
    configurationWriter.setGdbPath(getDefaultGdbPath());

    String defaultAshlingPath =
        isWindowsOs() ? LaunchConfigurationConstants.ASHLING_DEFAULT_PATH_WINDOWS
            : LaunchConfigurationConstants.ASHLING_DEFAULT_PATH_LINUX;
    configurationWriter.setAshlingPath(defaultAshlingPath);

    String ashlingXmlFile = new File(defaultAshlingPath).getParentFile().getPath()
        + java.io.File.separator +  LaunchConfigurationConstants.ASHLING_DEFAULT_XML_FILE;
    configurationWriter.setAshlingXmlPath(ashlingXmlFile);

    String defaultTDescPath = new File(defaultAshlingPath).getParentFile().getPath()
        + java.io.File.separator + LaunchConfigurationConstants.ASHLING_DEFAULT_TDESC_FILE;
    configurationWriter.setAshlingTDescPath(defaultTDescPath);
  }
}
