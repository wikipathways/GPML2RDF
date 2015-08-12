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
public class LabelConverter {
	
	/**
	 * conversion only GPML vocabulary
	 */
	public static void parseLabelGpml(PathwayElement elem, Model model, DataHandlerGpml data) {
		Resource labelRes = model.createResource(data.getPathwayRes().getURI() + "/Label/" + elem.getGraphId());

		labelRes.addProperty(RDF.type, Gpml.LABEL);
		data.getPathwayRes().addProperty(Gpml.HAS_LABEL, labelRes);
		labelRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());		
		
		labelRes.addLiteral(Gpml.FONT_STYLE, elem.isItalic() ? "Italic" : "Normal");
		labelRes.addLiteral(Gpml.LINE_THICKNESS, elem.getLineThickness());
		labelRes.addLiteral(Gpml.FONT_SIZE, elem.getMFontSize());
		labelRes.addLiteral(Gpml.FONT_NAME, elem.getFontName());
		labelRes.addLiteral(Gpml.ALIGN, elem.getAlign().getGpmlName());
		labelRes.addLiteral(Gpml.GRAPH_ID, elem.getGraphId());
		if(elem.getGroupRef() != null) labelRes.addLiteral(Gpml.GROUP_REF, elem.getGroupRef());
		labelRes.addLiteral(Gpml.COLOR, Utils.colorToHex(elem.getColor()));
		labelRes.addLiteral(Gpml.CENTER_Y, elem.getMCenterY());
		labelRes.addLiteral(Gpml.VALIGN, elem.getValign().getGpmlName());
		labelRes.addLiteral(Gpml.FONT_WEIGHT, elem.isBold() ? "Bold" : "Normal");
		labelRes.addLiteral(Gpml.FONT_DECORATION, elem.isUnderline() ? "Underline" : "Normal");
		labelRes.addLiteral(Gpml.FONT_STRIKETHRU, elem.isStrikethru() ? "Strikethru" : "Normal");
		labelRes.addLiteral(Gpml.HEIGHT, elem.getMHeight());
		if(elem.getHref() != null) labelRes.addLiteral(Gpml.HREF, elem.getHref());
		labelRes.addLiteral(Gpml.LINE_STYLE, elem.getLineStyle() != LineStyle.DASHED ? "Solid" : "Broken");
		labelRes.addLiteral(Gpml.CENTER_X, elem.getMCenterX());
		labelRes.addLiteral(Gpml.TEXTLABEL, elem.getTextLabel());
		labelRes.addLiteral(Gpml.WIDTH, elem.getMWidth());
		labelRes.addLiteral(Gpml.FILL_COLOR, Utils.colorToHex(elem.getFillColor()));
		labelRes.addLiteral(Gpml.ZORDER, elem.getZOrder());
		if (elem.getShapeType() != null)
			labelRes.addLiteral(Gpml.SHAPE_TYPE, elem.getShapeType().getName());
		
		for(String s : elem.getBiopaxRefs()) {
			labelRes.addLiteral(Gpml.BIOPAX_REF, s);
		}
		
		for(Comment c : elem.getComments()) {
			CommentConverter.parseCommentGpml(c, model, labelRes, data);
		}
		
		for(PublicationXref xref : elem.getBiopaxReferenceManager().getPublicationXRefs()) {
			PublicationXrefConverter.parsePublicationXrefGpml(xref, labelRes, model, data);
		}
		
		data.getPathwayElements().put(elem, labelRes);
	}
}
