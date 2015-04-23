package org.wikipathways.wp2rdf.converter;

import org.pathvisio.core.biopax.PublicationXref;
import org.pathvisio.core.model.MGroup;
import org.pathvisio.core.model.PathwayElement;
import org.wikipathways.wp2rdf.ontologies.Gpml;
import org.wikipathways.wp2rdf.ontologies.Wp;
import org.wikipathways.wp2rdf.utils.DataStorage;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class GroupConverter {

	public static void parseGroup(MGroup group, Model model, DataStorage data) {
		
		// TODO: should this contain the groupId or graphId?
		Resource groupRes = model.createResource(data.getPathwayRes().getURI() + "/Group/" + group.getGroupId());
		groupRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
		groupRes.addProperty(RDF.type, Gpml.Group);
		
		// GPML RELATED PROPERTIES
		// TODO: fix this in WP!!!!
		if(group.getGraphId() != null) groupRes.addLiteral(Gpml.graphid, group.getGraphId());
		groupRes.addLiteral(Gpml.groupid, group.getGroupId());
		if(group.getGroupRef() != null) {
			groupRes.addLiteral(Gpml.groupref, group.getGroupRef());
		}
		groupRes.addLiteral(Gpml.style, group.getGroupStyle().getName());
		if(!group.getTextLabel().equals("")) {
			groupRes.addLiteral(RDFS.label, group.getTextLabel().replace("\n", " "));
		}

		for(PathwayElement e : group.getGroupElements()) {
			Resource res = data.getPathwayElements().get(e);
			if(res != null) {
				res.addProperty(DCTerms.isPartOf, groupRes);
				// TODO: adding a hasParticipants or so, might be nice for the groups
				groupRes.addProperty(Wp.hasParticipant, res);
			} else {
				System.out.println("ERRRORR");
			}
		}
		// TODO: Comments
		
		// PUBLICATION REFERENCES
		for(PublicationXref xref : group.getBiopaxReferenceManager().getPublicationXRefs()) {
			PublicationXrefConverter.parsePublicationXref(xref, groupRes, model);
		}
		
		data.getPathwayElements().put(group, groupRes);
	}
}
