package org.wikipathways.wp2rdf.utils;

import java.util.HashMap;
import java.util.Map;

import org.bridgedb.Xref;
import org.pathvisio.core.biopax.PublicationXref;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;

import com.hp.hpl.jena.rdf.model.Resource;

public class DataHandlerWp {
	private Map<PathwayElement, Resource> pathwayElements;
	private Map<PublicationXref, Resource> pubXrefs;
	
	private Map<Xref, Resource> dataNodes;

	private Pathway pathway;
	private Resource pathwayRes;
	private String pwyId;
	private String revision;

	public DataHandlerWp(Pathway p, String pwyId, String revision, Resource pathwayRes) {
		pathway = p;
		this.pathwayRes = pathwayRes;
		this.pwyId = pwyId;
		this.revision = revision;
		pathwayElements = new HashMap<PathwayElement, Resource>();
		pubXrefs = new HashMap<PublicationXref, Resource>();
		dataNodes = new HashMap<Xref, Resource>();
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

	public String getPwyId() {
		return pwyId;
	}

	public String getRevision() {
		return revision;
	}

	public Map<Xref, Resource> getDataNodes() {
		return dataNodes;
	}
}
