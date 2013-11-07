package com.arc.embeddedcdt.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.arc.embeddedcdt.LaunchPlugin;

public class MainPrefPage extends FieldEditorPreferencePage
		implements
			IWorkbenchPreferencePage {

	String description;

	public MainPrefPage() {
		super(GRID);
		setPreferenceStore(LaunchPlugin.getDefault().getPreferenceStore());
		initializeDefaults();
		setDescription(description) ;
	}

	public void createFieldEditors() {
		addField(new StringFieldEditor(PrefConstants.P_DEBUGGER_NAME,"&Debugger name:", getFieldEditorParent()));
		addField(new FileFieldEditor(PrefConstants.P_DEBUGGER_INIT,"Debugger &init:", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
	}

	private void initializeDefaults() {
		description = LaunchPlugin.getResourceString(PrefConstants.P_RES_DESCRIPTION);
	}
}
