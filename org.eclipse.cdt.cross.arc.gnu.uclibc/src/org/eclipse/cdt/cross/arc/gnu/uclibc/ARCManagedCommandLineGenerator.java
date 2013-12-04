   package org.eclipse.cdt.cross.arc.gnu.uclibc;
   
   import java.util.ArrayList;
   import java.util.Arrays;
   import org.eclipse.cdt.managedbuilder.core.BuildException;
   import org.eclipse.cdt.managedbuilder.core.IManagedCommandLineInfo;
   import org.eclipse.cdt.managedbuilder.core.IOption;
   import org.eclipse.cdt.managedbuilder.core.ITool;
   import org.eclipse.cdt.managedbuilder.core.IToolChain;
   import org.eclipse.cdt.managedbuilder.internal.core.ManagedCommandLineGenerator;
   
   public class ARCManagedCommandLineGenerator extends ManagedCommandLineGenerator
   {
     private static final String OPTION_SUFIX_PROCESSOR = ".option.target.processor";
     private static final String OPTION_SUFIX_CORE = ".option.target.core700";                     //yunluz add for core 700
     private static final String OPTION_SUFIX_BARRELSHIFTER = ".option.target.barrelshifter";      //yunluz add for core barrelshifter
     private static final String OPTION_SUFIX_CODEDENSITY = ".option.target.codedensity";          //yunluz add for core codedensity
     private static final String OPTION_SUFIX_DIVIDE = ".option.target.divide";                    //yunluz add for core divide
     private static final String OPTION_SUFIX_NORMALIZE = ".option.target.normalize";              //yunluz add for core normalize
     private static final String OPTION_SUFIX_SWAP = ".option.target.swap";                 //yunluz add for core swap
     private static final String OPTION_SUFIX_COMFP = ".option.target.comfp";                 //yunluz add for core comfp
     private static final String OPTION_SUFIX_THUMB = ".option.target.thumb";
     private static final String OPTION_SUFIX_THUMB_INTERWORK = ".option.target.thumbinterwork";
     private static final String OPTION_SUFFIX_ENDIANNES = ".option.target.endiannes";
     private static final String OPTION_SUFFIX_FLOAT_ABI = ".option.target.fpu.abi";
     private static final String OPTION_SUFFIX_FLOAT_UNIT = ".option.target.fpu.unit";
     private static final String OPTION_SUFIX_DEBUGGING_LEVEL = ".option.debugging.level";
     private static final String OPTION_SUFIX_DEBUGGING_FORMAT = ".option.debugging.format";
     private static final String OPTION_SUFIX_DEBUGGING_OTHER = ".option.debugging.other";
     private static final String OPTION_SUFIX_DEBUGGING_PROF = ".option.debugging.prof";
     private static final String OPTION_SUFIX_DEBUGGING_GPROF = ".option.debugging.gprof";
     private static final boolean DEBUG_LOCAL = false;
   
     public IManagedCommandLineInfo generateCommandLineInfo(ITool oTool, String sCommandName, String[] asFlags, String sOutputFlag, String sOutputPrefix, String sOutputName, String[] asInputResources, String sCommandLinePattern)
     {
     return generateCommandLineInfo(oTool, sCommandName, asFlags, 
       sOutputFlag, sOutputPrefix, sOutputName, asInputResources, 
       sCommandLinePattern, false);
     }
   
     public IManagedCommandLineInfo generateCommandLineInfo(ITool oTool, String sCommandName, String[] asFlags, String sOutputFlag, String sOutputPrefix, String sOutputName, String[] asInputResources, String sCommandLinePattern, boolean bFlag)
     {
     ArrayList oList = new ArrayList();
     oList.addAll(
       Arrays.asList(asFlags));
   
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
                
       String sCore700 = null;        //yunluz add for core 700
       String sBarrelshifter = null;  //yunluz add for Barrelshifter
       String sCodedensity = null;    //yunluz add for codedensity
       String sDivide = null;         //yunluz add for divide
       String sNormalize = null;      //yunluz add for normalize
       String sSwap = null;           //yunluz add for swap
       String sComfp = null;           //yunluz add for comfp
       String sThumb = null;
   
       String sThumbInterwork = null;
   
       String sProcessorEndiannes = null;
   
       String sFloatAbi = null;
   
       String sFloatUnit = null;
   
       String sDebugLevel = null;
   
       String sDebugFormat = null;
   
       String sDebugOther = null;
   
       String sDebugProf = null;
   
       String sDebugGProf = null;
   
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
   
           if ((sID.endsWith(".option.target.processor")) || 
             (sID.indexOf(".option.target.processor.") > 0))
             sProcessor = sEnumCommand;
           else if ((sID.endsWith(".option.target.core700")) ||     //yunluz add for core 700
             (sID.indexOf(".option.target.core700.") > 0))          //yunluz add for core 700
             sCore700 = sEnumCommand;                               //yunluz add for core 700
           else if ((sID.endsWith(".option.target.endiannes")) || 
             (sID.indexOf(".option.target.endiannes.") > 0))
             sProcessorEndiannes = sEnumCommand;
           else if ((sID.endsWith(".option.target.fpu.abi")) || 
             (sID.indexOf(".option.target.fpu.abi.") > 0))
             sFloatAbi = sEnumCommand;
           else if ((sID.endsWith(".option.target.fpu.unit")) || 
             (sID.indexOf(".option.target.fpu.unit.") > 0))
             sFloatUnit = sEnumCommand;
           else if ((sID.endsWith(".option.debugging.level")) || 
             (sID.indexOf(".option.debugging.level.") > 0))
             sDebugLevel = sEnumCommand;
           else if ((sID.endsWith(".option.debugging.format")) || 
             (sID.indexOf(".option.debugging.format.") > 0))
             sDebugFormat = sEnumCommand;
           else if ((sID.endsWith(".option.debugging.other")) || 
             (sID.indexOf(".option.debugging.other.") > 0))
             sDebugOther = sVal;
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
   
           if ((sID.endsWith(".option.target.thumb")) || 
             (sID.indexOf(".option.target.thumb.") > 0)) {
             if (bVal)
               sThumb = sCommand;
           } else if ((sID.endsWith(".option.target.thumbinterwork")) || 
             (sID.indexOf(".option.target.thumbinterwork.") > 0)) {
             if (bVal)
               sThumbInterwork = sCommand;
           } else if ((sID.endsWith(".option.debugging.prof")) || 
             (sID.indexOf(".option.debugging.prof.") > 0)) {
             if (bVal)
               sDebugProf = sCommand;
           } else if ((sID.endsWith(".option.target.barrelshifter")) ||  //yunluz add for barrelshifter
             (sID.indexOf(".option.target.barrelshifter.") > 0)) {       //yunluz add for barrelshifter
             if (bVal)                                                   //yunluz add for barrelshifter
               sBarrelshifter = sCommand;                                //yunluz add for barrelshifter
           } else if ((sID.endsWith(".option.target.codedensity")) ||    //yunluz add for barrelshifter
             (sID.indexOf(".option.target.codedensity.") > 0)) {         //yunluz add for codedensity
              if (bVal)                                                  //yunluz add for codedensity
              sCodedensity = sCommand;                                   //yunluz add for codedensity
           } else if ((sID.endsWith(".option.target.divide")) ||         //yunluz add for divide
             (sID.indexOf(".option.target.divide.") > 0)) {              //yunluz add for divide
             if (bVal)                                                   //yunluz add for divide
             sDivide = sCommand;                                         //yunluz add for divide
           } else if ((sID.endsWith(".option.target.normalize")) ||      //yunluz add for divide
            (sID.indexOf(".option.target.normalize.") > 0)) {            //yunluz add for normalize
             if (bVal)                                                   //yunluz add for normalize
             sNormalize = sCommand;                                      //yunluz add for normalize
           } else if ((sID.endsWith(".option.target.swap")) ||           //yunluz add for divide
             (sID.indexOf(".option.target.swap.") > 0)) {                //yunluz add for swap
             if (bVal)                                                   //yunluz add for swap
              sSwap = sCommand;                                          //yunluz add for swap
           } else if ((sID.endsWith(".option.target.comfp")) ||           //yunluz add for divide
              (sID.indexOf(".option.target.comfp.") > 0)) {               //yunluz add for comfp
              if (bVal)                                                   //yunluz add for comfp
              sComfp = sCommand;                                          //yunluz add for comfp
           }else if (((sID.endsWith(".option.debugging.gprof")) || 
             (sID.indexOf(".option.debugging.gprof.") > 0)) && 
             (bVal)) {
             sDebugGProf = sCommand;
             }
   
           }
   
         }
   
       if ((sProcessor != null) && (sProcessor.length() > 0))
         oList.add(sProcessor);
       if ((sCore700 != null) && (sCore700.length() > 0))
         oList.add(sCore700);
       if ((sThumb != null) && (sThumb.length() > 0))
         oList.add(sThumb);
       if ((sThumbInterwork != null) && (sThumbInterwork.length() > 0))
         oList.add(sThumbInterwork);
       if ((sProcessorEndiannes != null) && (sProcessorEndiannes.length() > 0))
         oList.add(sProcessorEndiannes);
       if ((sFloatAbi != null) && (sFloatAbi.length() > 0)) {
         oList.add(sFloatAbi);
       if ((sFloatUnit != null) && (sFloatUnit.length() > 0))
           oList.add(sFloatUnit);
         }
       if ((sDebugLevel != null) && (sDebugLevel.length() > 0)) {
         oList.add(sDebugLevel);
       if ((sDebugFormat != null) && (sDebugFormat.length() > 0))
           oList.add(sDebugFormat);
         }
       if ((sDebugOther != null) && (sDebugOther.length() > 0))
         oList.add(sDebugOther);
       if ((sDebugProf != null) && (sDebugProf.length() > 0))
         oList.add(sDebugProf);
       if ((sDebugGProf != null) && (sDebugGProf.length() > 0)) {
         oList.add(sDebugGProf);
         }
       if ((sBarrelshifter != null) && (sBarrelshifter.length() > 0)) {//yunluz add for barrelshifter
         oList.add(sBarrelshifter);                                    //yunluz add for barrelshifter
         }
       if ((sCodedensity != null) && (sCodedensity.length() > 0)) {    //yunluz add for codedensity
           oList.add(sCodedensity);                                    //yunluz add for codedensity
           }
       if ((sDivide != null) && (sDivide.length() > 0)) {              //yunluz add for divide
           oList.add(sDivide);                                         //yunluz add for divide
           }
       if ((sNormalize != null) && (sNormalize.length() > 0)) {           //yunluz add for normalize
           oList.add(sNormalize);                                         //yunluz add for normalize
           }
       if ((sSwap != null) && (sSwap.length() > 0)) {                 //yunluz add for swap
           oList.add(sSwap);                                         //yunluz add for swap     
           }
       if ((sComfp != null) && (sComfp.length() > 0)) {                 //yunluz add for comfp
           oList.add(sComfp);                                         //yunluz add for comfp     
           }      
       }
   
     return super.generateCommandLineInfo(oTool, sCommandName, 
       (String[])oList
       .toArray(new String[0]), sOutputFlag, sOutputPrefix, 
       sOutputName, asInputResources, sCommandLinePattern);
     }
   }

/* Location:           C:\Users\yunluz\Desktop\plugins\bin\
 * Qualified Name:     org.eclipse.cdt.cross.arm.gnu.ARMManagedCommandLineGenerator
 * JD-Core Version:    0.6.2
 */
