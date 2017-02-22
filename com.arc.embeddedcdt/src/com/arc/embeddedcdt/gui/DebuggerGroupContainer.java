package com.arc.embeddedcdt.gui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import com.arc.embeddedcdt.common.ArcGdbServer;

abstract class DebuggerGroupContainer{
  
  public Group guiGroup;
  protected String defaultPortNumber;
  private String portNumber = "";
  protected boolean createTabItem = false;
  
  RemoteGdbDebuggerPageGui pageGui;
  
  public DebuggerGroupContainer(RemoteGdbDebuggerPageGui pageGui){
    this.pageGui = pageGui;
  }
  
  public Group getGroup(){
    return guiGroup;
  }
  
  public void chosenInGui(Composite subComp, final RemoteGdbDebuggerPage caller){
    for (ArcGdbServer server : ArcGdbServer.values()){
      if (server != pageGui.gdbServer){
        Group group = DebuggerGroupManager.guiGroupByGdbServer.get(server);
        if (pageGui.gdbServer != null && group != null){
          group.dispose();
        }
      }
    }
    if (portNumber != null) {
      if (!portNumber.isEmpty()) {
        pageGui.gdbServerPortNumberText.setText(portNumber);
      } else {
        pageGui.gdbServerPortNumberText.setText(defaultPortNumber);
      }
    }
    if (createTabItem == false) {
      if (!guiGroup.isDisposed()){
          guiGroup.dispose();
      }

      createTab(subComp, caller);
    }
    else{
      guiGroup.setText(pageGui.gdbServer.toString());
      guiGroup.setVisible(true);
    }
  }
  
  public abstract void createTab(Composite comp, RemoteGdbDebuggerPage caller);
  
}