package org.wikipathways.wp2rdf.utils;

import java.util.HashMap;
import java.util.Map;

import org.pathvisio.core.biopax.PublicationXref;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;

import com.hp.hpl.jena.rdf.model.Resource;

public class DataHandlerWp {
	private Map<PathwayElement, Resource> pathwayElements;
	private Map<PublicationXref, Resource> pubXrefs;

	private Pathway pathway;
	private Resource pathwayRes;

	public DataHandlerWp(Pathway p, Resource pathwayRes) {
		pathway = p;
		this.pathwayRes = pathwayRes;
		pathwayElements = new HashMap<PathwayElement, Resource>();
		pubXrefs = new HashMap<PublicationXref, Resource>();
	}
	
	public Map<PublicationXref, Resource> getPubXrefs() {
		return pubXrefs;
	}

	public Map<PathwayElement, Resource> getPathwayElements() {
		return pathwayElements;
	}

	public Pathway getPathway() {
		return pathway;
	}

	public Resource getPathwayRes() {
		return pathwayRes;
	}
}
