package org.wikipathways.wp2rdf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;
import org.bridgedb.bio.DataSourceTxt;
import org.junit.Assert;
import org.junit.Test;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.Pathway;
import org.wikipathways.wp2rdf.io.PathwayReader;

public abstract class AbstractWPConvertorTest extends AbstractConvertorTest {

	public static void loadModelAsWPRDF(String gpmlFile, String wpid, String revision) throws ConverterException, FileNotFoundException, ClassNotFoundException, IOException, IDMapperException {
		DataSourceTxt.init();
		// the next line is needed until BridgeDb gets updated
		DataSource.register("Cpx", "Complex Portal")
		  .identifiersOrgBase("http://identifiers.org/complexportal/")
		  .asDataSource();
		DataSource.register("Pbd", "Digital Object Identifier").asDataSource();
		DataSource.register("Pbm", "PubMed").asDataSource();
		InputStream input = AbstractConvertorTest.class.getClassLoader().getResourceAsStream(gpmlFile);
		Pathway pathway = PathwayReader.readPathway(input);
		Assert.assertNotNull(pathway);
		IDMapperStack stack = WPREST2RDF.maps();
		model = GpmlConverter.convertWp(pathway, wpid, revision, stack, Collections.<String>emptyList());
		Assert.assertNotNull(model);
		// System.out.println("===== " + gpmlFile + " =====");
		// String ttlContent = toString(model);
		// if (ttlContent.length() > 1000) ttlContent.substring(0,1000);
		// System.out.println(ttlContent);
	}

	@Test
	public void untypedPubMedRef() throws Exception {
		String sparql = ResourceHelper.resourceAsString("structure/untypedPubMedRefs.rq");
		StringMatrix table = SPARQLHelper.sparql(model, sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("No tping as wp:PublicationReference for PubMed URIs:\n" + table, 0, table.getRowCount());
	}
}
