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
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.model.PathwayElement.Comment;
import org.wikipathways.wp2rdf.ontologies.Gpml;
import org.wikipathways.wp2rdf.utils.DataHandlerGpml;
import org.wikipathways.wp2rdf.utils.Utils;

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
public class ShapeConverter {

	/**
	 * conversion only GPML vocabulary
	 */
	public static void parseShapeGpml(PathwayElement elem, Model model, DataHandlerGpml data) {
	
		Resource shapeRes = model.createResource(data.getPathwayRes().getURI() + "/Shape/" + elem.getGraphId());

		shapeRes.addProperty(RDF.type, Gpml.SHAPE);
		data.getPathwayRes().addProperty(Gpml.HAS_SHAPE, shapeRes);
		shapeRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
		
		shapeRes.addLiteral(Gpml.FONT_STYLE, elem.isItalic() ? "Italic" : "Normal");
		shapeRes.addLiteral(Gpml.LINE_THICKNESS, elem.getLineThickness());
		shapeRes.addLiteral(Gpml.FONT_SIZE, elem.getMFontSize());
		shapeRes.addLiteral(Gpml.FONT_NAME, elem.getFontName());
		shapeRes.addLiteral(Gpml.ALIGN, elem.getAlign().getGpmlName());
		shapeRes.addLiteral(Gpml.GRAPH_ID, elem.getGraphId());
		if(elem.getGroupRef() != null) shapeRes.addLiteral(Gpml.GROUP_REF, elem.getGroupRef());
		shapeRes.addLiteral(Gpml.COLOR, Utils.colorToHex(elem.getColor()));
		shapeRes.addLiteral(Gpml.CENTER_Y, elem.getMCenterY());
		shapeRes.addLiteral(Gpml.VALIGN, elem.getValign().getGpmlName());
		shapeRes.addLiteral(Gpml.FONT_WEIGHT, elem.isBold() ? "Bold" : "Normal");
		shapeRes.addLiteral(Gpml.FONT_DECORATION, elem.isUnderline() ? "Underline" : "Normal");
		shapeRes.addLiteral(Gpml.FONT_STRIKETHRU, elem.isStrikethru() ? "Strikethru" : "Normal");
		shapeRes.addLiteral(Gpml.HEIGHT, elem.getMHeight());
		shapeRes.addLiteral(Gpml.LINE_STYLE, elem.getLineStyle() != LineStyle.DASHED ? "Solid" : "Broken");
		shapeRes.addLiteral(Gpml.CENTER_X, elem.getMCenterX());
		shapeRes.addLiteral(Gpml.TEXTLABEL, elem.getTextLabel());
		shapeRes.addLiteral(Gpml.WIDTH, elem.getMWidth());
		if(elem.getFillColor() != null) shapeRes.addLiteral(Gpml.FILL_COLOR, Utils.colorToHex(elem.getFillColor()));
		shapeRes.addLiteral(Gpml.ZORDER, elem.getZOrder());
		if (elem.getShapeType() != null) shapeRes.addLiteral(Gpml.SHAPE_TYPE, elem.getShapeType().getName());
		shapeRes.addLiteral(Gpml.SHAPE, elem.getRotation());
		
		for(String s : elem.getBiopaxRefs()) {
			shapeRes.addLiteral(Gpml.BIOPAX_REF, s);
		}
		
		for(Comment c : elem.getComments()) {
			CommentConverter.parseCommentGpml(c, model, shapeRes, data);
		}
		
		for(PublicationXref xref : elem.getBiopaxReferenceManager().getPublicationXRefs()) {
			PublicationXrefConverter.parsePublicationXrefGpml(xref, shapeRes, model, data);
		}

		data.getPathwayElements().put(elem, shapeRes);
		
	}
}
