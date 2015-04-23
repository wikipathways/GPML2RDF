// WP2RDF
// Conversion from GPML pathways to RDF
// Copyright 2015 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.wikipathways.wp2rdf.converter;

import org.pathvisio.core.biopax.PublicationXref;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.model.PathwayElement.MAnchor;
import org.pathvisio.core.model.PathwayElement.MPoint;
import org.wikipathways.wp2rdf.ontologies.Gpml;
import org.wikipathways.wp2rdf.utils.DataStorage;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * 
 * @author mkutmon
 * @author ryanmiller
 *
 */
public class LineConverter {

	public static void parseLineGpml(PathwayElement e, Model model, DataStorage data) {
		
		Resource lineRes = model.createResource(data.getPathwayRes().getURI() + "/Line/" + e.getGraphId());
		lineRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
		lineRes.addProperty(RDF.type, Gpml.Interaction);
		
		lineRes.addLiteral(Gpml.graphid, e.getGraphId());
		lineRes.addLiteral(Gpml.color, e.getColor());
		lineRes.addLiteral(Gpml.linethickness, e.getLineThickness());
		lineRes.addLiteral(Gpml.linestyle, e.getLineStyle());
		// TODO: add connector type to GPML vocabulary
		lineRes.addLiteral(Gpml.zorder, e.getZOrder());
		
		// PUBLICATION REFERENCES
		for(PublicationXref xref : e.getBiopaxReferenceManager().getPublicationXRefs()) {
			PublicationXrefConverter.parsePublicationXref(xref, lineRes, model);
		}
		
		if(e.getXref() != null && !e.getXref().getId().equals("") && e.getXref().getDataSource() != null) {
			// TODO: fix issue with some of the identifiers.org URIs
			Resource idRes = model.createResource(e.getXref().getDataSource().getIdentifiersOrgUri(e.getXref().getId()));
			lineRes.addProperty(DC.identifier, idRes);
			lineRes.addLiteral(DCTerms.identifier, e.getXref().getId());
			// TODO: why not use DCTerms for source as well?
			lineRes.addLiteral(DC.source, e.getXref().getDataSource().getFullName());
		}
		
		MPoint start = e.getMStart();
		PointConverter.parsePoint(start, e.getStartLineType(), data.getPathway().getUniqueGraphId(), model, lineRes, data);
		// TODO: how about a hasPoint predicate??
		
		MPoint end = e.getMEnd();
		PointConverter.parsePoint(end, e.getEndLineType(), data.getPathway().getUniqueGraphId(), model, lineRes, data);
		// TODO: how about a hasPoint predicate??
		
		for(MAnchor a : e.getMAnchors()) {
			Resource anchorRes = AnchorConverter.parseAnchor(a, model, lineRes, data);
			lineRes.addProperty(Gpml.hasAnchor, anchorRes);
		}
		
		data.getPathwayElements().put(e, lineRes);
	}
}
