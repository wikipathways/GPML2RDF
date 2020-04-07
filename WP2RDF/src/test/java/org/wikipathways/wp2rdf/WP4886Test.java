package org.wikipathways.wp2rdf;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class WP4886Test extends AbstractWPConvertorTest {

	@BeforeClass
	public static void createModel() throws Exception {
		loadModelAsWPRDF("WP4886.gpml", "WP4886", "109431");
	}

	@Test
	public void nullPubMedIDs() throws Exception {
		String sparql = ResourceHelper.resourceAsString("pubs/nullPubMedIDs.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("PubMed with a null IDs:\n" + table, 0, table.getRowCount());
		System.out.println("===== WP4886.gpml =====");
		String ttlContent = toString(model);
		System.out.println(ttlContent);
	}

}
