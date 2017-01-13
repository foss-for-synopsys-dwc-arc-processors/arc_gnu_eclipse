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

import org.eclipse.cdt.launch.remote.IRemoteConnectionConfigurationConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

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
public class DebuggerGroupContainer extends Observable{

  public static final String DEFAULT_OOCD_BIN;
  public static final String DEFAULT_OOCD_CFG;
  private Combo jtagFrequencyCombo;
  private Combo ftdiDeviceCombo;
  private Combo ftdiCoreCombo;
  private FileFieldEditor ashlingXmlPathEditor;
  private FileFieldEditor ashlingTdescXmlPathEditor;
  private FileFieldEditor ashlingBinaryPathEditor;
  private FileFieldEditor customGdbBinaryPathEditor;
  private FileFieldEditor openOcdBinaryPathEditor;
  private FileFieldEditor openOcdConfigurationPathEditor;
  private FileFieldEditor nsimBinaryPathEditor;
  private FileFieldEditor nsimTcfPathEditor;
  private FileFieldEditor nsimPropertiesPathEditor;
  private FtdiDevice ftdiDevice = LaunchConfigurationConstants.DEFAULT_FTDI_DEVICE;
  private FtdiCore ftdiCore = LaunchConfigurationConstants.DEFAULT_FTDI_CORE;
  private Text gdbServerPortNumberText;
  private Text gdbServerIpAddressText;
  private Text customGdbCommandLineArgumentsText;
  private String customGdbCommandLineArguments = null;
  private Button launchEnableExceptionProperties;
  private Button launchInvalidInstructionExceptionProperties;
  private Button launchTcf;
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

  public Button getLaunchTcf(){
    return launchTcf;
  }

  public FileFieldEditor getNsimPropertiesPathEditor(){
    return nsimPropertiesPathEditor;
  }

  public FileFieldEditor getNsimTcfPathEditor(){
    return nsimTcfPathEditor;
  }

  public boolean getExternalNsimPropertiesEnabled(){
    return externalNsimPropertiesEnabled;
  }

  public void setExternalNsimPropertiesEnabled(boolean areEnabled){
    externalNsimPropertiesEnabled = areEnabled;
  }

  public String getNsimPropertiesFilesLast(){
    return nsimPropertiesFilesLast;
  }

  public void setNsimPropertiesFilesLast(String nsimPropertiesFilesLast){
    this.nsimPropertiesFilesLast = nsimPropertiesFilesLast;
  }

  public String getGdbPath(){
    return gdbPath;
  }

  public void setGdbPath(String path){
    gdbPath = path;
  }

  public boolean getCreateTabItemCustomGdb(){
    return createTabItemCustomGdb;
  }

  public void setCreateTabItemCustomGdb(boolean isCreated){
    createTabItemCustomGdb = isCreated;
  }

  public boolean getCreateTabItemComAshling(){
    return createTabItemComAshling;
  }

  public void setCreateTabItemComAshling(boolean isCreated){
    createTabItemComAshling = isCreated;
  }

  public boolean getCreateTabItemGenericGdbServer(){
    return createTabItemGenericGdbServer;
  }

  public void setCreateTabItemGenericGdbServer(boolean isCreated){
    createTabItemGenericGdbServer = isCreated;
  }

  public boolean getCreateTabItemNsim(){
    return createTabItemNsim;
  }

  public void setCreateTabItemNsim(boolean isCreated){
    createTabItemNsim = isCreated;
  }

  public boolean getCreateTabItemCom(){
    return createTabItemCom;
  }

  public void setCreateTabItemCom(boolean isCreated){
    createTabItemCom = isCreated;
  }

  public FileFieldEditor getNsimBinaryPathEditor(){
    return nsimBinaryPathEditor;
  }

  public void setNsimBinaryPathEditor(FileFieldEditor editor){
    nsimBinaryPathEditor = editor;
  }

  public FileFieldEditor getOpenOcdConfigurationPathEditor(){
    return openOcdConfigurationPathEditor;
  }

  public void setOpenOcdConfigurationPathEditor(FileFieldEditor editor){
    openOcdConfigurationPathEditor = editor;
  }

  public FileFieldEditor getOpenOcdBinaryPathEditor(){
    return openOcdBinaryPathEditor;
  }

  public void setOpenOcdBinaryPathEditor(FileFieldEditor editor){
    openOcdBinaryPathEditor = editor;
  }

  public Combo getFtdiCoreCombo(){
    return ftdiCoreCombo;
  }

  public void setFtdiCoreCombo(Combo combo){
    ftdiCoreCombo = combo;
  }

  public Combo getFtdiDeviceCombo(){
    return ftdiDeviceCombo;
  }

  public void setFtdiDeviceCombo(Combo combo){
    ftdiDeviceCombo = combo;
  }

  public FtdiCore getFtdiCore(){
    return ftdiCore;
  }

  public void setFtdiCore(FtdiCore core){
    ftdiCore = core;
  }

  public FtdiDevice getFtdiDevice(){
    return ftdiDevice;
  }

  public void setFtdiDevice(FtdiDevice device){
    ftdiDevice = device;
  }

  /* This get-method is called with the word "access" because method
   * "getOpenOcdConfigurationPath()" already exists. */
  public String accessOpenOcdConfigurationPath(){
    return openOcdConfigurationPath;
  }

  public void setOpenOcdConfigurationPath(String path){
    openOcdConfigurationPath = path;
  }

  public void setOpenOcdBinaryPath(String path){
    openOcdBinaryPath = path;
  }

  public String getOpenOcdBinaryPath(){
    return openOcdBinaryPath;
  }

  public FileFieldEditor getCustomGdbBinaryPathEditor(){
    return customGdbBinaryPathEditor;
  }

  public void setCustomGdbPath(String path){
    customGdbPath = path;
  }

  public String getCustomGdbPath(){
    return customGdbPath;
  }

  public boolean getExternalNsimJitEnabled(){
    return externalNsimJitEnabled;
  }

  public void setExternalNsimJitEnabled(boolean isEnabled){
    this.externalNsimJitEnabled = isEnabled;
  }

  public boolean getExternalNsimHostLinkToolsEnabled(){
    return externalNsimHostLinkToolsEnabled;
  }

  public void setExternalNsimHostLinkToolsEnabled(boolean areEnabled){
    this.externalNsimHostLinkToolsEnabled = areEnabled;
  }

  public boolean getExternalNsimMemoryExceptionToolsEnabled(){
    return externalNsimMemoryExceptionToolsEnabled;
  }

  public void setExternalNsimMemoryExceptionToolsEnabled(boolean areEnabled){
    this.externalNsimMemoryExceptionToolsEnabled = areEnabled;
  }

  public boolean getExternalNsimTcfToolsEnabled(){
    return externalNsimTcfToolsEnabled;
  }

  public void setExternalNsimTcfToolsEnabled(boolean areEnabled){
    this.externalNsimTcfToolsEnabled = areEnabled;
  }

  public void setPortNumberText(String defaultText) {
    if (portNumber.isEmpty())
      gdbServerPortNumberText.setText(defaultText);
    else
      gdbServerPortNumberText.setText(portNumber);
  }

  public String getTextFromGdbServerPortNumberText(){
    return gdbServerPortNumberText.getText();
  }

  public void setSelectionForLaunchEnableExceptionPropertiesButton(){
    launchEnableExceptionProperties.setSelection(externalNsimEnableExceptionToolsEnabled);
  }

  public String getAshlingXmlPath(){
    return ashlingXmlPath;
  }

  public void setTextForGdbServerIpAddressText(final String text){
    gdbServerIpAddressText.setText(text);;
  }

  public String getTextFromGdbServerIpAddressText(){
    return gdbServerIpAddressText.getText();
  }

  public void setJitThread(final String jitThread){
    this.jitThread = jitThread;
  }

  public String getJitThread(){
    return jitThread;
  }

  public void setExternalToolsNsimPath(final String externalToolsNsimPath){
    this.externalToolsNsimPath = externalToolsNsimPath;
  }

  public String getExternalToolsNsimPath(){
    return externalToolsNsimPath;
  }

  public void setHostName(final String hostName){
    this.hostName = hostName;
  }

  public String getHostName(){
    return hostName;
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

  public void initializeFrom(ConfigurationReader configurationReader){
    createTabItemCom = false;
    createTabItemComAshling = false;
    createTabItemNsim = false;
    createTabItemGenericGdbServer = false;
    createTabItemCustomGdb = false;

    // Set host and IP.
    portNumber = configurationReader.getGdbServerPort();
    gdbServerPortNumberText.setText(portNumber);
    hostName = configurationReader.getHostAddress();
    customGdbCommandLineArguments = configurationReader.getCustomGdbServerArgs();
    customGdbPath = configurationReader.getCustomGdbServerPath();
    ftdiDevice = configurationReader.getFtdiDevice();
    ftdiCore = configurationReader.getFtdiCore();

    openOcdBinaryPath = configurationReader.getOrDefault(
        DebuggerGroupContainer.DEFAULT_OOCD_BIN, "", configurationReader.getOpenOcdPath());
    openOcdConfigurationPath = configurationReader.getOrDefault(
        DebuggerGroupContainer.DEFAULT_OOCD_CFG, "", configurationReader.getOpenOcdConfig());
    if (!ftdiDeviceCombo.isDisposed())
      ftdiDeviceCombo.setText(ftdiDevice.toString());
    if (!ftdiCoreCombo.isDisposed())
      ftdiCoreCombo.setText(ftdiCore.toString());

    jtagFrequency = configurationReader.getAshlingJtagFrequency();
    if (!isJtagFrequencyComboDisposed() && !jtagFrequency.isEmpty())
      selectJtagFrequencyInCombo(jtagFrequency);
    setTextForJtagFrequencyCombo(configurationReader);
    String defaultAshlingPath =
        DebuggerGroupContainer.isWindowsOs() ? LaunchConfigurationConstants.ASHLING_DEFAULT_PATH_WINDOWS
            : LaunchConfigurationConstants.ASHLING_DEFAULT_PATH_LINUX;
    externalToolsAshlingPath = configurationReader.getOrDefault(defaultAshlingPath, "",
        configurationReader.getAshlingPath());
    String ashlingXmlFile = new File(defaultAshlingPath).getParentFile().getPath()
        + java.io.File.separator + "arc-cpu-em.xml";
    ashlingXmlPath = configurationReader.getOrDefault(ashlingXmlFile, "",
        configurationReader.getAshlingXmlPath());
    String defaultTDescPath = new File(defaultAshlingPath).getParentFile().getPath()
        + java.io.File.separator + "opella-arcem-tdesc.xml";
    ashlingTdescPath = configurationReader.getOrDefault(defaultTDescPath, "",
        configurationReader.getAshlingTDescPath());

    externalToolsNsimPath = configurationReader.getOrDefault(
        DebuggerGroupContainer.getNsimdrvDefaultPath(), "", configurationReader.getNsimPath());
    jitThread = configurationReader.getNsimJitThreads();
    launchExternalNsimInvalidInstructionException =
        configurationReader.getNsimSimulateInvalidInstructionExceptions();
    externalNsimEnableExceptionToolsEnabled = configurationReader.getNsimSimulateExceptions();
    nsimTcfFilesLast = configurationReader.getNsimTcfPath();
    nsimPropertiesFilesLast = configurationReader.getNsimPropsPath();
    externalNsimPropertiesEnabled = configurationReader.getNsimUseProps();
    externalNsimTcfToolsEnabled = configurationReader.getNsimUseTcf();
    externalNsimMemoryExceptionToolsEnabled =
        configurationReader.getNsimSimulateMemoryExceptions();
    externalNsimHostLinkToolsEnabled = configurationReader.getNsimUseNsimHostlink();
    externalNsimJitEnabled = configurationReader.getNsimUseJit();
  }

  public void createTabItemComAshling(Composite compositeCom){
    // Path to Ashling XMl file
    ashlingXmlPathEditor = new FileFieldEditor("ashlingXmlPathEditor", "Ashling XML File", false,
            StringButtonFieldEditor.VALIDATE_ON_KEY_STROKE, compositeCom);
    ashlingXmlPathEditor.setStringValue(ashlingXmlPath);

    ashlingXmlPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getProperty() == "field_editor_value") {
                ashlingXmlPath = (String) event.getNewValue();
                setChanged();
                notifyObservers();
            }
        }
    });
  }

  public void createLaunchTcf(final Composite compositeNsim, GridData gridData,
      final Button launchTcfPropertiesButton){
    launchTcf = new Button(compositeNsim, SWT.CHECK); //$NON-NLS-1$ //6-3
    launchTcf.setToolTipText("-propsfile=path");
    launchTcf.setSelection(externalNsimPropertiesEnabled);
    launchTcf.setLayoutData(gridData);
    launchTcf.setText("Use nSIM properties file?");

    launchTcf.addSelectionListener(new SelectionListener() {
        public void widgetSelected(SelectionEvent event) {
            if (launchTcf.getSelection() == true) {
                externalNsimTcfToolsEnabled = true;
                nsimTcfPathEditor.setEnabled(true, compositeNsim);

            } else {
                externalNsimTcfToolsEnabled = false;
                launchTcfPropertiesButton.setSelection(false);
                nsimTcfPathEditor.setEnabled(false, compositeNsim);
            }
            setChanged();
            notifyObservers();
        }

        public void widgetDefaultSelected(SelectionEvent event) {
        }

    });
  }

  public void createNsimTcfPathEditor(Composite compositeNsim){
    nsimTcfPathEditor = new FileFieldEditor("nsimTcfPath", "nSIM TCF path", false,
        StringButtonFieldEditor.VALIDATE_ON_KEY_STROKE, compositeNsim);
    nsimTcfPathEditor.setStringValue(nsimPropertiesFilesLast);
    nsimTcfPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty() == "field_editor_value") {
          nsimPropertiesFilesLast = (String) event.getNewValue();
          setChanged();
          notifyObservers();
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
          setChanged();
          notifyObservers();
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

  private String getOpenOcdConfigurationPath() {
    final File rootDirectory = new File(openOcdBinaryPath)
        .getParentFile().getParentFile();
    final File scriptsDirectory = new File(rootDirectory,
            "share" + File.separator + "openocd" + File.separator + "scripts");
    String openOcdConfiguration = scriptsDirectory + File.separator + "board" + File.separator;

    switch (ftdiDevice) {
    case EM_SK_v1x:
        openOcdConfiguration += "snps_em_sk_v1.cfg";
        break;
    case EM_SK_v21:
        openOcdConfiguration += "snps_em_sk_v2.1.cfg";
        break;
    case EM_SK_v22:
        openOcdConfiguration += "snps_em_sk_v2.2.cfg";
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
        } else {
            openOcdConfiguration += "snps_axs103_hs38.cfg";
        }
        break;
    case CUSTOM:
        break;
    default:
        throw new IllegalArgumentException("Unknown enum value has been used");
    }
    return openOcdConfiguration;
  }

  public void createTabItemCom(final Composite compositeCom) {
    createTabItemCom = true;
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
            setChanged();
            notifyObservers();
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
                    openOcdConfigurationPath = getOpenOcdConfigurationPath();
                    openOcdConfigurationPathEditor.setStringValue(openOcdConfigurationPath);
                }
            }
            setChanged();
            notifyObservers();
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
                setChanged();
                notifyObservers();
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

  public void createTabCustomGdb(Composite compositeCustomGdb) {
    // GDB server executable path
    customGdbBinaryPathEditor = new FileFieldEditor("GDB server executable path", "GDB server executable path",
            compositeCustomGdb);
    customGdbBinaryPathEditor.setStringValue(customGdbPath);
    customGdbBinaryPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getProperty() == "field_editor_value") {
                customGdbPath = (String) event.getNewValue();
                setChanged();
                notifyObservers();
            }
        }
    });
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
            setChanged();
            notifyObservers();
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
            setChanged();
            notifyObservers();
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
            setChanged();
            notifyObservers();
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
                    openOcdConfigurationPath = getOpenOcdConfigurationPath();
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

  public void performApply(ConfigurationWriter configurationWriter) {
    configurationWriter.setNsimPath(externalToolsNsimPath);
    configurationWriter.setNsimJitThreads(jitThread);
    configurationWriter.setNsimSimulateInvalidInstructionExceptions(
        launchExternalNsimInvalidInstructionException);
    configurationWriter.setNsimSimulateExceptions(externalNsimEnableExceptionToolsEnabled);
    configurationWriter.setNsimTcfPath(nsimTcfFilesLast);
    configurationWriter.setNsimPropsPath(nsimPropertiesFilesLast);
    configurationWriter.setNsimUseTcf(externalNsimTcfToolsEnabled);
    configurationWriter.setNsimSimulateMemoryExceptions(
        externalNsimMemoryExceptionToolsEnabled);
    configurationWriter.setNsimUseNsimHostLink(externalNsimHostLinkToolsEnabled);
    configurationWriter.setNsimUseJit(externalNsimJitEnabled);
    configurationWriter.setNsimUseProps(externalNsimPropertiesEnabled);

    configurationWriter.setFtdiDevice(DebuggerGroupContainer.getAttributeValueFromString(
        getFtdiDevice().name()));
    configurationWriter.setFtdiCore(DebuggerGroupContainer.getAttributeValueFromString(
        getFtdiCore().name()));

    if (jtagFrequency != null)
        configurationWriter.setAshlingJtagFrequency(getAttributeValueFromString(jtagFrequency));
    configurationWriter.setAshlingTDescPath(ashlingTdescPath);
    configurationWriter.setAshlingXmlPath(ashlingXmlPath);
    configurationWriter.setAshlingPath(externalToolsAshlingPath);

    if (customGdbCommandLineArguments != null)
      configurationWriter.setCustomGdbServerArgs(customGdbCommandLineArguments);
    String str = gdbServerPortNumberText.getText();
    str = str.trim();
    configurationWriter.setGdbServerPort(str);
    configurationWriter.setCustomGdbServerPath(customGdbPath);
    configurationWriter.setGdbPath(getGdbPath());

    configurationWriter.setOpenOcdPath(openOcdBinaryPath);
    configurationWriter.setOpenOcdConfig(openOcdConfigurationPath);
  }

  public void createCustomGdbServerArgs(Composite compositeCustomGdb){
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
            setChanged();
            notifyObservers();
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
            setChanged();
            notifyObservers();
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
            setChanged();
            notifyObservers();
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

  public void setDefaults(ILaunchConfigurationWorkingCopy configuration){
    ConfigurationWriter configurationWriter = new ConfigurationWriter(configuration);
    configurationWriter.setGdbServerCommand(
        IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_COMMAND_DEFAULT);
    configurationWriter.setGdbServerPort(
        IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_PORT_DEFAULT);
    configurationWriter.setGdbServer(ArcGdbServer.DEFAULT_GDB_SERVER.toString());
    configurationWriter.setOpenOcdConfig(DEFAULT_OOCD_CFG);
    configurationWriter.setAshlingPath("");
    configurationWriter.setNsimPath("");
    configurationWriter.setDoLaunchTerminal(false);
    configurationWriter.setNsimDefaultPath(getNsimdrvDefaultPath());
    configurationWriter.setOpenOcdPath(DEFAULT_OOCD_BIN);
    configurationWriter.setAshlingJtagFrequency("");
    configurationWriter.setFtdiDevice(LaunchConfigurationConstants.DEFAULT_FTDI_DEVICE_NAME);
    configurationWriter.setFtdiCore(LaunchConfigurationConstants.DEFAULT_FTDI_CORE_NAME);
  }

}
