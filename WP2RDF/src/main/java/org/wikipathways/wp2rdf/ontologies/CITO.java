/* Copyright (C) 2021  Egon Willighagen <egonw@users.sf.net>
 *
 * License: new BSD.
 */
package org.wikipathways.wp2rdf.ontologies;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

public class CITO {

	public static final String URI =
			"http://purl.org/spar/cito/";

	private static final Property property(String local) {
		return ResourceFactory.createProperty(URI, local);
	}

	public static final Property cites = property("cites"); // has attribute

}
