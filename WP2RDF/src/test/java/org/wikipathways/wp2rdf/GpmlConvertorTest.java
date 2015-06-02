package org.wikipathways.wp2rdf;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.Pathway;
import org.wikipathways.wp2rdf.io.PathwayReader;

import com.hp.hpl.jena.rdf.model.Model;

public class GpmlConvertorTest {

	private String toString(Model model) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		model.write(output, "TURTLE");
		return new String(output.toByteArray());
	}
	
	@Test
	public void testDataNode() throws ConverterException {
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("gpmlParts/datanode.gpml");
		Pathway pathway = PathwayReader.readPathway(input);
		Model model = GpmlConverter.convert(pathway, "WP4", "42");
		String turtle = toString(model);
		Assert.assertTrue(turtle.contains("<http://rdf.wikipathways.org/Pathway/WP4_r42/DataNode/b06fe>"));
		Assert.assertTrue(turtle.contains("gpml:DataNode"));
		Assert.assertTrue(turtle.contains("gpml:centerX"));
	}

	@Test
	public void isPartOfAPathway() throws Exception {
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("gpmlParts/datanode.gpml");
		String sparql = ResourceHelper.resourceAsString("structure/isPartOfAPathway.rq");
		Pathway pathway = PathwayReader.readPathway(input);
		Model model = GpmlConverter.convert(pathway, "WP4", "42");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Found data nodes that are not part of a pathway:\n" + table, 0, table.getRowCount());
	}

	@Test
	public void noIdentifierURIs() throws Exception {
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("WP2447_75221.gpml");
		String sparql = ResourceHelper.resourceAsString("structure/noIdentifierURIs.rq");
		Pathway pathway = PathwayReader.readPathway(input);
		Model model = GpmlConverter.convert(pathway, "WP2447", "75221");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Data nodes with a 'noIdentifier' URI:\n" + table, 0, table.getRowCount());
	}

	@Test
	public void dataNodeWithoutGraphId() throws Exception {
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("WP2447_75221.gpml");
		String sparql = ResourceHelper.resourceAsString("structure/dataNodeWithoutGraphId.rq");
		Pathway pathway = PathwayReader.readPathway(input);
		Model model = GpmlConverter.convert(pathway, "WP2447", "75221");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Data nodes without @GraphId:\n" + table, 0, table.getRowCount());
	}

	@Test
	public void groupsHaveDetail() throws Exception {
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("WP2447_75221.gpml");
		String sparql = ResourceHelper.resourceAsString("structure/groupDetails.rq");
		Pathway pathway = PathwayReader.readPathway(input);
		Model model = GpmlConverter.convert(pathway, "WP2447", "75221");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Expected details for things of type gpml:Group: " + table, 0, table.getRowCount());
	}

}
