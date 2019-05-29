package org.wikipathways.wp2rdf.interactionTests;

import org.junit.Assert;
import org.junit.Test;
import org.wikipathways.wp2rdf.AbstractWPConvertorTest;
import org.wikipathways.wp2rdf.ResourceHelper;
import org.wikipathways.wp2rdf.SPARQLHelper;
import org.wikipathways.wp2rdf.StringMatrix;

abstract public class AbstractInteractionTest extends AbstractWPConvertorTest {

	protected static int expectedDataNodeCount;
	protected static int expectedInteractionCount;
	protected static String expectedType;

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
		System.out.println(toString(model));
		Assert.assertTrue(table.getColumn("type").contains(expectedType));
	}
}
