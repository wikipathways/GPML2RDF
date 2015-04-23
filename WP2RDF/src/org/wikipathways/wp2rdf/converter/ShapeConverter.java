package org.wikipathways.wp2rdf.converter;

import org.pathvisio.core.biopax.PublicationXref;
import org.pathvisio.core.model.PathwayElement;
import org.wikipathways.wp2rdf.ontologies.Gpml;
import org.wikipathways.wp2rdf.utils.DataStorage;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ShapeConverter {

	public static void parseShape(PathwayElement elem, Model model, DataStorage data) {
		// TODO: currently this is called GpmlLabel, why??
		Resource shapeRes = model.createResource(data.getPathwayRes().getURI() + "/Shape/" + elem.getGraphId());
		shapeRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
		shapeRes.addProperty(RDF.type, Gpml.Shape);
		
		if(!elem.getTextLabel().equals("")) {
			shapeRes.addLiteral(RDFS.label, model.createLiteral(elem.getTextLabel().replace("\n", " "), "en"));
		}
		
		// GPML RELATED PROPERTIES
		shapeRes.addLiteral(Gpml.graphid, elem.getGraphId());
		
		// TODO: missing predicate "rotation"
		shapeRes.addLiteral(Gpml.rotation, elem.getRotation());
		
		// TODO: missing predicate "shapetype"
		shapeRes.addLiteral(Gpml.shapetype, elem.getShapeType().getName());

		if(elem.getFillColor() != null) {
			shapeRes.addLiteral(Gpml.fillcolor, elem.getFillColor().getRGB());
		}
		shapeRes.addLiteral(Gpml.zorder, elem.getZOrder());
		
		// TODO: Add all font related with a central font resource
		
		// PUBLICATION REFERENCES
		for(PublicationXref xref : elem.getBiopaxReferenceManager().getPublicationXRefs()) {
			PublicationXrefConverter.parsePublicationXref(xref, shapeRes, model);
		}
		
		// TODO: RectAttributes
		// TODO: FontAttributes
		// TODO: ShapeStyleAttributes
		
		// TODO: Comments
		
		data.getPathwayElements().put(elem, shapeRes);
	}
}
