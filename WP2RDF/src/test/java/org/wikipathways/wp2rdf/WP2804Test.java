package org.wikipathways.wp2rdf;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pathvisio.core.model.ConverterException;

public class WP2804Test extends AbstractConvertorTest {

	@BeforeClass
	public static void createModel() throws ConverterException {
		loadModelAsWPRDF("WP2804_79882.gpml", "WP2447", "75221");
	}

	@Test
	public void noIdentifierURIs() throws Exception {
		String sparql = ResourceHelper.resourceAsString("structure/noIdentifierURIs.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Data nodes with a 'noIdentifier' URI:\n" + table, 0, table.getRowCount());
	}

	@Test
	public void nodesHaveTypedParents() throws Exception {
		String sparql = ResourceHelper.resourceAsString("structure/nodesHaveTypedParents.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Parents of DataNodes should be typed: " + table, 0, table.getRowCount());
	}

}
