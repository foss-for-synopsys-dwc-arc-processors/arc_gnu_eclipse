/*    */ package org.eclipse.cdt.cross.arc.gnu.uclibc.common;
/*    */ 
/*    */ import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.cross.arc.gnu.uclibc.common.CommandInfo;
import org.eclipse.cdt.cross.arc.gnu.uclibc.common.IsToolchainData;
import org.eclipse.cdt.managedbuilder.core.IManagedIsToolChainSupported;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.osgi.framework.Version;

public abstract class IsToolChainSupported implements IManagedIsToolChainSupported
{
   static final boolean DEBUG = false;

   public String getCompilerName()
  {
     return "arc-elf32-gcc";
  }

   public String getPlatform() {
     return "linux";
   }

public boolean isSupportedImpl(IToolChain oToolChain, Version oVersion, String sInstance, IsToolchainData oStaticData)
{
	ITool[] tools = oToolChain.getTools();
	for (ITool tool : tools) {
		String extensions[] = tool.getAllOutputExtensions();

		if( tool.getName().indexOf("Linker")>1&&tool.getId().indexOf("share")>-1&&tool.getOutputFlag().indexOf("-shared")<1){
			tool.setOutputFlag("-shared -o");   
		}


		String cmd = tool.getToolCommand();
		if (cmd != null && cmd.length() > 0) {
			if (!CommandInfo.commandExists(cmd))
				return false;
		}

	}
	return true;
}

}
