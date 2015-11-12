package org.wikipathways.wp2rdf;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.bridgedb.IDMapperException;
import org.bridgedb.bio.DataSourceTxt;
import org.junit.Assert;
import org.junit.Test;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.Pathway;
import org.wikipathways.wp2rdf.io.PathwayReader;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public abstract class AbstractConvertorTest {

	protected static Model model;

	protected static String toString(Model model) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		model.write(output, "TURTLE");
		return new String(output.toByteArray());
	}

	public static void loadModelAsGPMLRDF(String gpmlFile, String wpid, String revision) throws ConverterException {
		InputStream input = AbstractConvertorTest.class.getClassLoader().getResourceAsStream(gpmlFile);
		Pathway pathway = PathwayReader.readPathway(input);
		Assert.assertNotNull(pathway);
		model = GpmlConverter.convertGpml(pathway, wpid, revision);
		Assert.assertNotNull(model);
		System.out.println("===== " + gpmlFile + " =====");
		String ttlContent = toString(model);
		if (ttlContent.length() > 1000) ttlContent.substring(0,1000);
		System.out.println(ttlContent);
	}

	public static void loadModelAsWPRDF(String gpmlFile, String wpid, String revision) throws ConverterException, FileNotFoundException, ClassNotFoundException, IOException, IDMapperException {
		DataSourceTxt.init();
		InputStream input = AbstractConvertorTest.class.getClassLoader().getResourceAsStream(gpmlFile);
		Pathway pathway = PathwayReader.readPathway(input);
		Assert.assertNotNull(pathway);
		model = GpmlConverter.convertWp(pathway, wpid, revision);
		Assert.assertNotNull(model);
		System.out.println("===== " + gpmlFile + " =====");
		String ttlContent = toString(model);
		if (ttlContent.length() > 1000) ttlContent.substring(0,1000);
		System.out.println(ttlContent);
	}

	@Test
	public void testForResourceURIsWithWhitespace() {
		String errors = "";
		int errorCount = 0;
		for (Resource resource : model.listSubjects().toSet()) {
			String resourceURI = resource.getURI();
			if (containsWhiteSpace(resourceURI)) {
				errors += "<" + resourceURI + ">\n";
				errorCount++;
			}
		}
		Assert.assertEquals(
			"Resource URIs with whitespace:\n" + errors,
			0, errorCount
		);
	}

	@Test
	public void untypedPubMedRef() throws Exception {
		String sparql = ResourceHelper.resourceAsString("structure/untypedPubMedRefs.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("No tping as wp:PublicationReference for PubMed URIs:\n" + table, 0, table.getRowCount());
	}

	private boolean containsWhiteSpace(String string) {
		for (int i=0; i<string.length(); i++) {
			if (Character.isWhitespace(string.charAt(i))) return true;
		}
		return false;
	}
}
