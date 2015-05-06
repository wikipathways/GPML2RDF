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
import org.wikipathways.wp2rdf.ontologies.GpmlNew;
import org.wikipathways.wp2rdf.utils.DataHandler;
import org.wikipathways.wp2rdf.utils.Utils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
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
	
	/**
	 * conversion only GPML vocabulary
	 */
	public static void parseLabelGpml(PathwayElement elem, Model model, DataHandler data) {
		Resource labelRes = model.createResource(data.getPathwayRes().getURI() + "/Label/" + elem.getGraphId());

		labelRes.addProperty(DC.type, GpmlNew.LABEL);
		data.getPathwayRes().addProperty(GpmlNew.HAS_LABEL, labelRes);
		labelRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());		
		
		labelRes.addLiteral(GpmlNew.FONT_STYLE, elem.isItalic() ? "Italic" : "Normal");
		labelRes.addLiteral(GpmlNew.LINE_THICKNESS, elem.getLineThickness());
		labelRes.addLiteral(GpmlNew.FONT_SIZE, elem.getMFontSize());
		labelRes.addLiteral(GpmlNew.FONT_NAME, elem.getFontName());
		labelRes.addLiteral(GpmlNew.ALIGN, elem.getAlign().getGpmlName());
		labelRes.addLiteral(GpmlNew.GRAPH_ID, elem.getGraphId());
		if(elem.getGroupRef() != null) labelRes.addLiteral(GpmlNew.GROUP_REF, elem.getGroupRef());
		labelRes.addLiteral(GpmlNew.COLOR, Utils.colorToHex(elem.getColor()));
		labelRes.addLiteral(GpmlNew.CENTER_Y, elem.getMCenterY());
		labelRes.addLiteral(GpmlNew.VALIGN, elem.getValign().getGpmlName());
		labelRes.addLiteral(GpmlNew.FONT_WEIGHT, elem.isBold() ? "Bold" : "Normal");
		labelRes.addLiteral(GpmlNew.FONT_DECORATION, elem.isUnderline() ? "Underline" : "Normal");
		labelRes.addLiteral(GpmlNew.FONT_STRIKETHRU, elem.isStrikethru() ? "Strikethru" : "Normal");
		labelRes.addLiteral(GpmlNew.HEIGHT, elem.getMHeight());
		if(elem.getHref() != null) labelRes.addLiteral(GpmlNew.HREF, elem.getHref());
		labelRes.addLiteral(GpmlNew.LINE_STYLE, elem.getLineStyle() != LineStyle.DASHED ? "Solid" : "Broken");
		labelRes.addLiteral(GpmlNew.CENTER_X, elem.getMCenterX());
		labelRes.addLiteral(GpmlNew.TEXTLABEL, elem.getTextLabel());
		labelRes.addLiteral(GpmlNew.WIDTH, elem.getMWidth());
		labelRes.addLiteral(GpmlNew.FILL_COLOR, Utils.colorToHex(elem.getFillColor()));
		labelRes.addLiteral(GpmlNew.ZORDER, elem.getZOrder());
		labelRes.addLiteral(GpmlNew.SHAPE_TYPE, elem.getShapeType().getName());
		
		for(String s : elem.getBiopaxRefs()) {
			labelRes.addLiteral(GpmlNew.BIOPAX_REF, s);
		}
		
		for(Comment c : elem.getComments()) {
			CommentConverter.parseCommentGpml(c, model, labelRes, data);
		}
		
		for(PublicationXref xref : elem.getBiopaxReferenceManager().getPublicationXRefs()) {
			PublicationXrefConverter.parsePublicationXrefGpml(xref, labelRes, model, data);
		}
		
		data.getPathwayElements().put(elem, labelRes);
	}
	
	/**
	 * old conversion GPML + WP
	 */
	public static void parseLabel(PathwayElement elem, Model model, DataHandler data) {
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
