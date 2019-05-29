package org.wikipathways.wp2rdf.interactionTests;

import org.junit.BeforeClass;
import org.pathvisio.core.view.MIMShapes;

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
