package eu.hatsproject.absplugin.actions.runconfig;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class MaudeTabGroup extends AbstractLaunchConfigurationTabGroup {

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		//you have to set some tabs (not null) .. (you do not have to add MaudeTab here)
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {};
		setTabs(tabs);
	}

}
