package org.wikipathways.wp2rdf;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class WP4862Test extends AbstractWPConvertorTest {

	@BeforeClass
	public static void createModel() throws Exception {
		loadModelAsWPRDF("WP4862.gpml", "WP4862", "109431");
	}

	@Test
	public void nullPubMedIDs() throws Exception {
		String sparql = ResourceHelper.resourceAsString("pubs/nullPubMedIDs.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("PubMed with a null IDs:\n" + table, 0, table.getRowCount());
		System.out.println("===== WP4862.gpml =====");
		String ttlContent = toString(model);
		System.out.println(ttlContent);
	}

}
