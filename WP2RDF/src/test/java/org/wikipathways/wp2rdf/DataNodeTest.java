package org.wikipathways.wp2rdf;

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pathvisio.core.model.ConverterException;

import com.hp.hpl.jena.rdf.model.Model;

public class DataNodeTest extends AbstractConvertorTest {

	private String toString(Model model) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		model.write(output, "TURTLE");
		return new String(output.toByteArray());
	}

	@BeforeClass
	public static void createModel() throws ConverterException {
		loadModelAsGPMLRDF("gpmlParts/datanode.gpml", "WP4", "42");
	}

	@Test
	public void testDataNode() throws ConverterException {
		String turtle = toString(model);
		Assert.assertTrue(turtle.contains("<http://rdf.wikipathways.org/Pathway/WP4_r42/DataNode/b06fe>"));
		Assert.assertTrue(turtle.contains("gpml:DataNode"));
		Assert.assertTrue(turtle.contains("gpml:centerX"));
	}

	@Test
	public void isPartOfAPathway() throws Exception {
		String sparql = ResourceHelper.resourceAsString("structure/isPartOfAPathway.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Found data nodes that are not part of a pathway:\n" + table, 0, table.getRowCount());
	}

}
