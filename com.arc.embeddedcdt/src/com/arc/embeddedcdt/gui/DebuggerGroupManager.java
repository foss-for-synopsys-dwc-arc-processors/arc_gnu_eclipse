package com.arc.embeddedcdt.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.arc.embeddedcdt.common.ArcGdbServer;

public class DebuggerGroupManager {

  public static HashMap<ArcGdbServer, Group> guiGroupByGdbServer = new HashMap<>();
  
  public static HashSet<DebuggerGroupContainer> groupContainers = new HashSet<>();
  
  public DebuggerGroupManager(final List<DebuggerGroupContainer> containers) {
    /*groupContainers.add(new ComAshlingGroupContainer());
    groupContainers.add(new ComGroupContainer());
    groupContainers.add(new ComCustomGdbGroupContainer());
    groupContainers.add(new GenericGdbServerGroupContainer());
    groupContainers.add(new NsimGroupContainer());*/
    groupContainers.addAll(containers);
  }
  
  ComAshlingGroupContainer groupComAshling;
  ComCustomGdbGroupContainer groupComCustomGdb;
  ComGroupContainer groupCom;
  NsimGroupContainer groupNsim;
  GenericGdbServerGroupContainer groupGenericGdbServer;
  
  
  public void createTabItemsIfNotCreated(Composite subComp, RemoteGdbDebuggerPage caller){
    for (DebuggerGroupContainer groupContainer : groupContainers){
      if (groupContainer.createTabItem == false){
        groupContainer.createTab(subComp, caller);
      }
    }
  }
}
