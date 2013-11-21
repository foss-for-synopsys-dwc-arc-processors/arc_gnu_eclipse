/*******************************************************************************
* This program and the accompanying materials 
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*     Synopsys, Inc. - ARC GNU Toolchain support
*******************************************************************************/
package com.arc.embeddedcdt.proxy.cdt;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import com.ibm.icu.text.MessageFormat;

public class LaunchMessages {

	private static HashMap<String, String> keyMap = new HashMap<String, String>();
	
	static {
		keyMap.put("Launch.common.Browse_1", 			"Launch_common_Browse_1");
		keyMap.put("CMainTab.C/C++_Application", 		"CMainTab_C_Application");
		keyMap.put("CMainTab.Search...", 				"CMainTab_Search");
		keyMap.put("Launch.common.Browse_2", 			"Launch_common_Browse_2");
		keyMap.put("CMainTab.UseTerminal", 				"CMainTab_UseTerminal");
		keyMap.put("CMainTab.Project_required", 		"CMainTab_Project_required");
		keyMap.put("CMainTab.Choose_program_to_run",	"CMainTab_Choose_program_to_run");
		keyMap.put("CMainTab.Program_Selection", 		"CMainTab_Program_selection");
		keyMap.put("Launch.common.BinariesColon", 		"Launch_common_BinariesColon");
		keyMap.put("Launch.common.QualifierColon", 		"Launch_common_QualifierColon");
		keyMap.put("CMainTab.Project_Selection", 		"CMainTab_Project_Selection");
		keyMap.put("CMainTab.Main", 					"CMainTab_Main");
		
		keyMap.put("CMainTab.Enter_project_before_searching_for_program", 
						"CMainTab_Enter_project_before_browsing_for_program");
		keyMap.put("CMainTab.Choose_project_to_constrain_search_for_program", 
						"CMainTab_Choose_project_to_constrain_search_for_program");
		keyMap.put("AbstractCLaunchDelegate.Program_file_does_not_exist", 
						"AbstractCLaunchDelegate_Program_file_does_not_exist");
		keyMap.put("AbstractCLaunchDelegate.PROGRAM_PATH_not_found", 
						"AbstractCLaunchDelegate_PROGRAM_PATH_not_found");
	}
	
	public static String getFormattedString(String key, String arg) {
		return MessageFormat.format(getString(key), new String[]{arg});
	}
	
	public static String getString(String key) {
		
		Class clazz = null;
		
		try {
			clazz = Class.forName(
					"org.eclipse.cdt.launch.internal.ui.LaunchMessages");
		} catch (ClassNotFoundException e) {
			//e.printStackTrace();
		}
		
		if(null == clazz){
			return '!' + key + '!';
		}
		
		try {
			// if it has getString(String) method
			Method getStringMethod = clazz.getMethod("getString", new Class[]{key.getClass()});
			return (String)getStringMethod.invoke(null, new Object[]{key});
			
		} catch (NoSuchMethodException e) {
			//e.printStackTrace();
		} catch (IllegalAccessException e) {
			//e.printStackTrace();
		} catch (InvocationTargetException e) {
			//e.printStackTrace();
		}
		
		try {
			// or else it has the static string field			
			Field field = clazz.getField(keyMap.get(key));
			return (String)field.get(clazz);
			
		} catch (SecurityException e) {
			//e.printStackTrace();
		} catch (NoSuchFieldException e) {
			//e.printStackTrace();
		} catch (IllegalAccessException e) {
			//e.printStackTrace();
		}
		
		return '!' + key + '!';
	}
	
}
