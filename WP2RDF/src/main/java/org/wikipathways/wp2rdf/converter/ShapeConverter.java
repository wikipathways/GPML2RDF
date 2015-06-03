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
import org.wikipathways.wp2rdf.utils.DataHandlerGpml;
import org.wikipathways.wp2rdf.utils.Utils;

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
public class ShapeConverter {

	/**
	 * conversion only GPML vocabulary
	 */
	public static void parseShapeGpml(PathwayElement elem, Model model, DataHandlerGpml data) {
	
		Resource shapeRes = model.createResource(data.getPathwayRes().getURI() + "/Shape/" + elem.getGraphId());

		shapeRes.addProperty(RDF.type, GpmlNew.SHAPE);
		data.getPathwayRes().addProperty(GpmlNew.HAS_SHAPE, shapeRes);
		shapeRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
		
		shapeRes.addLiteral(GpmlNew.FONT_STYLE, elem.isItalic() ? "Italic" : "Normal");
		shapeRes.addLiteral(GpmlNew.LINE_THICKNESS, elem.getLineThickness());
		shapeRes.addLiteral(GpmlNew.FONT_SIZE, elem.getMFontSize());
		shapeRes.addLiteral(GpmlNew.FONT_NAME, elem.getFontName());
		shapeRes.addLiteral(GpmlNew.ALIGN, elem.getAlign().getGpmlName());
		shapeRes.addLiteral(GpmlNew.GRAPH_ID, elem.getGraphId());
		if(elem.getGroupRef() != null) shapeRes.addLiteral(GpmlNew.GROUP_REF, elem.getGroupRef());
		shapeRes.addLiteral(GpmlNew.COLOR, Utils.colorToHex(elem.getColor()));
		shapeRes.addLiteral(GpmlNew.CENTER_Y, elem.getMCenterY());
		shapeRes.addLiteral(GpmlNew.VALIGN, elem.getValign().getGpmlName());
		shapeRes.addLiteral(GpmlNew.FONT_WEIGHT, elem.isBold() ? "Bold" : "Normal");
		shapeRes.addLiteral(GpmlNew.FONT_DECORATION, elem.isUnderline() ? "Underline" : "Normal");
		shapeRes.addLiteral(GpmlNew.FONT_STRIKETHRU, elem.isStrikethru() ? "Strikethru" : "Normal");
		shapeRes.addLiteral(GpmlNew.HEIGHT, elem.getMHeight());
		shapeRes.addLiteral(GpmlNew.LINE_STYLE, elem.getLineStyle() != LineStyle.DASHED ? "Solid" : "Broken");
		shapeRes.addLiteral(GpmlNew.CENTER_X, elem.getMCenterX());
		shapeRes.addLiteral(GpmlNew.TEXTLABEL, elem.getTextLabel());
		shapeRes.addLiteral(GpmlNew.WIDTH, elem.getMWidth());
		if(elem.getFillColor() != null) shapeRes.addLiteral(GpmlNew.FILL_COLOR, Utils.colorToHex(elem.getFillColor()));
		shapeRes.addLiteral(GpmlNew.ZORDER, elem.getZOrder());
		shapeRes.addLiteral(GpmlNew.SHAPE_TYPE, elem.getShapeType().getName());
		shapeRes.addLiteral(GpmlNew.SHAPE, elem.getRotation());
		
		for(String s : elem.getBiopaxRefs()) {
			shapeRes.addLiteral(GpmlNew.BIOPAX_REF, s);
		}
		
		for(Comment c : elem.getComments()) {
			CommentConverter.parseCommentGpml(c, model, shapeRes, data);
		}
		
		for(PublicationXref xref : elem.getBiopaxReferenceManager().getPublicationXRefs()) {
			PublicationXrefConverter.parsePublicationXrefGpml(xref, shapeRes, model, data);
		}

		data.getPathwayElements().put(elem, shapeRes);
		
	}
	
	/**
	 * old conversion GPML + WP
	 */
	public static void parseShape(PathwayElement elem, Model model, DataHandlerGpml data) {
		// TODO: currently this is called GpmlLabel, why??
		Resource shapeRes = model.createResource(data.getPathwayRes().getURI() + "/Shape/" + elem.getGraphId());
		shapeRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
		shapeRes.addProperty(RDF.type, Gpml.Shape);
		
		if(!elem.getTextLabel().equals("")) {
			shapeRes.addLiteral(RDFS.label, model.createLiteral(elem.getTextLabel().replace("\n", " "), "en"));
		}
		
		// GPML RELATED PROPERTIES
		shapeRes.addLiteral(Gpml.graphid, elem.getGraphId());
		
		// TODO: missing predicate "rotation"
		shapeRes.addLiteral(Gpml.rotation, elem.getRotation());
		
		// TODO: missing predicate "shapetype"
		shapeRes.addLiteral(Gpml.shapetype, elem.getShapeType().getName());

		if(elem.getFillColor() != null) {
			shapeRes.addLiteral(Gpml.fillcolor, elem.getFillColor().getRGB());
		}
		shapeRes.addLiteral(Gpml.zorder, elem.getZOrder());
		
		// TODO: Add all font related with a central font resource
		
		// PUBLICATION REFERENCES
		for(PublicationXref xref : elem.getBiopaxReferenceManager().getPublicationXRefs()) {
			PublicationXrefConverter.parsePublicationXref(xref, shapeRes, model);
		}
		
		// TODO: RectAttributes
		// TODO: FontAttributes
		// TODO: ShapeStyleAttributes
		
		// TODO: Comments
		
		data.getPathwayElements().put(elem, shapeRes);
	}
}
