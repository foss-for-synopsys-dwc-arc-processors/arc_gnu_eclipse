/*******************************************************************************
* This program and the accompanying materials 
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*     Synopsys, Inc. - ARC GNU Toolchain support
*******************************************************************************/

 package org.eclipse.cdt.cross.arc.gnu.linux;
 
 import org.eclipse.cdt.cross.arc.gnu.common.IsToolchainData;
 import org.eclipse.cdt.managedbuilder.core.IToolChain;
 import org.osgi.framework.Version;
 
 public class IsToolChainSupported extends org.eclipse.cdt.cross.arc.gnu.common.IsToolChainSupported
 {
   static IsToolchainData ms_oData = null;
 
   public boolean isSupported(IToolChain oToolChain, Version oVersion, String sInstance)
   {
     if (ms_oData == null) {
       ms_oData = new IsToolchainData();
     }
     return isSupportedImpl(oToolChain, oVersion, sInstance, 
       ms_oData);
   }
 }

