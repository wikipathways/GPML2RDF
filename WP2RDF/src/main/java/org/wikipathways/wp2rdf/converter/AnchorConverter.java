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

import org.pathvisio.core.model.PathwayElement.MAnchor;
import org.wikipathways.wp2rdf.ontologies.Gpml;
import org.wikipathways.wp2rdf.ontologies.GpmlNew;
import org.wikipathways.wp2rdf.utils.DataHandler;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * 
 * @author mkutmon
 * @author ryanmiller
 *
 */
public class AnchorConverter {

	/**
	 * conversion only GPML vocabulary
	 */
	public static void parseAnchorGpml(MAnchor anchor, Model model, Resource lineRes, DataHandler data) {
		Resource anchorRes = model.createResource(lineRes.getURI() + "/Anchor/" + anchor.getGraphId());

		anchorRes.addProperty(RDF.type, GpmlNew.ANCHOR);
		anchorRes.addProperty(DCTerms.isPartOf, lineRes);
		anchorRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
		
		lineRes.addProperty(GpmlNew.HAS_ANCHOR, anchorRes);
		
		anchorRes.addLiteral(GpmlNew.GRAPH_ID, anchor.getGraphId());
		anchorRes.addLiteral(GpmlNew.POSITION, anchor.getPosition());
		anchorRes.addLiteral(GpmlNew.SHAPE, anchor.getShape().getName());
		
		data.getAnchors().put(anchor, anchorRes);
	}

	/**
	 * old conversion
	 */
	public static Resource parseAnchor(MAnchor anchor, Model model, Resource lineRes, DataHandler data) {
		// TODO: currently the URI is depending on the line - this is not the case for the Point, so I wouldn't do it for Anchors either
		Resource anchorRes = model.createResource(lineRes.getURI() + "/Anchor/" + anchor.getGraphId());
		
		// TODO: shouldn't this be part of the pathway as well?
		// TODO: anchors actually don't even link to the lines currently. we added this isPartOf
		anchorRes.addProperty(DCTerms.isPartOf, lineRes);
		anchorRes.addProperty(RDF.type, Gpml.Anchor);
		
		// GPML RELATED PROPERTIES
		anchorRes.addLiteral(Gpml.anchorPosition, anchor.getPosition());
		anchorRes.addLiteral(Gpml.graphid, anchor.getGraphId());
		anchorRes.addLiteral(Gpml.anchorShape, anchor.getShape());
		return anchorRes;
	}
}
