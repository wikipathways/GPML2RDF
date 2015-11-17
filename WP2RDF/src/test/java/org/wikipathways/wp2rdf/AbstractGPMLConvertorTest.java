package org.wikipathways.wp2rdf;

import java.io.InputStream;

import org.junit.Assert;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.Pathway;
import org.wikipathways.wp2rdf.io.PathwayReader;

public abstract class AbstractGPMLConvertorTest extends AbstractConvertorTest {

	public static void loadModelAsGPMLRDF(String gpmlFile, String wpid, String revision) throws ConverterException {
		InputStream input = AbstractConvertorTest.class.getClassLoader().getResourceAsStream(gpmlFile);
		Pathway pathway = PathwayReader.readPathway(input);
		Assert.assertNotNull(pathway);
		model = GpmlConverter.convertGpml(pathway, wpid, revision);
		Assert.assertNotNull(model);
		// System.out.println("===== " + gpmlFile + " =====");
		// String ttlContent = toString(model);
		// if (ttlContent.length() > 1000) ttlContent.substring(0,1000);
		// System.out.println(ttlContent);
	}

}
