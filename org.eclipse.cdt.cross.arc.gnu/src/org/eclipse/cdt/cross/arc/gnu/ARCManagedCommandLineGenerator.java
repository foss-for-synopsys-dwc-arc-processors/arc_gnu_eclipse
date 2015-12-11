/*******************************************************************************
* This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*     Synopsys, Inc. - ARC GNU Toolchain support
*******************************************************************************/

package org.eclipse.cdt.cross.arc.gnu;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IManagedCommandLineInfo;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedCommandLineGenerator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.ui.statushandlers.StatusManager;

import com.arc.cdt.toolchain.tcf.TcfContent;
import com.arc.cdt.toolchain.tcf.TcfContentException;

public class ARCManagedCommandLineGenerator extends ManagedCommandLineGenerator {
    public IManagedCommandLineInfo generateCommandLineInfo(ITool oTool, String sCommandName,
            String[] asFlags, String sOutputFlag, String sOutputPrefix, String sOutputName,
            String[] asInputResources, String sCommandLinePattern) {
        return generateCommandLineInfo(oTool, sCommandName, asFlags, sOutputFlag, sOutputPrefix,
                sOutputName, asInputResources, sCommandLinePattern, false);
    }

     public IManagedCommandLineInfo generateCommandLineInfo(ITool oTool, String sCommandName, String[] asFlags, String sOutputFlag, String sOutputPrefix, String sOutputName, String[] asInputResources, String sCommandLinePattern, boolean bFlag)
     {
        ArrayList<String> tcf_properties = new ArrayList<String>();
        ArrayList<String> oList = new ArrayList<String>();

         ArrayList<String> oList_gcc_options = new ArrayList<String>();
         Object oParent = oTool.getParent();
         while ((oParent != null) && (!(oParent instanceof IToolChain)))
         {
             Object oSuper = oTool.getSuperClass();
             if ((oSuper != null) && ((oSuper instanceof ITool)))
                 oParent = ((ITool)oSuper).getParent();
            else {
                 oParent = null;
            }
         }
   
        if ((oParent != null) && ((oParent instanceof IToolChain))) {
        IToolChain oToolChain = (IToolChain)oParent;
   
        IOption[] aoOptions = oToolChain.getOptions();
   
       String sProcessor = null;
                
       String sCore700 = null;        //Customized for ARC GNU core 700
       String sBarrelshifter = null;  //Customized for ARC GNU Barrelshifter
       String sCodedensity = null;    //Customized for ARC GNU codedensity
       String sDivide = null;         //Customized for ARC GNU divide
       String sNormalize = null;      //Customized for ARC GNU normalize
       String sMPY=null;
       String sSwap = null;           //Customized for ARC GNU swap
       String sEa = null;           //Customized for ARC GNU ea
   
       String sProcessorEndiannes = null;
   
       String sSyntaxonly = null;
   
       String sFPUEM = null;
       String sFPUHS = null;
       String smpyhs= null;
       String smpyem= null;
   
       String sDebugLevel = null;
   
       String sDebugFormat = null;
   
       String sDebugOther = null;
   
       String sDebugProf = null;
   
       String sDebugGProf = null;
       String satomic = null;
       String sll64 = null;
       String smfpi= null;
       String smno_dpfp_lrsr= null;
       String smul3216= null;
       String smxy= null;

       String sabi= null;
       String sTCF= null;
       Boolean tcf_selected= (Boolean) null;
       Boolean tcf_map_selected= (Boolean) null;

       String projectBuildPath = getProjectBuildPath(oToolChain);
       String tcfMapPath = projectBuildPath + File.separator + "memory.x";

       for (int i = 0; i < aoOptions.length; i++)
       {
         IOption oOption = aoOptions[i];
         String sID = oOption.getId();
   
         Object oValue = oOption.getValue();
   
         String sCommand = oOption.getCommand();
         if ((oValue instanceof String)) {
             String sVal;
             try {
             sVal = oOption.getStringValue();
             }
             catch (BuildException e)
             {
               // yunluz comment String sVal;
             sVal = null;
             }
             String sEnumCommand;
             try
             {
             sEnumCommand = oOption.getEnumCommand(sVal);
             }
             catch (BuildException e1)
             {
               //yunluz String sEnumCommand;
             sEnumCommand = null;
             }
   

           if (sID.indexOf(".option.target.processor") > 0) {
               sProcessor = sEnumCommand;
           } else if (sID.indexOf(".option.target.core700") > 0) { //Customized for ARC GNU core 700
               sCore700 = sEnumCommand;
           } else if (sID.indexOf(".option.target.endiannes") > 0) {
               sProcessorEndiannes = sEnumCommand;
           } else if (sID.indexOf(".option.warnings.syntax") > 0) {
               sSyntaxonly = sEnumCommand;
           } else if (sID.indexOf(".option.target.fpuem") > 0 && sProcessor.equals("-mcpu=arcem")) {
               sFPUEM = sEnumCommand;
           } else if (sID.indexOf(".option.target.fpuhs") > 0 && sProcessor.equals("-mcpu=archs")) {
               sFPUHS = sEnumCommand;
           } else if (sID.indexOf(".option.target.mpyhs") > 0 && sProcessor.equals("-mcpu=archs")) {
               smpyhs = sEnumCommand;
           } else if (sID.indexOf(".option.target.mpyem") > 0 && sProcessor.equals("-mcpu=arcem")) {
               smpyem = sEnumCommand;
           } else if (sID.indexOf(".option.target.fpi") > 0 && (sProcessor.equals("-mcpu=arcem") ||
                   sProcessor.equals("-mcpu=arc600")||sProcessor.equals("-mcpu=arc700"))) {
               smfpi = sEnumCommand;  //Customized for ARC GNU
           } else if (sID.indexOf(".option.debugging.level") > 0) {
               sDebugLevel = sEnumCommand;
           } else if (sID.indexOf(".option.debugging.format") > 0) {
               sDebugFormat = sEnumCommand;
           } else if (sID.indexOf(".option.debugging.other") > 0) {
               sDebugOther = sVal;
           } else if (sID.indexOf(".option.target.abiselection") > 0) {
               sabi = sEnumCommand;
           } else if (sID.indexOf(".option.target.filefortcf") > 0) {
               sTCF = sVal;
           }
         }
           else if ((oValue instanceof Boolean)) {
             boolean bVal;
             try {
             bVal = oOption.getBooleanValue();
             }
             catch (BuildException e)
             {
               //yunluz boolean bVal;
             bVal = false;
             }

             if (bVal) {
                if (sID.indexOf(".option.debugging.prof") > 0) {
                    sDebugProf = sCommand;
                } else if (sID.indexOf(".option.target.barrelshifter") > 0) {
                    sBarrelshifter = sCommand; // Customized for ARC GNU barrelshifter
                } else if (sID.indexOf(".option.target.codedensity") > 0
                        && (sProcessor.equals("-mcpu=arcem")
                                || sProcessor.equals("-mcpu=archs"))) {
                    sCodedensity = sCommand; // Customized for ARC GNU codedensity
                } else if (sID.indexOf(".option.target.divide") > 0) {
                    sDivide = sCommand; // Customized for ARC GNU divide
                } else if (sID.indexOf(".option.target.normalize") > 0) {
                    sNormalize = sCommand; // Customized for ARC GNU normalize
                } else if (sID.indexOf(".option.target.mpy") > 0) {
                    sMPY = sCommand;
                } else if (sID.indexOf(".option.target.swap") > 0) {
                    sSwap = sCommand; // Customized for ARC GNU swap
                } else if (sID.indexOf(".option.target.ea") > 0
                        && (sProcessor.equals("-mcpu=arc700")
                                || sProcessor.equals("-mcpu=arc600"))) {
                    sEa = sCommand; // Customized for ARC GNU ea
                } else if (sID.indexOf(".option.target.mul3216") > 0
                        && (sProcessor.equals("-mcpu=arc700")
                                || sProcessor.equals("-mcpu=arc600"))) {
                    smul3216 = sCommand;
                } else if (sID.indexOf(".option.target.xy") > 0
                        && (sProcessor.equals("-mcpu=arc700")
                                || sProcessor.equals("-mcpu=arc600"))) {
                    smxy = sCommand;
                } else if (sID.indexOf(".option.target.lock") > 0
                        && sProcessor.equals("-mcpu=arc700")) {
                    smxy = sCommand;
                } else if (sID.indexOf(".option.target.atomic") > 0) {
                    satomic = sCommand;
                } else if (sID.indexOf(".option.target.ll64") > 0) {
                    sll64 = sCommand;
                } else if (sID.indexOf(".option.target.mno-dpfp-lrsr") > 0) {
                    smno_dpfp_lrsr = sCommand;
                } else if (sID.indexOf(".option.debugging.gprof") > 0) {
                    sDebugGProf = sCommand;
                } else if (sID.indexOf("option.target.tcf") > 0) {
                    tcf_selected = true;
                } else if (sID.indexOf("option.target.maptcf") > 0) {
                    tcf_map_selected = true;
                }
             }
        }
       }

            if (sProcessor != null && !sProcessor.isEmpty())
                oList.add(sProcessor);
            if (sCore700 != null && !sCore700.isEmpty())
                oList.add(sCore700);
            if (sProcessorEndiannes != null && !sProcessorEndiannes.isEmpty())
                oList_gcc_options.add(sProcessorEndiannes);
            if (sSyntaxonly != null && sSyntaxonly.isEmpty()) {
                oList.add(sSyntaxonly);
            }
            if (sFPUEM != null && !sFPUEM.isEmpty())
                oList_gcc_options.add(sFPUEM);
            if (sFPUHS != null && !sFPUHS.isEmpty())
                oList_gcc_options.add(sFPUHS);
            if (smpyhs != null && smpyhs.isEmpty())
                oList_gcc_options.add(smpyhs);
            if (smpyem != null && smpyem.isEmpty())
                oList_gcc_options.add(smpyem);
            if (sDebugLevel != null && sDebugLevel.isEmpty()) {
                oList.add(sDebugLevel);
                if (sDebugFormat != null && sDebugFormat.isEmpty())
                    oList.add(sDebugFormat);
            }
            if (sDebugOther != null && sDebugOther.isEmpty())
                oList.add(sDebugOther);
            if (sDebugProf != null && sDebugProf.isEmpty())
                oList.add(sDebugProf);
            if (sDebugGProf != null && sDebugGProf.isEmpty()) {
                oList.add(sDebugGProf);
            }
            if (sBarrelshifter != null && sBarrelshifter.isEmpty()) {
                oList_gcc_options.add(sBarrelshifter); // Customized for ARC GNU barrelshifter
            }
            if (sCodedensity != null && sCodedensity.isEmpty()) {
                oList_gcc_options.add(sCodedensity); // Customized for ARC GNU codedensity
            }
            if (sDivide != null && sDivide.isEmpty()) {
                oList_gcc_options.add(sDivide); // Customized for ARC GNU divide
            }
            if (sNormalize != null && sNormalize.isEmpty()) {
                oList_gcc_options.add(sNormalize); // Customized for ARC GNU normalize
            }
            if (sMPY != null && sMPY.isEmpty()) {
                oList_gcc_options.add(sMPY); // Customized for ARC GNU mpy
            }
            if (sSwap != null && sSwap.isEmpty()) {
                oList_gcc_options.add(sSwap); // Customized for ARC GNU swap
            }
            if (smfpi != null && smfpi.isEmpty()) {
                oList_gcc_options.add(smfpi);
            }
            if (smno_dpfp_lrsr != null && smno_dpfp_lrsr.isEmpty()) {
                oList_gcc_options.add(smno_dpfp_lrsr);
            }

            if (sEa != null && sEa.isEmpty()) {
                oList_gcc_options.add(sEa);
            }
            if (smul3216 != null && smul3216.isEmpty()) {
                oList_gcc_options.add(smul3216);
            }
            if ((smxy != null) && (smxy.length() > 0)) {
                oList_gcc_options.add(smxy);
            }
            if ((satomic != null) && (satomic.length() > 0)) {
                oList_gcc_options.add(satomic);
            }
            if (sll64 != null && sll64.isEmpty()) {
                oList_gcc_options.add(sll64);
            }
            if (sabi != null && sabi.isEmpty()) {
                oList.add(sabi);
            }
            if (sTCF != null && sTCF.isEmpty()) {
                oList_gcc_options.addAll(tcf_properties);
            }
            if (sProcessor != null) {
                if (sProcessor.equals("-mcpu=arc700")) {
                    if (oList_gcc_options.indexOf(sMPY) < 0) {
                        oList_gcc_options.add("-mno-mpy");
                    } else {
                        int i = oList_gcc_options.indexOf(sMPY);
                        oList_gcc_options.remove(i);
                    }
                    if (oList_gcc_options.indexOf(sNormalize) < 0) {
                        oList_gcc_options.add("-mno-norm");
                    } else {
                        int i = oList_gcc_options.indexOf(sNormalize);
                        oList_gcc_options.remove(i);
                    }
                    if (oList_gcc_options.indexOf(sBarrelshifter) < 0) {
                        oList_gcc_options.add("-mno-barrel-shifter");
                    } else {
                        int i = oList_gcc_options.indexOf(sBarrelshifter);
                        oList_gcc_options.remove(i);
                    }
                }
                if (sProcessor.equals("-mcpu=archs")) {
                    if (oList_gcc_options.indexOf(satomic) < 0) {
                        oList_gcc_options.add("-mno-atomic");
                    } else {
                        int i = oList_gcc_options.indexOf(satomic);
                        oList_gcc_options.remove(i);
                    }
                }
            }

            if ((tcf_selected != null) && tcf_selected && sTCF != null) {
                oList_gcc_options.clear();
                TcfContent fileContent = null;
                try {
                    fileContent = TcfContent.readFile(new File(sTCF));
                } catch (TcfContentException e1) {
                    StatusManager.getManager().handle(
                            new Status(IStatus.ERROR, ARCPlugin.PLUGIN_ID, e1.getMessage()),
                            StatusManager.SHOW);
                    e1.printStackTrace();
                }
                if (fileContent != null) {
                    Properties gccOptions = fileContent.getGccOptions();
                    Enumeration<?> e = gccOptions.propertyNames();
                    tcf_properties.clear();
                    while (e.hasMoreElements()) {
                        String gcc_option = (String) e.nextElement();
                        String value = gccOptions.getProperty(gcc_option);
                        if (!gcc_option.equalsIgnoreCase("-mcpu")) {
                            if (!value.isEmpty()) {
                                gcc_option = gcc_option + "=" + value;
                            }
                            tcf_properties.add(gcc_option);
                        }
                    }
                    String memoryMap = fileContent.getLinkerMemoryMap();
                    if (tcf_map_selected != null && tcf_map_selected && memoryMap != null) {
                        try {
                            Files.deleteIfExists(Paths.get(tcfMapPath));
                            Files.write(Paths.get(tcfMapPath), memoryMap.getBytes(),
                                    StandardOpenOption.CREATE);
                        } catch (IOException e1) {
                            StatusManager.getManager().handle(
                                    new Status(IStatus.ERROR, ARCPlugin.PLUGIN_ID, e1.getMessage()),
                                    StatusManager.SHOW);
                            e1.printStackTrace();
                        }
                    }

                    if (tcf_map_selected != null && tcf_map_selected
                            && Files.exists(Paths.get(tcfMapPath))) {
                        if (oTool.getBaseId().contains("linker")) {
                            oList_gcc_options.add("-Wl,-marcv2elfb -L " + projectBuildPath);
                        }
                    }
                }
                oList_gcc_options.addAll(tcf_properties);
            }
            oList.addAll(Arrays.asList(asFlags));
        }
        oList.addAll(oList_gcc_options);
        return super.generateCommandLineInfo(oTool, sCommandName,
                (String[]) oList.toArray(new String[0]), sOutputFlag, sOutputPrefix, sOutputName,
                asInputResources, sCommandLinePattern);
     }

    private String getProjectBuildPath(IToolChain toolChain) {
        // Contains eclipse path variable, needs resolving
        String projectBuildPath = toolChain.getBuilder().getBuildPath();
        try {
            projectBuildPath = VariablesPlugin.getDefault().getStringVariableManager()
                    .performStringSubstitution(projectBuildPath);
        } catch (CoreException e) {
            StatusManager.getManager()
                    .handle(new Status(IStatus.WARNING, ARCPlugin.PLUGIN_ID,
                            "Can not save memory map from TCF in project directory, using temp"),
                    StatusManager.SHOW);
            projectBuildPath = System.getProperty("java.io.tmpdir");
        }
        return projectBuildPath;
    }

}

