package com.arc.embeddedcdt.gui;

import java.io.File;

import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.arc.embeddedcdt.LaunchConfigurationConstants;
import com.arc.embeddedcdt.common.ArcGdbServer;
import com.arc.embeddedcdt.common.FtdiCore;
import com.arc.embeddedcdt.common.FtdiDevice;

public class ComGroupContainer extends DebuggerGroupContainer {

  public ComGroupContainer(RemoteGdbDebuggerPageGui pageGui) {
    super(pageGui);
    defaultPortNumber = LaunchConfigurationConstants.DEFAULT_OPENOCD_PORT;
  }

  @Override
  public void createTab(Composite subComp, final RemoteGdbDebuggerPage caller) {
    createTabItem = true;
    guiGroup = SWTFactory.createGroup(subComp, pageGui.externalToolsCombo.getItem(0), 3, 5,
        GridData.FILL_HORIZONTAL);
    DebuggerGroupManager.guiGroupByGdbServer.put(ArcGdbServer.JTAG_OPENOCD, guiGroup);
    final Composite compositeCom = SWTFactory.createComposite(guiGroup, 3, 5, GridData.FILL_BOTH);

    // Path to OpenOCD binary
    pageGui.openOcdBinPathEditor = new FileFieldEditor("openocdBinaryPathEditor", "OpenOCD executable",
        false, StringButtonFieldEditor.VALIDATE_ON_KEY_STROKE, compositeCom);
    pageGui.openOcdBinPathEditor.setStringValue(pageGui.openOcdBinaryPath);
    pageGui.openOcdBinPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty() == "field_editor_value") {
          pageGui.openOcdBinaryPath = (String) event.getNewValue();
          if (pageGui.ftdiDevice != FtdiDevice.CUSTOM) {
            pageGui.openOcdConfigurationPath = getOpenOcdConfigurationPath();
            pageGui.openOcdConfigurationPathEditor.setStringValue(pageGui.openOcdConfigurationPath);
          }
          caller.updateLaunchConfigurationDialogPublic();
        }
      }
    });
    Label label = new Label(compositeCom, SWT.LEFT);
    label.setText("Development system:");
    pageGui.ftdiDeviceCombo = new Combo(compositeCom, SWT.None | SWT.READ_ONLY);// 1-2 and 1-3

    GridData gridDataJtag = new GridData(GridData.BEGINNING);
    gridDataJtag.widthHint = 220;
    gridDataJtag.horizontalSpan = 2;
    pageGui.ftdiDeviceCombo.setLayoutData(gridDataJtag);

    for (FtdiDevice i : FtdiDevice.values())
      pageGui.ftdiDeviceCombo.add(i.toString());
    pageGui.ftdiDeviceCombo.setText(pageGui.ftdiDevice.toString());

    pageGui.ftdiDeviceCombo.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent event) {
        Combo combo = (Combo) event.widget;
        pageGui.ftdiDevice = FtdiDevice.fromString(combo.getText());

        if (pageGui.ftdiDevice == FtdiDevice.CUSTOM)
          pageGui.openOcdConfigurationPathEditor.setEnabled(true, compositeCom);
        else
          pageGui.openOcdConfigurationPathEditor.setEnabled(false, compositeCom);

        if (pageGui.ftdiDevice.getCores().size() <= 1)
          pageGui.ftdiCoreCombo.setEnabled(false);
        else
          pageGui.ftdiCoreCombo.setEnabled(true);

        updateFtdiCoreCombo();
        caller.updateLaunchConfigurationDialogPublic();
      }
    });

    Label coreLabel = new Label(compositeCom, SWT.LEFT);
    coreLabel.setText("Target Core");
    pageGui.ftdiCoreCombo = new Combo(compositeCom, SWT.None | SWT.READ_ONLY);// 1-2 and 1-3
    pageGui.ftdiCoreCombo.setLayoutData(gridDataJtag);

    if (pageGui.ftdiDevice.getCores().size() <= 1)
      pageGui.ftdiCoreCombo.setEnabled(false);
    else
      pageGui.ftdiCoreCombo.setEnabled(true);

    updateFtdiCoreCombo();

    pageGui.ftdiCoreCombo.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent event) {
        Combo combo = (Combo) event.widget;
        if (!combo.getText().isEmpty()) {
          pageGui.ftdiCore = FtdiCore.fromString(combo.getText());
          if (pageGui.ftdiDevice != FtdiDevice.CUSTOM) {
            pageGui.openOcdConfigurationPath = getOpenOcdConfigurationPath();
            pageGui.openOcdConfigurationPathEditor.setStringValue(pageGui.openOcdConfigurationPath);
          }
        }
        caller.updateLaunchConfigurationDialogPublic();
      }
    });

    pageGui.openOcdConfigurationPathEditor =
        new FileFieldEditor("openocdConfigurationPathEditor", "OpenOCD configuration file", false,
            StringButtonFieldEditor.VALIDATE_ON_KEY_STROKE, compositeCom);
    pageGui.openOcdConfigurationPathEditor.setEnabled(false, compositeCom);
    pageGui.openOcdConfigurationPathEditor.setStringValue(pageGui.openOcdConfigurationPath);
    pageGui.openOcdConfigurationPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty() == "field_editor_value") {
          pageGui.openOcdConfigurationPath = event.getNewValue().toString();
          caller.updateLaunchConfigurationDialogPublic();
        }
      }
    });

    if (pageGui.openOcdConfigurationPathEditor != null) {
      if (!pageGui.ftdiDeviceCombo.getText().equalsIgnoreCase(FtdiDevice.CUSTOM.toString())) {
        pageGui.openOcdConfigurationPathEditor.setEnabled(false, compositeCom);
      } else {
        pageGui.openOcdConfigurationPathEditor.setEnabled(true, compositeCom);
      }
    }
  }
  
  private String getOpenOcdConfigurationPath() {
    final File rootDirectory = new File(pageGui.openOcdBinaryPath).getParentFile().getParentFile();
    final File scriptsDirectory = new File(rootDirectory,
            "share" + File.separator + "openocd" + File.separator + "scripts");
    String openOcdConfiguration = scriptsDirectory + File.separator + "board" + File.separator;

    switch (pageGui.ftdiDevice) {
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
        if (pageGui.ftdiCore == FtdiCore.HS36) {
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

  private void updateFtdiCoreCombo() {
    pageGui.ftdiCoreCombo.removeAll();
    java.util.List<FtdiCore> cores = pageGui.ftdiDevice.getCores();
    String text = cores.get(0).toString();
    for (FtdiCore core : cores) {
        pageGui.ftdiCoreCombo.add(core.toString());
        if (pageGui.ftdiCore == core) {
            /*
             * Should select current ftdiCore if it is present in cores list in order to be able
             * to initialize from configuration. Otherwise ftdiCore field will be rewritten to
             * the selected core when we initialize FTDI_DeviceCombo
             */
            text = core.toString();
        }
    }
    pageGui.ftdiCoreCombo.setText(text);
  }
  
}
