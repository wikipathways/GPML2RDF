package org.wikipathways.wp2rdf;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class WP1533Test extends AbstractWPConvertorTest {

	@BeforeClass
	public static void createModel() throws Exception {
		loadModelAsWPRDF("WP1533_82707.gpml", "WP1533", "82707");
	}

	@Ignore
	@Test
	public void hasUnlinkedPubMedReference() throws Exception {
		String ttl = toString(model);
		Assert.assertTrue(
			"PubMed reference 9789062 is not linked to a DataNode or Pathway, but should appear in the RDF",
			ttl.contains("/9789062")
		);
	}

}
