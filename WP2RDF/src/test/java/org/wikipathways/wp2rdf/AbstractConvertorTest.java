package org.wikipathways.wp2rdf;

import java.io.InputStream;

import org.junit.Assert;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.Pathway;
import org.wikipathways.wp2rdf.io.PathwayReader;

import com.hp.hpl.jena.rdf.model.Model;

public abstract class AbstractConvertorTest {

	protected static Model model;

	public static void loadModel(String gpmlFile, String wpid, String revision) throws ConverterException {
		InputStream input = AbstractConvertorTest.class.getClassLoader().getResourceAsStream(gpmlFile);
		Pathway pathway = PathwayReader.readPathway(input);
		Assert.assertNotNull(pathway);
		model = GpmlConverter.convertGpml(pathway, wpid, revision);
		Assert.assertNotNull(model);
	}

}
