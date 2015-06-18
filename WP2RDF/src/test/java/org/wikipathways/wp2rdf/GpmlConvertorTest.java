package org.wikipathways.wp2rdf;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pathvisio.core.model.ConverterException;

public class GpmlConvertorTest extends AbstractConvertorTest {

	@BeforeClass
	public static void createModel() throws ConverterException {
		loadModel("WP2447_75221.gpml", "WP2447", "75221");
	}

	@Test
	public void noIdentifierURIs() throws Exception {
		String sparql = ResourceHelper.resourceAsString("structure/noIdentifierURIs.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Data nodes with a 'noIdentifier' URI:\n" + table, 0, table.getRowCount());
	}

	@Test
	public void dataNodeWithoutGraphId() throws Exception {
		String sparql = ResourceHelper.resourceAsString("structure/dataNodeWithoutGraphId.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Data nodes without @GraphId:\n" + table, 0, table.getRowCount());
	}

	@Test
	public void groupsHaveDetail() throws Exception {
		String sparql = ResourceHelper.resourceAsString("structure/groupDetails.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Expected details for things of type gpml:Group: " + table, 0, table.getRowCount());
	}

}
