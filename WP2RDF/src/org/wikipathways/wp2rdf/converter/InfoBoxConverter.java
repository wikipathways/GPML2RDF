package org.wikipathways.wp2rdf.converter;

import org.pathvisio.core.model.PathwayElement;
import org.wikipathways.wp2rdf.ontologies.GpmlNew;
import org.wikipathways.wp2rdf.utils.DataStorage;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;

public class InfoBoxConverter {

	
	public static void parseInfoBoxGpml(PathwayElement e, Model model, DataStorage data) {
		Resource infoboxRes = model.createResource(data.getPathwayRes().getURI() + "/InfoBox/" + e.getGraphId());

		infoboxRes.addProperty(RDF.type, GpmlNew.INFO_BOX);
		infoboxRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());		
		data.getPathwayRes().addProperty(GpmlNew.HAS_INFO_BOX, infoboxRes);
		
		infoboxRes.addLiteral(GpmlNew.CENTER_X, e.getMCenterX());
		infoboxRes.addLiteral(GpmlNew.CENTER_Y, e.getMCenterY());
		
		data.getPathwayElements().put(e, infoboxRes);
	}
}
