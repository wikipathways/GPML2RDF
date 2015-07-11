package org.wikipathways.wp2rdf;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pathvisio.core.model.ConverterException;

public class WP2279Test extends AbstractConvertorTest {

	@BeforeClass
	public static void createModel() throws ConverterException {
		loadModelAsGPMLRDF("WP2279_78384.gpml", "WP2279", "78384");
	}

	@Test
	public void nullPubMedIDs() throws Exception {
		String sparql = ResourceHelper.resourceAsString("pubs/nullPubMedIDs.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("PubMed with a null IDs:\n" + table, 0, table.getRowCount());
	}

	@Test
	public void nullPubMedIDs2() throws Exception {
		String sparql = ResourceHelper.resourceAsString("pubs/nullPubMedIDs2.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("PubMed with a null IDs:\n" + table, 0, table.getRowCount());
	}

}
