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
import org.pathvisio.core.model.LineStyle;
import org.pathvisio.core.model.MLine;
import org.pathvisio.core.model.PathwayElement.Comment;
import org.pathvisio.core.model.PathwayElement.MAnchor;
import org.pathvisio.core.model.PathwayElement.MPoint;
import org.wikipathways.wp2rdf.ontologies.GpmlNew;
import org.wikipathways.wp2rdf.utils.DataHandler;
import org.wikipathways.wp2rdf.utils.Utils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * 
 * @author mkutmon
 * @author ryanmiller
 *
 */
public class GraphicalLineConverter {

	/**
	 * conversion only GPML vocabulary
	 */
	public static void parseInteractionGpml(MLine e, Model model, DataHandler data) {
		Resource intRes = model.createResource(data.getPathwayRes().getURI() + "/GraphicalLine/" + e.getGraphId());
		
		intRes.addProperty(DC.type, GpmlNew.GRAPHICAL_LINE);
		data.getPathwayRes().addProperty(GpmlNew.HAS_GRAPHICAL_LINE, intRes);
		intRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
		
		intRes.addLiteral(GpmlNew.LINE_THICKNESS, e.getLineThickness());
		intRes.addLiteral(GpmlNew.GRAPH_ID, e.getGraphId());
		intRes.addLiteral(GpmlNew.COLOR, Utils.colorToHex(e.getColor()));
		intRes.addLiteral(GpmlNew.LINE_STYLE, e.getLineStyle() != LineStyle.DASHED ? "Solid" : "Broken");
		intRes.addLiteral(GpmlNew.ZORDER, e.getZOrder());
		intRes.addLiteral(GpmlNew.CONNECTOR_TYPE, e.getConnectorType().getName());
		
		if(e.getGroupRef() != null) intRes.addLiteral(GpmlNew.GROUP_REF, e.getGroupRef());
		
		// TODO: in schema there is an interaction type but that's not in the data model. 
		
		for(MAnchor a : e.getMAnchors()) {
			AnchorConverter.parseAnchorGpml(a, model, intRes, data);
		}
		
		for(MPoint p : e.getMPoints()) {
			if(p.equals(e.getStartPoint())) {
				PointConverter.parsePointGpml(p, model, intRes, data, e.getStartLineType().getName());
			} else if (p.equals(e.getEndPoint())) {
				PointConverter.parsePointGpml(p, model, intRes, data, e.getEndLineType().getName());
			} else {
				PointConverter.parsePointGpml(p, model, intRes, data, null);
			}
		}

		for(String s : e.getBiopaxRefs()) {
			intRes.addLiteral(GpmlNew.BIOPAX_REF, s);
		}
		
		for(Comment c : e.getComments()) {
			CommentConverter.parseCommentGpml(c, model, intRes, data);
		}
		
		for(PublicationXref xref : e.getBiopaxReferenceManager().getPublicationXRefs()) {
			PublicationXrefConverter.parsePublicationXrefGpml(xref, intRes, model, data);
		}
			
		data.getPathwayElements().put(e, intRes);
	}
	
}
