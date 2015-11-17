package org.wikipathways.wp2rdf;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.bridgedb.IDMapperException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.pathvisio.core.model.ConverterException;

public class WP1533Test extends AbstractWPConvertorTest {

	@BeforeClass
	public static void createModel() throws ConverterException, FileNotFoundException, ClassNotFoundException, IOException, IDMapperException {
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
