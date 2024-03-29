/* Copyright (C) 2013  Egon Willighagen <egonw@users.sf.net>
 *
 * License: new BSD.
 */
package org.wikipathways.wp2rdf.ontologies;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

public class SIO {

	public static final String URI =
			"http://semanticscience.org/resource/";

	private static final Property property(String local) {
		return ResourceFactory.createProperty(URI, local);
	}

	public static final Property SIO_000300 = property("SIO_000300"); // has value

}
