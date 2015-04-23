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
import org.wikipathways.wp2rdf.ontologies.Gpml;
import org.wikipathways.wp2rdf.utils.DataStorage;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * 
 * @author mkutmon
 * @author ryanmiller
 *
 */
public class LabelConverter {
	
	public static void parseLabel(PathwayElement elem, Model model, DataStorage data) {
		// TODO: currently this is called GpmlLabel, why??
		Resource labelRes = model.createResource(data.getPathwayRes().getURI() + "/Label/" + elem.getGraphId());
		labelRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
		labelRes.addProperty(RDF.type, Gpml.Label);
		labelRes.addLiteral(RDFS.label, model.createLiteral(elem.getTextLabel().replace("\n", " "), "en"));
		
		// GPML RELATED PROPERTIES
		labelRes.addLiteral(Gpml.graphid, elem.getGraphId());
		labelRes.addLiteral(Gpml.height, elem.getMHeight());
		labelRes.addLiteral(Gpml.width, elem.getMWidth());
		labelRes.addLiteral(Gpml.zorder, elem.getZOrder());
		labelRes.addLiteral(Gpml.centerx, elem.getMCenterX());
		labelRes.addLiteral(Gpml.centery, elem.getMCenterY());
		
		// TODO: Add all font related with a central font resource
		
		// PUBLICATION REFERENCES
		for(PublicationXref xref : elem.getBiopaxReferenceManager().getPublicationXRefs()) {
			PublicationXrefConverter.parsePublicationXref(xref, labelRes, model);
		}
		
		data.getPathwayElements().put(elem, labelRes);
	}

}
