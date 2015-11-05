package org.wikipathways.wp2rdf;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.bridgedb.IDMapperException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pathvisio.core.model.ConverterException;

public class OpenPHACTSTests extends AbstractConvertorTest {

	@BeforeClass
	public static void createModel() throws ConverterException, FileNotFoundException, ClassNotFoundException, IOException, IDMapperException {
		loadModelAsWPRDF("WP2901_79023.gpml", "WP2901", "79023");
	}

	@Test
	public void pathwaysGetCompounds() throws Exception {
		String sparql = ResourceHelper.resourceAsString("openphacts/pathwayGetCompounds.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertNotSame(0, table.getRowCount());
	}
	
	public void pathwayByCompoundCount() throws Exception {
		String sparql = ResourceHelper.resourceAsString("openphacts/pathwayByCompound_Count.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertNotSame(0, table.getRowCount());
	}
	public void pathwayByCompound() throws Exception {
		String sparql = ResourceHelper.resourceAsString("openphacts/pathwayByCompound.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertNotSame(0, table.getRowCount());
	}
	public void pathwayByReferenceCount() throws Exception {
		String sparql = ResourceHelper.resourceAsString("openphacts/pathwayByReference_Count.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertNotSame(0, table.getRowCount());
	}
	public void pathwayByReference() throws Exception {
		String sparql = ResourceHelper.resourceAsString("openphacts/pathwayByReference.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertNotSame(0, table.getRowCount());
	}
	public void pathwayByTargetCount() throws Exception {
		String sparql = ResourceHelper.resourceAsString("openphacts/pathwayByTarget_Count.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertNotSame(0, table.getRowCount());
	}
	public void pathwayByTarget() throws Exception {
		String sparql = ResourceHelper.resourceAsString("openphacts/pathwayByTarget.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertNotSame(0, table.getRowCount());
	}
	public void pathwayGetReferences() throws Exception {
		String sparql = ResourceHelper.resourceAsString("openphacts/pathwayGetReferences.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertNotSame(0, table.getRowCount());
	}
	public void pathwayGetTargets() throws Exception {
		String sparql = ResourceHelper.resourceAsString("openphacts/pathwayGetTargets.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertNotSame(0, table.getRowCount());
	}
	public void pathwayInfo() throws Exception {
		String sparql = ResourceHelper.resourceAsString("openphacts/pathwayInfo.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertNotSame(0, table.getRowCount());
	}
	public void pathwayListCount() throws Exception {
		String sparql = ResourceHelper.resourceAsString("openphacts/pathwayList_Count.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertNotSame(0, table.getRowCount());
	}
	public void pathwayList() throws Exception {
		String sparql = ResourceHelper.resourceAsString("openphacts/pathwayList.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertNotSame(0, table.getRowCount());
	}
	public void pathwayOrganisms() throws Exception {
		String sparql = ResourceHelper.resourceAsString("openphacts/pathwayOrganisms.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertNotSame(0, table.getRowCount());
	}
	
}
