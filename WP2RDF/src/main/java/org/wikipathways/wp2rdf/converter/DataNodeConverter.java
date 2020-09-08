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

import java.io.FileNotFoundException;
import java.io.IOException;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;
import org.bridgedb.Xref;
import org.pathvisio.core.biopax.PublicationXref;
import org.pathvisio.core.model.LineStyle;
import org.pathvisio.core.model.Pathway;
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
public class DataNodeConverter {

	/**
	 * conversion only WP vocabulary
	 * semantic information about a data node
	 * @param mapper 
	 * @param metMapper 
	 * @param geneMapper 
	 * @throws IDMapperException 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 */
	public static void parseDataNodeWp(PathwayElement elem, Model model, DataHandlerWp data, Pathway p, IDMapperStack mapper, Xref idXref) throws IDMapperException, FileNotFoundException, ClassNotFoundException, IOException {
		
		
		//final Properties prop = new Properties();
		//prop.load(new FileInputStream("config.properties"));
		
		if(elem.getXref() != null && elem.getXref().getId() != null && elem.getXref().getDataSource() != null) {
			if(!elem.getDataNodeType().equals("Unknown")) {
				if (elem.getXref().getId() != null && elem.getXref().getId().trim().length() > 0) {
					Xref xref = elem.getXref();
					String xrefid = xref.getId(); 
					DataSource datasource = elem.getDataSource(); 
					String url = datasource.getIdentifiersOrgUri(xrefid);
					String foafURL = datasource.getKnownUrl(xrefid).replaceAll(" ", "_");
					if ("HMDB".equals(xref.getDataSource().getFullName())) {
						if (xrefid.length() == 11) {
							// OK, all is fine
						} else if (xrefid.length() > 4) {
							xrefid = "HMDB00" + xrefid.substring(4);
						} // else, something really weird
						url = datasource.getIdentifiersOrgUri(xrefid);
					}
					if(url != null && !url.equals("")) {
						Resource datanodeRes = data.getDataNodes().get(xref);
						if(datanodeRes == null) {
							if (url.contains("chebi/CHEBI:")){
								String resourceURL = url.trim().replaceAll(" ", "_");
								datanodeRes = model.createResource(resourceURL);
								datanodeRes.addProperty(DC.identifier, model.createResource(resourceURL));
							}
							else if (url.contains("chebi")){
								String resourceURL = url.trim().replaceAll(" ", "_").replace("chebi/","chebi/CHEBI:");
								datanodeRes = model.createResource(resourceURL);
								datanodeRes.addProperty(DC.identifier, model.createResource(resourceURL));
							}
							else{
								String resourceURL = url.trim().replaceAll(" ", "_");
								datanodeRes = model.createResource(resourceURL);
								datanodeRes.addProperty(DC.identifier, model.createResource(resourceURL));
							}
							
							datanodeRes.addLiteral(DC.source, xref.getDataSource().getFullName());
							datanodeRes.addLiteral(DCTerms.identifier, xrefid);

							datanodeRes.addProperty(RDF.type, Wp.DataNode);
							
							switch (elem.getDataNodeType()) {
							case "GeneProduct":
								datanodeRes.addProperty(RDF.type, Wp.GeneProduct);
								// add id mapping step
								
								GpmlConverter.getUnifiedIdentifiers(model, mapper, idXref, datanodeRes);
								
								break;
								
							case "Protein":
								datanodeRes.addProperty(RDF.type, Wp.Protein);
								// add id mapping step
								
								GpmlConverter.getUnifiedIdentifiers(model, mapper, idXref, datanodeRes);
								
								break;
								
							case "Metabolite":
								datanodeRes.addProperty(RDF.type, Wp.Metabolite);
								// add id mapping step
								
								GpmlConverter.getUnifiedIdentifiers(model, mapper, idXref, datanodeRes);
								
								break;
								
							case "Rna":
								datanodeRes.addProperty(RDF.type, Wp.Rna);
								// add id mapping step
								
								GpmlConverter.getUnifiedIdentifiers(model, mapper, idXref, datanodeRes);

								
								break;
							case "Pathway":
								// TODO
								break;
							case "Complex":
								datanodeRes.addProperty(RDF.type, Wp.Complex);
								// add id mapping step

								GpmlConverter.getUnifiedIdentifiers(model, mapper, idXref, datanodeRes);

								break;
							default:
								break;
							}
									
							
							data.getDataNodes().put(elem.getXref(), datanodeRes);
							data.getPathwayElements().put(elem, datanodeRes);
						}
						// FOAF URL
						if (foafURL != null) {
							Resource foafResource = model.createResource(foafURL);
							datanodeRes.addProperty(FOAF.page, foafResource);
						}
						// TODO: what to do about those - are they pathway specific?
						for(PublicationXref pubXref : elem.getBiopaxReferenceManager().getPublicationXRefs()) {
							if(pubXref.getPubmedId() != null && !pubXref.getPubmedId().trim().equals("")) {
								String pubmedUrl = Utils.IDENTIFIERS_ORG_URL + "/pubmed/" + pubXref.getPubmedId().trim();
								Resource pubmedRes = model.createResource(pubmedUrl.trim());
								datanodeRes.addProperty(DCTerms.bibliographicCitation, pubmedRes);
								pubmedRes.addProperty(RDF.type, Wp.PublicationReference);
								pubmedRes.addProperty(FOAF.page, model.createResource(Utils.PUBMED_URL + pubXref.getPubmedId().trim()));
								pubmedRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
							
							}
						}

						datanodeRes.addProperty(Wp.isAbout, model.createResource(Utils.WP_RDF_URL + "/Pathway/" + data.getPwyId() + "_r" + data.getRevision() +
								"/DataNode/" + elem.getGraphId()));
						datanodeRes.addLiteral(RDFS.label, elem.getTextLabel().replace("\n", " ").trim());
						datanodeRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
					}
				}
			}
		}
	}
	
	/**
	 * conversion only GPML vocabulary
	 */
	public static void parseDataNodesGpml(PathwayElement elem, Model model, DataHandlerGpml data) {
		
		Resource datanodeRes = model.createResource(data.getPathwayRes().getURI() + "/DataNode/" + elem.getGraphId());
		if (elem.getGraphId() == null)
			datanodeRes = model.createResource(data.getPathwayRes().getURI() + "/DataNode/" + elem.hashCode());

		datanodeRes.addLiteral(Gpml.FONT_STYLE, elem.isItalic() ? "Italic" : "Normal");
		datanodeRes.addLiteral(Gpml.LINE_THICKNESS, elem.getLineThickness());
		datanodeRes.addLiteral(Gpml.FONT_SIZE, elem.getMFontSize());
		datanodeRes.addLiteral(Gpml.FONT_NAME, elem.getFontName());
		datanodeRes.addLiteral(Gpml.ALIGN, elem.getAlign().getGpmlName());
		datanodeRes.addLiteral(Gpml.GRAPH_ID, elem.getGraphId() != null ? elem.getGraphId() : "");
		if(elem.getGroupRef() != null) datanodeRes.addLiteral(Gpml.GROUP_REF, elem.getGroupRef());
		datanodeRes.addLiteral(Gpml.COLOR, Utils.colorToHex(elem.getColor()));
		datanodeRes.addLiteral(Gpml.CENTER_Y, elem.getMCenterY());
		datanodeRes.addLiteral(Gpml.VALIGN, elem.getValign().getGpmlName());
		datanodeRes.addLiteral(Gpml.FONT_WEIGHT, elem.isBold() ? "Bold" : "Normal");
		datanodeRes.addLiteral(Gpml.FONT_DECORATION, elem.isUnderline() ? "Underline" : "Normal");
		datanodeRes.addLiteral(Gpml.FONT_STRIKETHRU, elem.isStrikethru() ? "Strikethru" : "Normal");
		datanodeRes.addLiteral(Gpml.HEIGHT, elem.getMHeight());
		datanodeRes.addLiteral(Gpml.LINE_STYLE, elem.getLineStyle() != LineStyle.DASHED ? "Solid" : "Broken");
		datanodeRes.addLiteral(Gpml.CENTER_X, elem.getMCenterX());
		datanodeRes.addLiteral(Gpml.TEXTLABEL, elem.getTextLabel());
		datanodeRes.addLiteral(Gpml.WIDTH, elem.getMWidth());
		datanodeRes.addLiteral(Gpml.FILL_COLOR, Utils.colorToHex(elem.getFillColor()));
		datanodeRes.addLiteral(Gpml.ZORDER, elem.getZOrder());
		if (elem.getShapeType() != null)
			datanodeRes.addLiteral(Gpml.SHAPE_TYPE, elem.getShapeType().getName());
		datanodeRes.addLiteral(Gpml.TYPE, elem.getDataNodeType());
		
		if(elem.getXref() != null && elem.getXref().getId() != null && elem.getXref().getDataSource() != null) {
			datanodeRes.addLiteral(Gpml.XREF_ID, elem.getXref().getId());
			datanodeRes.addLiteral(Gpml.XREF_DATASOURCE, elem.getXref().getDataSource().getFullName());
		}
		
		for(String s : elem.getBiopaxRefs()) {
			datanodeRes.addLiteral(Gpml.BIOPAX_REF, s);
		}
		
		for(Comment c : elem.getComments()) {
			CommentConverter.parseCommentGpml(c, model, datanodeRes, data);
		}
		
		for(PublicationXref xref : elem.getBiopaxReferenceManager().getPublicationXRefs()) {
			PublicationXrefConverter.parsePublicationXrefGpml(xref, datanodeRes, model, data);
		}
		
		datanodeRes.addProperty(RDF.type, Gpml.DATA_NODE);
		
		datanodeRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
		data.getPathwayRes().addProperty(Gpml.HAS_DATA_NODE, datanodeRes);
		data.getPathwayElements().put(elem, datanodeRes);
		
	}
}
