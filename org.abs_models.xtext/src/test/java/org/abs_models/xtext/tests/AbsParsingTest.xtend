/*
 * generated by Xtext 2.17.0.M1
 */
package org.abs_models.xtext.tests

import com.google.inject.Inject
import org.abs_models.xtext.abs.CompilationUnit
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

@ExtendWith(InjectionExtension)
@InjectWith(AbsInjectorProvider)
class AbsParsingTest {
	@Inject
	ParseHelper<CompilationUnit> parseHelper
	
	@Test
	def void loadModel() {
		val result = parseHelper.parse('''
			module Xtext;
		''')
		Assertions.assertNotNull(result)
		val errors = result.eResource.errors
		Assertions.assertTrue(errors.isEmpty, '''Unexpected errors: «errors.join(", ")»''')
	}
}