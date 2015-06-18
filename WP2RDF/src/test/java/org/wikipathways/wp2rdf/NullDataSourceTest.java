package org.wikipathways.wp2rdf;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pathvisio.core.model.ConverterException;

public class NullDataSourceTest extends AbstractConvertorTest {

	@BeforeClass
	public static void createModel() throws ConverterException {
		loadModel("gpmlParts/nullDataSource.gpml", "WP4", "42");
	}

	@Test
	public void isPartOfAPathway() throws Exception {
		String sparql = ResourceHelper.resourceAsString("xrefs/nullDataSource.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Found data nodes that are not part of a pathway:\n" + table, 0, table.getRowCount());
	}

}
