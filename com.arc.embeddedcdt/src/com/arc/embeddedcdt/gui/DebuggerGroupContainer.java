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
  private FtdiDevice ftdiDevice = LaunchConfigurationConstants.DEFAULT_FTDI_DEVICE;
  private FtdiCore ftdiCore = LaunchConfigurationConstants.DEFAULT_FTDI_CORE;
  private Text gdbServerPortNumberText;
  private Text gdbServerIpAddressText;
  private Text customGdbCommandLineArgumentsText;
  private String customGdbCommandLineArguments = null;
  private Button launchEnableExceptionProperties;
  private Button launchInvalidInstructionExceptionProperties;
  private String jtagFrequency = null;
  private String hostName = "";
  private String portNumber = "";
  private String externalToolsNsimPath = "";
  private String jitThread = "1";
  private String nsimTcfFilesLast = "";
  private String ashlingTdescPath = "";
  private String ashlingXmlPath = "";
  private String externalToolsAshlingPath = "";
  private String customGdbPath;
  private String openOcdBinaryPath;
  private String openOcdConfigurationPath;
  private boolean launchExternalNsimInvalidInstructionException = true;
  private boolean externalNsimEnableExceptionToolsEnabled = true;
  private boolean externalNsimTcfToolsEnabled = true;
  private boolean externalNsimMemoryExceptionToolsEnabled = true;
  private boolean externalNsimHostLinkToolsEnabled = true;
  private boolean externalNsimJitEnabled = true;

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

  public String getOpenOcdConfigurationPath(){
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
    // Set host and IP.
    portNumber = configurationReader.getGdbServerPort();
    gdbServerPortNumberText.setText(portNumber);
    setHostName(configurationReader.getHostAddress());
    customGdbCommandLineArguments = configurationReader.getCustomGdbServerArgs();
    setCustomGdbPath(configurationReader.getCustomGdbServerPath());
    ftdiDevice = configurationReader.getFtdiDevice();
    ftdiCore = configurationReader.getFtdiCore();

    setOpenOcdBinaryPath(configurationReader.getOrDefault(
        DebuggerGroupContainer.DEFAULT_OOCD_BIN, "", configurationReader.getOpenOcdPath()));
    openOcdConfigurationPath = configurationReader.getOrDefault(
        DebuggerGroupContainer.DEFAULT_OOCD_CFG, "", configurationReader.getOpenOcdConfig());
    if (!ftdiDeviceCombo.isDisposed())
      ftdiDeviceCombo.setText(getFtdiDevice().toString());
    if (!ftdiCoreCombo.isDisposed())
      ftdiCoreCombo.setText(getFtdiCore().toString());

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

    setExternalToolsNsimPath(configurationReader.getOrDefault(
        DebuggerGroupContainer.getNsimdrvDefaultPath(), "", configurationReader.getNsimPath()));
    setJitThread(configurationReader.getNsimJitThreads());
    launchExternalNsimInvalidInstructionException =
        configurationReader.getNsimSimulateInvalidInstructionExceptions();
    externalNsimEnableExceptionToolsEnabled = configurationReader.getNsimSimulateExceptions();
    nsimTcfFilesLast = configurationReader.getNsimTcfPath();
    setExternalNsimTcfToolsEnabled(configurationReader.getNsimUseTcf());
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

  public void createTabCustomGdb(Composite compositeCustomGdb) {
    // GDB server executable path
    customGdbBinaryPathEditor = new FileFieldEditor("GDB server executable path", "GDB server executable path",
            compositeCustomGdb);
    customGdbBinaryPathEditor.setStringValue(getCustomGdbPath());
    customGdbBinaryPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getProperty() == "field_editor_value") {
                setCustomGdbPath((String) event.getNewValue());
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
        gdbServerIpAddressText.setText(getHostName());
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
    configurationWriter.setNsimPath(getExternalToolsNsimPath());
    configurationWriter.setNsimJitThreads(getJitThread());
    configurationWriter.setNsimSimulateInvalidInstructionExceptions(
        launchExternalNsimInvalidInstructionException);
    configurationWriter.setNsimSimulateExceptions(externalNsimEnableExceptionToolsEnabled);
    configurationWriter.setNsimTcfPath(nsimTcfFilesLast);
    configurationWriter.setNsimUseTcf(getExternalNsimTcfToolsEnabled());
    configurationWriter.setNsimSimulateMemoryExceptions(
        externalNsimMemoryExceptionToolsEnabled);
    configurationWriter.setNsimUseNsimHostLink(externalNsimHostLinkToolsEnabled);
    configurationWriter.setNsimUseJit(externalNsimJitEnabled);

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
    configurationWriter.setCustomGdbServerPath(getCustomGdbPath());

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
    if (string.length() > 0) {
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
