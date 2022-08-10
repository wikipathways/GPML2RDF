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
import org.pathvisio.core.biopax.PublicationXref;
import org.pathvisio.core.model.LineStyle;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.model.PathwayElement.Comment;
import org.wikipathways.wp2rdf.ontologies.Gpml;
import org.wikipathways.wp2rdf.utils.DataHandlerGpml;
import org.wikipathways.wp2rdf.utils.Utils;

/**
 * 
 * @author mkutmon
 * @author ryanmiller
 *
 */
public class StateConverter {

	/**
	 * conversion only GPML vocabulary
	 */
	public static void parseStateGpml(PathwayElement elem, Model model, DataHandlerGpml data) {
		Resource stateRes = model.createResource(data.getPathwayRes().getURI() + "/State/" + elem.getGraphId());
		
		stateRes.addProperty(RDF.type, Gpml.STATE);
		
		data.getPathwayRes().addProperty(Gpml.HAS_STATE, stateRes);
		stateRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
		
		stateRes.addLiteral(Gpml.LINE_THICKNESS, elem.getLineThickness());
		if (elem.getGraphId() != null)
			stateRes.addLiteral(Gpml.GRAPH_ID, elem.getGraphId());
		stateRes.addLiteral(Gpml.COLOR, Utils.colorToHex(elem.getColor()));
		stateRes.addLiteral(Gpml.HEIGHT, elem.getMHeight());
		stateRes.addLiteral(Gpml.LINE_STYLE, elem.getLineStyle() != LineStyle.DASHED ? "Solid" : "Broken");
		if (elem.getGraphRef() != null) {
		    stateRes.addLiteral(Gpml.GRAPH_REF, elem.getGraphRef());
		    stateRes.addProperty(Gpml.STATE_OF, model.createResource(data.getPathwayRes().getURI() + "/DataNode/" + elem.getGraphRef()));
		}
		stateRes.addLiteral(Gpml.TEXTLABEL, elem.getTextLabel());
		stateRes.addLiteral(Gpml.REL_X, elem.getRelX());
		stateRes.addLiteral(Gpml.REL_Y, elem.getRelY());
		stateRes.addLiteral(Gpml.WIDTH, elem.getMWidth());
		stateRes.addLiteral(Gpml.FILL_COLOR, Utils.colorToHex(elem.getFillColor()));
		stateRes.addLiteral(Gpml.ZORDER, elem.getZOrder());
		if (elem.getShapeType() != null)
			stateRes.addLiteral(Gpml.SHAPE_TYPE, elem.getShapeType().getName());
		stateRes.addLiteral(Gpml.STATE_TYPE, elem.getDataNodeType());

		if(elem.getXref() != null && elem.getXref().getId() != null && elem.getXref().getDataSource() != null) {
			stateRes.addLiteral(Gpml.XREF_ID, elem.getXref().getId());
			stateRes.addLiteral(Gpml.XREF_DATASOURCE, elem.getXref().getDataSource().getFullName());
		}
		
		for(String s : elem.getBiopaxRefs()) {
			stateRes.addLiteral(Gpml.BIOPAX_REF, s);
		}
		
		for(Comment c : elem.getComments()) {
			CommentConverter.parseCommentGpml(c, model, stateRes, data);
		}
		
		for(PublicationXref xref : elem.getBiopaxReferenceManager().getPublicationXRefs()) {
			PublicationXrefConverter.parsePublicationXrefGpml(xref, stateRes, model, data);
		}
	
		data.getPathwayElements().put(elem, stateRes);
	}
}
