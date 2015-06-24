package org.wikipathways.wp2rdf;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pathvisio.core.model.ConverterException;

public class OpenPHACTSTests extends AbstractConvertorTest {

	@BeforeClass
	public static void createModel() throws ConverterException {
		loadModelAsWPRDF("WP2901_79023.gpml", "WP2901", "79023");
	}

	@Test
	public void pathwaysGetCompounds() throws Exception {
		String sparql = ResourceHelper.resourceAsString("openphacts/pathwayGetCompounds.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertNotSame(0, table.getRowCount());
	}
}
