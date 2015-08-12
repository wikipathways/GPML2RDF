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

import org.pathvisio.core.model.PathwayElement.MPoint;
import org.wikipathways.wp2rdf.ontologies.Gpml;
import org.wikipathways.wp2rdf.utils.DataHandlerGpml;

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
public class PointConverter {

	/**
	 * conversion only GPML vocabulary
	 */
	public static void parsePointGpml(MPoint point, Model model, Resource lineRes, DataHandlerGpml data, String arrowHead) {
		String graphId = point.getGraphId();
		if(graphId == null) {
			graphId = data.getPathway().getUniqueGraphId();
		}
		Resource pointRes = model.createResource(lineRes.getURI() + "/Point/" + graphId);

		pointRes.addProperty(RDF.type, Gpml.POINT);
		pointRes.addProperty(DCTerms.isPartOf, lineRes);
		pointRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
		
		lineRes.addProperty(Gpml.HAS_POINT, pointRes);
		
		// TODO: make sure that every point has a graph id!!!
		if(point.getGraphId() != null) pointRes.addLiteral(Gpml.GRAPH_ID, point.getGraphId());
		if(point.getGraphRef() != null) pointRes.addLiteral(Gpml.GRAPH_REF, point.getGraphRef());
		pointRes.addLiteral(Gpml.REL_X, point.getRelX());
		pointRes.addLiteral(Gpml.REL_Y, point.getRelY());
		pointRes.addLiteral(Gpml.X, point.getX());
		pointRes.addLiteral(Gpml.Y, point.getY());
		
		if(arrowHead != null) pointRes.addLiteral(Gpml.ARROW_HEAD, arrowHead);
		
		data.getPoints().put(point, pointRes);
	}
}
