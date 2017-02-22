package com.arc.embeddedcdt.gui;

import org.eclipse.swt.widgets.Composite;

public class ComCustomGdbGroupContainer extends DebuggerGroupContainer{

  public ComCustomGdbGroupContainer(RemoteGdbDebuggerPageGui pageGui) {
    super(pageGui);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void chosenInGui(Composite comp, RemoteGdbDebuggerPage caller) {
    super.chosenInGui(comp, caller);
    updateLaunchConfigurationDialog();
  }
  
  private void updateLaunchConfigurationDialog(){
    
  }

  @Override
  public void createTab(Composite comp, RemoteGdbDebuggerPage caller) {
    // TODO Auto-generated method stub
    
  }
}
