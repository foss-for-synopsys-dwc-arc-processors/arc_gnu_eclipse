/*    */ package org.eclipse.cdt.cross.arc.gnu.uclibc.common;
/*    */ 
/*    */ import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.cross.arc.gnu.uclibc.common.CommandInfo;
import org.eclipse.cdt.cross.arc.gnu.uclibc.common.IsToolchainData;
import org.eclipse.cdt.managedbuilder.core.IManagedIsToolChainSupported;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Version;

public abstract class IsToolChainSupported implements IManagedIsToolChainSupported
{
   static final boolean DEBUG = false;

   public String getCompilerName()
  {
     return "arc-linux-gcc";
  }

   public String getPlatform() {
     return "linux";
   }
static String last_command="";
public boolean isSupportedImpl(IToolChain oToolChain, Version oVersion, String sInstance, IsToolchainData oStaticData)
{
	ITool[] tools = oToolChain.getTools();
    for (ITool tool : tools) {
        String extensions[] = tool.getAllOutputExtensions();
        List<String> extList = Arrays.asList(extensions);
        if (extList.contains("o") || extList.contains("obj")) {
            // We assume this tool is the compiler if its output
            // is .o or .obj file.
            // If the compiler doesn't exist in the search path,
            // then we don't support the tool.
            String cmd = tool.getToolCommand();
            if (cmd != null && cmd.length() > 0) {
                if (!CommandInfo.commandExists(cmd))
                    return false;
            }
        }
        
        if(CommandInfo.path_or_predefined_path.equalsIgnoreCase("PREDEFINED_PATH")){
     	  String current_tool_command=tool.getToolCommand();
    	      String eclipsehome = Platform.getInstallLocation().getURL().getPath();
    		  File predefined_path_dir = new File(eclipsehome).getParentFile();
    	      String predefined_path = predefined_path_dir + File.separator + "bin"+File.separator;
    	      if(current_tool_command.indexOf(predefined_path)<0){
    	    	 last_command=current_tool_command;
    	    	 tool.setToolCommand(predefined_path+last_command); 
    	    	 last_command="";
    	       }  
     	   
        }
    }
    return true;
}
}
