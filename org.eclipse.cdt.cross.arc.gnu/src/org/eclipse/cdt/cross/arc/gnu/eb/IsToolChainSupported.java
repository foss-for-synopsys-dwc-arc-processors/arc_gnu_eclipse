/*******************************************************************************
* This program and the accompanying materials 
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*     Synopsys, Inc. - ARC GNU Toolchain support
*******************************************************************************/

package org.eclipse.cdt.cross.arc.gnu.eb;

 public abstract class IsToolChainSupported extends org.eclipse.cdt.cross.arc.gnu.common.IsToolChainSupported
 {
   public String getCompilerName()
   {
    return "arcea-elf32-gcc";
   }
 }

