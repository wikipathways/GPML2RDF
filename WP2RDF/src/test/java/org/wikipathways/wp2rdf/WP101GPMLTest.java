package org.wikipathways.wp2rdf;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class WP101GPMLTest extends AbstractGPMLConvertorTest {

	@BeforeClass
	public static void createModel() throws Exception {
		loadModelAsGPMLRDF("WP101_79360.gpml", "WP101", "79360");
	}

	@Ignore
	@Test
	public void containsPubMed() throws Exception {
		String ttl = toString(model);
		Assert.assertTrue(ttl.contains("/14769483/"));
		Assert.assertTrue(ttl.contains("\"14769483\""));
	}

}
