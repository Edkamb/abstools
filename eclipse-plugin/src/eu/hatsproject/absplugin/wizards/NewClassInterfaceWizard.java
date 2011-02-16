package eu.hatsproject.absplugin.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import abs.frontend.ast.ModuleDecl;
import eu.hatsproject.absplugin.navigator.ModulePath;
import eu.hatsproject.absplugin.util.InternalASTNode;
import eu.hatsproject.absplugin.wizards.pages.IABSClassInterfaceWizardPage;

/**
 * Class for providing common functionality to the new interface and new class wizard
 * @author cseise
 *
 */
public abstract class NewClassInterfaceWizard extends ABSNewWizard implements INewWizard {

	protected InternalASTNode<ModuleDecl> mDecl;

	protected IABSClassInterfaceWizardPage page;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		prepareSelection(selection);

	}

	/**
	 * Extracts a module declaration out of a selection. And sets the internal
	 * state accordingly. If there if no module declaration, the internal state
	 * will not be changed.
	 * 
	 * @param selection
	 */
	@SuppressWarnings("unchecked")
	protected void prepareSelection(IStructuredSelection selection) {
		if (selection != null) {
			Object firstSelection = selection.getFirstElement();
			if (firstSelection instanceof ModulePath) {
				mDecl = ((ModulePath) firstSelection).getModuleDecl();
			} else if (firstSelection instanceof InternalASTNode<?>) {
				InternalASTNode<?> internalASTNode = (InternalASTNode<?>) firstSelection;

				if (InternalASTNode.hasASTNodeOfType(internalASTNode, ModuleDecl.class))
					//Suppress warnings as we have checked that the internal ASTNode
					//contains a module declaration
					mDecl = (InternalASTNode<ModuleDecl>) internalASTNode;
			}
		}
	}

	@Override
	public abstract void addPages();

	protected void findModuleDecl() {
		if (!mDecl.equals(page.getResultModule())){
			mDecl = page.getResultModule();
		}
	}

	@Override
	public abstract boolean performFinish();

}
