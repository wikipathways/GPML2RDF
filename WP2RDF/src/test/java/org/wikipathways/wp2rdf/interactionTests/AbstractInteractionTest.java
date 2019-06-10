package org.wikipathways.wp2rdf.interactionTests;

import java.io.InputStream;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.pathvisio.core.view.MIMShapes;
import org.wikipathways.wp2rdf.AbstractConvertorTest;
import org.wikipathways.wp2rdf.AbstractWPConvertorTest;
import org.wikipathways.wp2rdf.ResourceHelper;
import org.wikipathways.wp2rdf.SPARQLHelper;
import org.wikipathways.wp2rdf.StringMatrix;

abstract public class AbstractInteractionTest extends AbstractWPConvertorTest {

	protected static int expectedDataNodeCount;
	protected static int expectedInteractionCount;
	protected static String expectedType;
	
	public static void createModel(String gpmlFilePrefix) throws Exception {
		MIMShapes.registerShapes();
		loadModelAsWPRDF("interactionTests/" + gpmlFilePrefix + ".gpml", "WP42", "42");
		InputStream input = AbstractConvertorTest.class.getClassLoader().getResourceAsStream(
			"interactionTests/" + gpmlFilePrefix + ".props"
		);
		Properties props = new Properties();
		props.load(input);
		expectedDataNodeCount = Integer.parseInt(props.getProperty("dataNodeCount"));
		expectedInteractionCount = Integer.parseInt(props.getProperty("interactionCount"));
		expectedType = props.getProperty("intType").trim();
	}

	@Test
	public void testDataNodeCount() throws Exception {
		String sparql = ResourceHelper.resourceAsString("interactionTests/dataNodeCount.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals(expectedDataNodeCount, table.getRowCount());
	}

	@Test
	public void testInteractionCount() throws Exception {
		String sparql = ResourceHelper.resourceAsString("interactionTests/interactionCount.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals(expectedInteractionCount, table.getRowCount());
	}

	@Test
	public void testInteractionTypes() throws Exception {
		String sparql = ResourceHelper.resourceAsString("interactionTests/types.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertNotSame(0, table.getRowCount());
		Assert.assertTrue(table.getColumn("type").contains(expectedType));
	}
}
