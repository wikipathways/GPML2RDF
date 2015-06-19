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
public class StateConverter {

	/**
	 * conversion only GPML vocabulary
	 */
	public static void parseStateGpml(PathwayElement elem, Model model, DataHandlerGpml data) {
		Resource stateRes = model.createResource(data.getPathwayRes().getURI() + "/State/" + elem.getGraphId());
		
		stateRes.addProperty(RDF.type, GpmlNew.STATE);
		
		data.getPathwayRes().addProperty(GpmlNew.HAS_STATE, stateRes);
		stateRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
		
		stateRes.addLiteral(GpmlNew.LINE_THICKNESS, elem.getLineThickness());
		if (elem.getGraphId() != null)
			stateRes.addLiteral(GpmlNew.GRAPH_ID, elem.getGraphId());
		stateRes.addLiteral(GpmlNew.COLOR, Utils.colorToHex(elem.getColor()));
		stateRes.addLiteral(GpmlNew.HEIGHT, elem.getMHeight());
		stateRes.addLiteral(GpmlNew.LINE_STYLE, elem.getLineStyle() != LineStyle.DASHED ? "Solid" : "Broken");
		stateRes.addLiteral(GpmlNew.GRAPH_REF, elem.getGraphRef());
		stateRes.addLiteral(GpmlNew.TEXTLABEL, elem.getTextLabel());
		stateRes.addLiteral(GpmlNew.REL_X, elem.getRelX());
		stateRes.addLiteral(GpmlNew.REL_Y, elem.getRelY());
		stateRes.addLiteral(GpmlNew.WIDTH, elem.getMWidth());
		stateRes.addLiteral(GpmlNew.FILL_COLOR, Utils.colorToHex(elem.getFillColor()));
		stateRes.addLiteral(GpmlNew.ZORDER, elem.getZOrder());
		if (elem.getShapeType() != null)
			stateRes.addLiteral(GpmlNew.SHAPE_TYPE, elem.getShapeType().getName());
		stateRes.addLiteral(GpmlNew.STATE_TYPE, elem.getDataNodeType());

		if(elem.getXref() != null && elem.getXref().getId() != null && elem.getXref().getDataSource() != null) {
			stateRes.addLiteral(GpmlNew.XREF_ID, elem.getXref().getId());
			stateRes.addLiteral(GpmlNew.XREF_DATASOURCE, elem.getXref().getDataSource().getFullName());
		}
		
		for(String s : elem.getBiopaxRefs()) {
			stateRes.addLiteral(GpmlNew.BIOPAX_REF, s);
		}
		
		for(Comment c : elem.getComments()) {
			CommentConverter.parseCommentGpml(c, model, stateRes, data);
		}
		
		for(PublicationXref xref : elem.getBiopaxReferenceManager().getPublicationXRefs()) {
			PublicationXrefConverter.parsePublicationXrefGpml(xref, stateRes, model, data);
		}
	
		data.getPathwayElements().put(elem, stateRes);
	}
	
	/**
	 * old conversion GPML + WP
	 */
	public static void parseState(PathwayElement elem, Model model, DataHandlerGpml data) {
		Resource stateRes = model.createResource(data.getPathwayRes().getURI() + "/State/" + elem.getGraphId());
		stateRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
		stateRes.addProperty(RDF.type, Gpml.State);
		
		if(!elem.getTextLabel().equals("")) {
			stateRes.addLiteral(RDFS.label, model.createLiteral(elem.getTextLabel().replace("\n", " "), "en"));
		}
		
		// GPML RELATED PROPERTIES
		stateRes.addLiteral(Gpml.graphid, elem.getGraphId());
		if(elem.getGraphRef() != null) {
			stateRes.addLiteral(Gpml.graphref, elem.getGraphRef());
			Resource parent = data.getPathwayElements().get(data.getPathway().getElementById(elem.getGraphRef()));
			parent.addProperty(Gpml.hasState, stateRes);
		}

		if(elem.getFillColor() != null) stateRes.addLiteral(Gpml.fillcolor, elem.getFillColor().getRGB());
		stateRes.addLiteral(Gpml.relX, elem.getRelX());
		stateRes.addLiteral(Gpml.relY, elem.getRelY());
		stateRes.addLiteral(Gpml.width, elem.getMWidth());
		stateRes.addLiteral(Gpml.height, elem.getMHeight());
		stateRes.addLiteral(Gpml.zorder, elem.getZOrder());
		
		stateRes.addLiteral(Gpml.shapetype, elem.getShapeType().getName());
		
		// TODO: add state type implementation in PV and WP!!!
		
		// PUBLICATION REFERENCES
		for(PublicationXref xref : elem.getBiopaxReferenceManager().getPublicationXRefs()) {
			PublicationXrefConverter.parsePublicationXref(xref, stateRes, model);
		}
		
		// XREFS
		if(elem.getXref() != null && !elem.getXref().getId().equals("") && elem.getXref().getDataSource() != null) {
			// TODO: fix issue with some of the identifiers.org URIs
			Resource idRes = model.createResource(elem.getXref().getDataSource().getIdentifiersOrgUri(elem.getXref().getId()));
			stateRes.addProperty(DC.identifier, idRes);
			stateRes.addLiteral(DCTerms.identifier, elem.getXref().getId());
			// TODO: why not use DCTerms for source as well?
			stateRes.addLiteral(DC.source, elem.getXref().getDataSource().getFullName());
		}
		
		// TODO: ShapeStyleAttributes
		// TODO: Comments
		
		data.getPathwayElements().put(elem, stateRes);
	}
}
