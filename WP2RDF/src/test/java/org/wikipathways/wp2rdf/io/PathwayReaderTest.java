package org.wikipathways.wp2rdf.io;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.Pathway;

public class PathwayReaderTest {

	@Test
	public void testReading() throws ConverterException {
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("WP2447_75221.gpml");
		Assert.assertNotNull(input);
		Pathway pathway = PathwayReader.readPathway(input);
		Assert.assertNotNull(pathway);
	}

}
