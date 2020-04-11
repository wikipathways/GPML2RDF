package org.wikipathways.wp2rdf;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class COVIDPathwayTest extends AbstractWPConvertorTest {
	
	@BeforeClass
	public static void createModel() throws Exception {
		String pathwayID = System.getProperty("WPID", "WP4846");
		loadModelAsWPRDF(pathwayID + ".gpml", pathwayID, "109431");
	}

	@Test
	public void nullPubMedIDs() throws Exception {
		String pathwayID = System.getProperty("WPID", "WP4846");
		String sparql = ResourceHelper.resourceAsString("pubs/nullPubMedIDs.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("PubMed with a null IDs:\n" + table, 0, table.getRowCount());
		System.out.println("===== " + pathwayID + ".gpml =====");
		String ttlContent = toString(model);
		System.out.println(ttlContent);
	}

}
