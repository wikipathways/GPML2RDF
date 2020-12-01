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

import java.util.ArrayList;
import java.util.List;

import org.bridgedb.IDMapper;
import org.bridgedb.Xref;
import org.pathvisio.core.biopax.PublicationXref;
import org.pathvisio.core.model.GroupStyle;
import org.pathvisio.core.model.MGroup;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.model.PathwayElement.Comment;
import org.wikipathways.wp2rdf.GpmlConverter;
import org.wikipathways.wp2rdf.ontologies.Gpml;
import org.wikipathways.wp2rdf.ontologies.Wp;
import org.wikipathways.wp2rdf.utils.DataHandlerGpml;
import org.wikipathways.wp2rdf.utils.DataHandlerWp;
import org.wikipathways.wp2rdf.utils.Utils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
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
public class GroupConverter {

	/**
	 * conversion only WP vocabulary
	 * semantic information about a complex group
	 */
	public static void parseComplexWp(MGroup group, Model model, IDMapper  mapper, DataHandlerWp data) {
		PathwayElement embeddedComplexDataNode = null;
		if(group.getGroupStyle().equals(GroupStyle.COMPLEX)) {
			List<Resource> participants = new ArrayList<Resource>();
			for(PathwayElement e : group.getGroupElements()) {
				if (e.getDataNodeType().equals("Complex") ) {
					// if it has a DataNode participant of @Type=Complex
					embeddedComplexDataNode = e;
				} else {
					Resource r = data.getDataNodes().get(e.getXref());
					if(r != null) {
						participants.add(r);
					}
				}
			}
			// TODO: what about complexes with only one data node?
			if(participants.size() > 1) {
				String graphId = group.getGraphId();
				if(graphId == null) {
					graphId = group.getGroupId();
				}
				Resource groupRes = model.createResource(Utils.WP_RDF_URL + "/Pathway/" + data.getPwyId() + "_r" + data.getRevision() + "/Complex/" + graphId);
				groupRes.addProperty(RDF.type, Wp.DataNode);
				groupRes.addProperty(RDF.type, Wp.Complex);
				groupRes.addProperty(Wp.isAbout, model.createResource(Utils.WP_RDF_URL + "/Pathway/" + data.getPwyId() + "_r" + data.getRevision() + "/Group/" + graphId));
				groupRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
				if(group.getTextLabel() != null && !group.getTextLabel().equals("")) groupRes.addLiteral(RDFS.label, group.getTextLabel().replace("\n", " "));

				data.getPathwayElements().put(group, groupRes);
				
				Resource complexBinding = model.createResource(Utils.WP_RDF_URL + "/Pathway/" + data.getPwyId() + "_r" + data.getRevision() + "/ComplexBinding/" + graphId);
				complexBinding.addProperty(RDF.type, Wp.Interaction);
				complexBinding.addProperty(RDF.type, Wp.Binding);
				complexBinding.addProperty(RDF.type, Wp.ComplexBinding);
				complexBinding.addProperty(Wp.participants, groupRes);
				complexBinding.addProperty(Wp.isAbout, model.createResource(Utils.WP_RDF_URL + "/Pathway/" + data.getPwyId() + "_r" + data.getRevision() + "/Group/" + graphId));
				complexBinding.addProperty(DCTerms.isPartOf, data.getPathwayRes());
				
				for(Resource r : participants) {
					groupRes.addProperty(Wp.participants, r);
					complexBinding.addProperty(Wp.participants, r);
					r.addProperty(DCTerms.isPartOf, groupRes);
				}
				// there is (potentially) one special participant
				if (embeddedComplexDataNode != null) {
					Xref idXref = embeddedComplexDataNode.getXref();
					if (idXref != null && idXref.getId() != null && idXref.getId().trim().length() > 0) {
						try {
							GpmlConverter.getUnifiedIdentifiers(model, mapper, idXref, groupRes);
						} catch (Exception exception) {
							// System.out.println("Error while adding complex identifier: " + exception.getMessage());
						}
						groupRes.addProperty(RDFS.label, embeddedComplexDataNode.getTextLabel());
						if (idXref.getDataSource() != null) {
							String idURL = idXref.getDataSource().getIdentifiersOrgUri(idXref.getId());
							groupRes.addProperty(DC.identifier, model.createResource(idURL));
							groupRes.addLiteral(DC.source, idXref.getDataSource().getFullName());
							groupRes.addLiteral(DCTerms.identifier, idXref.getId());
						}
					}
				}
				
				for(PublicationXref xref : group.getBiopaxReferenceManager().getPublicationXRefs()) {
					if(xref.getPubmedId() != null && !xref.getPubmedId().trim().equals("")) {
						String pubmedUrl = Utils.IDENTIFIERS_ORG_URL + "/pubmed/" + xref.getPubmedId().trim();
						Resource pubmedRes = model.createResource(pubmedUrl);
						groupRes.addProperty(DCTerms.bibliographicCitation, pubmedRes);
						complexBinding.addProperty(DCTerms.bibliographicCitation, model.createResource(pubmedUrl));
						pubmedRes.addProperty(RDF.type, Wp.PublicationReference);
						pubmedRes.addProperty(FOAF.page, model.createResource(Utils.PUBMED_URL + xref.getPubmedId().trim()));
						pubmedRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
					}
				}
				
			}
		}
		// TODO - for now only convert groups of type complex - what about other elements
	}
	
	/**
	 * conversion only GPML vocabulary
	 */
	public static void parseGroupGpml(MGroup group, Model model, DataHandlerGpml data) {
		
		String graphId = group.getGraphId();
		if(graphId == null) {
			graphId = group.getGroupId();
		}
		
		Resource groupRes = model.createResource(data.getPathwayRes().getURI() + "/Group/" + graphId);
	
		groupRes.addProperty(RDF.type, Gpml.GROUP);
		data.getPathwayRes().addProperty(Gpml.HAS_GROUP, groupRes);
		groupRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());	
		
		
		if(group.getGroupRef() != null) groupRes.addLiteral(Gpml.GROUP_REF, group.getGroupRef());
		groupRes.addLiteral(Gpml.GROUP_ID, group.getGroupId());
		if(group.getGraphId() != null) groupRes.addLiteral(Gpml.GRAPH_ID, group.getGraphId());
		if(group.getTextLabel() != null && !group.getTextLabel().equals("")) groupRes.addLiteral(Gpml.TEXTLABEL, group.getTextLabel());
		groupRes.addLiteral(Gpml.STYLE, group.getGroupStyle().getName());
		
		for(String s : group.getBiopaxRefs()) {
			groupRes.addLiteral(Gpml.BIOPAX_REF, s);
		}
		
		for(Comment c : group.getComments()) {
			CommentConverter.parseCommentGpml(c, model, groupRes, data);
		}
		
		for(PublicationXref xref : group.getBiopaxReferenceManager().getPublicationXRefs()) {
			PublicationXrefConverter.parsePublicationXrefGpml(xref, groupRes, model, data);
		}
		
		for(PathwayElement e : group.getGroupElements()) {
			Resource r = data.getPathwayElements().get(e);
			if (r != null) r.addProperty(DCTerms.isPartOf, groupRes);
		}
		
		data.getPathwayElements().put(group, groupRes);
	}
}
