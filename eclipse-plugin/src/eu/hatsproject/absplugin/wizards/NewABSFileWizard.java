package eu.hatsproject.absplugin.wizards;

import static eu.hatsproject.absplugin.util.Constants.ABS_FILE_EXTENSION;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;

import abs.frontend.ast.CompilationUnit;
import abs.frontend.ast.ModuleDecl;
import eu.hatsproject.absplugin.builder.AbsNature;
import eu.hatsproject.absplugin.console.ConsoleManager;
import eu.hatsproject.absplugin.console.ConsoleManager.MessageType;
import eu.hatsproject.absplugin.editor.ABSEditor;
import eu.hatsproject.absplugin.navigator.ModulePath;
import eu.hatsproject.absplugin.util.InternalASTNode;
import eu.hatsproject.absplugin.util.UtilityFunctions;

/**
 * New Wizard for creating a new module in a new file
 * @author cseise, mweber
 *
 */
public class NewABSFileWizard extends Wizard implements INewWizard {
	private IStructuredSelection selection;
	private WizardNewFileCreationPage mainPage;
	
	private final String WIZARD_TITLE = "New ABS module (new file)"; 
	private final String WIZARD_DESCRIPTION = "Creates a new ABS File with a new module";

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		setWindowTitle(WIZARD_TITLE);
	}

	@Override
	public void addPages() {
		IStructuredSelection projectSelectionFromModulePath = getProjectSelectionFromModulePath(selection);
		mainPage = new WizardNewFileCreationPage(WIZARD_TITLE, projectSelectionFromModulePath);
		mainPage.setTitle(WIZARD_TITLE);
		mainPage.setFileExtension(ABS_FILE_EXTENSION);
		mainPage.setDescription(WIZARD_DESCRIPTION);
		addPage(mainPage);
	}

	@Override
	public boolean performFinish() {
		IFile file = mainPage.createNewFile();
		if (file != null) {
			try {
				ABSEditor targeteditor = (ABSEditor) IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file);
				
				insertModuleDeclaration(targeteditor);

			} catch (PartInitException e) {
				e.printStackTrace(ConsoleManager.getDefault().getPrintStream(MessageType.MESSAGE_ERROR));
			} catch (BadLocationException e) {
				e.printStackTrace(ConsoleManager.getDefault().getPrintStream(MessageType.MESSAGE_ERROR));
			}
			return true;
		} else
			return false;
	}

	/**
	 * If the original selection 
	 * @param targeteditor
	 * @throws BadLocationException
	 */
	private void insertModuleDeclaration(ABSEditor targeteditor) throws BadLocationException {
		IDocument doc = targeteditor.getDocumentProvider().getDocument(targeteditor.getEditorInput());
		ModulePath mp = getLastModulePathElement(selection);
		
		if (mp != null) {
			doc.replace(0, 0, "module " + mp.getModulePath() + ".");
			targeteditor.getSelectionProvider().setSelection(new TextSelection(8 + mp.getModulePath().length(), 0));
		} else {
			doc.replace(0, 0, "module ");
			targeteditor.getSelectionProvider().setSelection(new TextSelection(7, 0));
		}
	}

	/**
	 * @param selection
	 * @return The first ModulePath element in the given selection, or null if
	 *         the selection is null or the selection does not contain any
	 *         ModulePath element
	 */
	private ModulePath getLastModulePathElement(IStructuredSelection selection) {
		if (selection != null) {
			Object[] array = selection.toArray();
			for (int i = array.length-1; i>=0; i--) {
				Object o = array[i];
				if (o instanceof ModulePath) {
					ModulePath mp = (ModulePath) o;
					return mp;
				}
			}
		}

		return null;
	}

	private IStructuredSelection getProjectSelectionFromModulePath(IStructuredSelection sel) {
		ModulePath mp = getLastModulePathElement(sel);
		if (mp != null) {
			AbsNature nature = mp.getNature();
			IProject project = nature.getProject();
			LinkedHashSet<InternalASTNode<ModuleDecl>> modulesForPrefix = mp.getModulesForPrefix();
			InternalASTNode<ModuleDecl> m;
			try {
				// Get first the of element in the HashSet
				m = modulesForPrefix.iterator().next();
			} catch (NoSuchElementException nse) {
				m = null;
			}

			List<IResource> folders = new ArrayList<IResource>();

			folders.add(project);

			if (m != null) {
				CompilationUnit compilationUnit = UtilityFunctions.getCompilationUnitOfASTNode(m.getASTNode());
				IPath path = new Path(compilationUnit.getFileName());
				path = path.makeRelativeTo(project.getLocation());

				for (int i = 0; i < path.segmentCount() - 1; i++) {
					folders.add(project.getFolder(path.segment(i)));
				}

			}

			TreePath treePath = new TreePath(folders.toArray());
			TreeSelection treeSelection = new TreeSelection(new TreePath[] { treePath });
			return treeSelection;
		}
		return sel;
	}
}
