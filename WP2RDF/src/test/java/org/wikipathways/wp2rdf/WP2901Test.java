package org.wikipathways.wp2rdf;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.pathvisio.core.model.ConverterException;

public class WP2901Test extends AbstractConvertorTest {

	@BeforeClass
	public static void createModel() throws ConverterException {
		loadModelAsGPMLRDF("WP2901_79023.gpml", "WP2901", "79023");
	}

	@Ignore("curation test")
	@Test
	public void dataNodeWithoutGraphId() throws Exception {
		String sparql = ResourceHelper.resourceAsString("structure/dataNodeWithoutGraphId.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Data nodes without @GraphId:\n" + table, 0, table.getRowCount());
	}

	@Test
	public void dataNodeWithNullInURI() throws Exception {
		String sparql = ResourceHelper.resourceAsString("structure/dataNodeWithNullInURI.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Data nodes without @GraphId:\n" + table, 0, table.getRowCount());
	}

}
