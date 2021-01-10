package org.wikipathways.wp2rdf;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class WP2864Test extends AbstractWPConvertorTest {

	@BeforeClass
	public static void createModel() throws Exception {
		loadModelAsWPRDF("WP2864_79278.gpml", "WP2864", "79278");
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
