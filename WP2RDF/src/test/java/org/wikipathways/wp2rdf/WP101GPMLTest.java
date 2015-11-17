package org.wikipathways.wp2rdf;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.bridgedb.IDMapperException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.pathvisio.core.model.ConverterException;

public class WP101GPMLTest extends AbstractGPMLConvertorTest {

	@BeforeClass
	public static void createModel() throws ConverterException, FileNotFoundException, ClassNotFoundException, IOException, IDMapperException {
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
