package eu.hatsproject.absplugin.debug.perspective;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import eu.hatsproject.absplugin.debug.DebugUtils;

/**
 * Dialog for the executeNSteps functionality of the debugger.
 * @author tfischer
 */
public class StepNumberDialog extends Dialog{

	private Spinner steps;
	
	protected StepNumberDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		GridLayout gridLayout = new GridLayout(1, false);
		Composite composite = (Composite)super.createDialogArea(parent);
	    composite.setLayout(gridLayout);
	    
	    Composite stepsContainer = new Composite(composite, SWT.NONE);
		stepsContainer.setLayout(gridLayout);
	    
	    steps = new Spinner(stepsContainer, SWT.NONE);
		steps.setMinimum(0);
		steps.setMaximum(Integer.MAX_VALUE);
		steps.setIncrement(1);
		steps.setSelection(0);
	     
	    return composite;
	}

	@Override
	protected void configureShell(Shell newShell) {
	     super.configureShell(newShell);
	     newShell.setText("Number of steps");
	}
	
	@Override
	protected void okPressed() {
		int n = steps.getSelection();
		DebugUtils.getSchedulerRef().doMultipleSteps(n);
		super.okPressed();
	}
}
