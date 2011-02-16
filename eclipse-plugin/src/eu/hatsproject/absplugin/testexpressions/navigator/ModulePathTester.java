package eu.hatsproject.absplugin.testexpressions.navigator;

import org.eclipse.core.expressions.PropertyTester;

import eu.hatsproject.absplugin.navigator.ModulePath;

/**
 * Property tester for the properties:
 * <p>
 * <i>eu.hatsproject.absplugin.hasModule</i> 
 * 
 * Tests, if a ModulePath instance has an
 * associated ModuleDecl, or is merely stems from modules with lower hierarchy
 * levels (Example: A ModuleDecl with name A.B.C leads to the creation of a
 * ModulePath A.B, which has no ModuleDecl assigned)
 * </p>
 * <p>
 * <i>eu.hatsproject.absplugin.hasModuleWithDecls</i>
 * 
 * Same as
 * eu.hatsproject.absplugin.hasModule with the difference that it is checked,
 * whether the ModuleDecl corresponding to the ModulePath has declarations (see
 * UtilityFunctions.hasDecls(ModuleDecl) for more details.)
 * </p>
 * 
 * @author cseise
 * 
 */
public class ModulePathTester extends PropertyTester {
	private static final String HAS_MODULE_PROPERTY = "hasModule";
	private static final String HAS_MODULEWDECL_PROPERTY = "hasModuleWithDecls";
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof ModulePath){
			ModulePath mp = (ModulePath) receiver;
			
			if (HAS_MODULE_PROPERTY.equals(property)){
				//If there is no expected value assume true, else assume the value of expected value
				if (expectedValue == null){
					return mp.getModuleDecl() != null;
				}else{
					return ((Boolean)expectedValue) == (mp.getModuleDecl() != null);
				}
			}else if (HAS_MODULEWDECL_PROPERTY.equals(property)){
				//If there is no expected value assume true, else assume the value of expected value
				if (expectedValue == null){
					return mp.hasModuleWithDecls();
				}else{
					return ((Boolean)expectedValue) == (mp.hasModuleWithDecls());
				}
			}
		}
		
		//This should not happen, as the property tester is bound to ModulePaths!
		assert(false);
		return false;
	}

}
