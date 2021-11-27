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

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.pathvisio.core.model.PathwayElement.MAnchor;
import org.wikipathways.wp2rdf.ontologies.Gpml;
import org.wikipathways.wp2rdf.utils.DataHandlerGpml;

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
	public static void parseAnchorGpml(MAnchor anchor, Model model, Resource lineRes, DataHandlerGpml data) {
		Resource anchorRes = model.createResource(lineRes.getURI() + "/Anchor/" + anchor.getGraphId());

		anchorRes.addProperty(RDF.type, Gpml.ANCHOR);
		anchorRes.addProperty(DCTerms.isPartOf, lineRes);
		anchorRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
		
		lineRes.addProperty(Gpml.HAS_ANCHOR, anchorRes);
		
		anchorRes.addLiteral(Gpml.GRAPH_ID, anchor.getGraphId());
		anchorRes.addLiteral(Gpml.POSITION, anchor.getPosition());
		anchorRes.addLiteral(Gpml.SHAPE, anchor.getShape().getName());
		
		data.getAnchors().put(anchor, anchorRes);
	}
}
