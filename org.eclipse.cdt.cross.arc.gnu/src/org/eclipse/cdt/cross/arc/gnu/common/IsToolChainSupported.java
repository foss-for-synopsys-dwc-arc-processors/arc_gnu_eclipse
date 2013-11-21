/*******************************************************************************
* This program and the accompanying materials 
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*     Synopsys, Inc. - ARC GNU Toolchain support
*******************************************************************************/
 
package org.eclipse.cdt.cross.arc.gnu.common;
 
 import org.eclipse.cdt.managedbuilder.core.IManagedIsToolChainSupported;
 import org.eclipse.cdt.managedbuilder.core.IToolChain;
 import org.osgi.framework.Version;
 
 public abstract class IsToolChainSupported
   implements IManagedIsToolChainSupported
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
     return true;
   }
 }


