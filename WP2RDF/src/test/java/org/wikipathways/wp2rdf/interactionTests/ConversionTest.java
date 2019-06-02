package org.wikipathways.wp2rdf.interactionTests;

import org.junit.BeforeClass;
import org.pathvisio.core.view.MIMShapes;

/**
 * Based on test data by Ryan Miller. See <a href="https://github.com/RyAMiller/WikiPathwaysInteractions">this repository</a>.
 *
 * @author Egon Willighagen
 * @author Ryan Miller
 */
public class ConversionTest extends AbstractInteractionTest {

	@BeforeClass
	public static void createModel() throws Exception {
		MIMShapes.registerShapes();
		loadModelAsWPRDF("interactionTests/Conversion.gpml", "WP42", "42");
		expectedDataNodeCount = 2;
		expectedInteractionCount = 1;
		expectedType = "http://vocabularies.wikipathways.org/wp#Conversion";
	}

}
