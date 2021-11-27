package org.wikipathways.wp2rdf;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import org.apache.jena.rdf.model.Model;
import org.bridgedb.IDMapperException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.Pathway;
import org.wikipathways.wp2rdf.io.PathwayReader;

public class PathwayTest {

	protected static Model model;

	private static String toString(Model model) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		model.write(output, "TURTLE");
		return new String(output.toByteArray());
	}

	@BeforeClass
	public static void createModel() throws ConverterException, FileNotFoundException, ClassNotFoundException, IOException, IDMapperException {
		loadModelAsWPRDF("gpmlParts/datanode.gpml", "WP4", "42");
	}

	private static void loadModelAsWPRDF(String gpmlFile, String wpid, String revision) throws ConverterException, FileNotFoundException, ClassNotFoundException, IOException, IDMapperException {
		InputStream input = AbstractConvertorTest.class.getClassLoader().getResourceAsStream(gpmlFile);
		Pathway pathway = PathwayReader.readPathway(input);
		Assert.assertNotNull(pathway);
		model = GpmlConverter.convertWp(pathway, wpid, revision, Collections.<String>emptyList());
		Assert.assertNotNull(model);
		System.out.println("===== " + gpmlFile + " =====");
		System.out.println(toString(model));
	}

	@Test
	public void pathwayHasDCTermsIdentifier() throws Exception {
		String sparql = ResourceHelper.resourceAsString("structure/pathwayHasDCTermsIdentifier.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Found pathways without a dcterms:identifier:\n" + table, 0, table.getRowCount());
	}

}
