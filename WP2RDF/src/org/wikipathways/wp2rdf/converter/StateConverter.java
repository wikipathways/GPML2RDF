package org.wikipathways.wp2rdf.converter;

import org.pathvisio.core.biopax.PublicationXref;
import org.pathvisio.core.model.PathwayElement;
import org.wikipathways.wp2rdf.ontologies.Gpml;
import org.wikipathways.wp2rdf.utils.DataStorage;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class StateConverter {

	public static void parseState(PathwayElement elem, Model model, DataStorage data) {
		Resource stateRes = model.createResource(data.getPathwayRes().getURI() + "/State/" + elem.getGraphId());
		stateRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
		stateRes.addProperty(RDF.type, Gpml.State);
		
		if(!elem.getTextLabel().equals("")) {
			stateRes.addLiteral(RDFS.label, model.createLiteral(elem.getTextLabel().replace("\n", " "), "en"));
		}
		
		// GPML RELATED PROPERTIES
		stateRes.addLiteral(Gpml.graphid, elem.getGraphId());
		if(elem.getGraphRef() != null) {
			stateRes.addLiteral(Gpml.graphref, elem.getGraphRef());
			Resource parent = data.getPathwayElements().get(data.getPathway().getElementById(elem.getGraphRef()));
			parent.addProperty(Gpml.hasState, stateRes);
		}

		if(elem.getFillColor() != null) stateRes.addLiteral(Gpml.fillcolor, elem.getFillColor().getRGB());
		stateRes.addLiteral(Gpml.relX, elem.getRelX());
		stateRes.addLiteral(Gpml.relY, elem.getRelY());
		stateRes.addLiteral(Gpml.width, elem.getMWidth());
		stateRes.addLiteral(Gpml.height, elem.getMHeight());
		stateRes.addLiteral(Gpml.zorder, elem.getZOrder());
		
		stateRes.addLiteral(Gpml.shapetype, elem.getShapeType().getName());
		
		// TODO: add state type implementation in PV and WP!!!
		
		// PUBLICATION REFERENCES
		for(PublicationXref xref : elem.getBiopaxReferenceManager().getPublicationXRefs()) {
			PublicationXrefConverter.parsePublicationXref(xref, stateRes, model);
		}
		
		// XREFS
		if(elem.getXref() != null && !elem.getXref().getId().equals("") && elem.getXref().getDataSource() != null) {
			// TODO: fix issue with some of the identifiers.org URIs
			Resource idRes = model.createResource(elem.getXref().getDataSource().getIdentifiersOrgUri(elem.getXref().getId()));
			stateRes.addProperty(DC.identifier, idRes);
			stateRes.addLiteral(DCTerms.identifier, elem.getXref().getId());
			// TODO: why not use DCTerms for source as well?
			stateRes.addLiteral(DC.source, elem.getXref().getDataSource().getFullName());
		}
		
		// TODO: ShapeStyleAttributes
		// TODO: Comments
		
		data.getPathwayElements().put(elem, stateRes);
	}
}
