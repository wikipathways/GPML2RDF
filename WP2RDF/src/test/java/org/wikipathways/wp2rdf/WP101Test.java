package org.wikipathways.wp2rdf;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.bridgedb.IDMapperException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pathvisio.core.model.ConverterException;

public class WP101Test extends AbstractConvertorTest {

	@BeforeClass
	public static void createModel() throws ConverterException, FileNotFoundException, ClassNotFoundException, IOException, IDMapperException {
		loadModelAsWPRDF("WP101_79360.gpml", "WP101", "79360");
	}

	@Test
	public void noIdentifierURIs() throws Exception {
		String sparql = ResourceHelper.resourceAsString("structure/noIdentifierURIs.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Data nodes with a 'noIdentifier' URI:\n" + table, 0, table.getRowCount());
	}

	@Test
	public void nodesHaveTypedParents() throws Exception {
		String sparql = ResourceHelper.resourceAsString("structure/nodesHaveTypedParents.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Parents of DataNodes should be typed: " + table, 0, table.getRowCount());
	}

	@Test
	public void whitespaceIdentifiers() throws Exception {
		String sparql = ResourceHelper.resourceAsString("xrefs/allIdentifiers.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertNotSame(0, table.getRowCount());
		String errors = "";
		int errorCount = 0;
		for (int i=1; i<=table.getRowCount(); i++) {
			if (table.get(i, "identifier").trim().length() == 0) {
				errors += table.get(i, "pathway") + " " + table.get(i, "label")+ " " + table.get(i, "concept") + "\n";
				errorCount++;
			}
		}
		Assert.assertEquals(
			"Unexpected identifiers that only consist of whitespace:\n" + errors,
			0, errorCount
		);
	}

}
