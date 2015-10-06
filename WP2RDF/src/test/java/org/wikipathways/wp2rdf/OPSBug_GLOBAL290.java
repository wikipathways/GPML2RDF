package org.wikipathways.wp2rdf;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pathvisio.core.model.ConverterException;

public class OPSBug_GLOBAL290 extends AbstractConvertorTest {

	@BeforeClass
	public static void createModel() throws ConverterException {
		loadModelAsWPRDF("test-ChEBI-oldIDStyle.gpml", "WP4", "1"); // not really WP4
	}

	@Test
	public void properIdentifierDotOrgURI() throws Exception {
		String turtle = toString(model);
		Assert.assertFalse(turtle.contains("http://identifiers.org/obo.chebi/47776"));
		Assert.assertTrue(turtle.contains("http://identifiers.org/obo.chebi/CHEBI:47776"));
	}

	@Test
	public void properIdentifierDotOrgURIFormat() throws Exception {
		String turtle = toString(model);
		Assert.assertFalse(turtle.contains("http://identifiers.org/chebi/"));
	}
}
