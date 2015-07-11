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

import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.pathvisio.core.biopax.PublicationXref;
import org.pathvisio.core.model.LineStyle;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.model.PathwayElement.Comment;
import org.wikipathways.wp2rdf.ontologies.Gpml;
import org.wikipathways.wp2rdf.ontologies.GpmlNew;
import org.wikipathways.wp2rdf.ontologies.Skos;
import org.wikipathways.wp2rdf.ontologies.Wp;
import org.wikipathways.wp2rdf.ontologies.WpOld;
import org.wikipathways.wp2rdf.utils.DataHandlerGpml;
import org.wikipathways.wp2rdf.utils.DataHandlerWp;
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
public class DataNodeConverter {

	/**
	 * conversion only WP vocabulary
	 * semantic information about a data node
	 */
	public static void parseDataNodeWp(PathwayElement elem, Model model, DataHandlerWp data) {
		
		if(elem.getXref() != null && elem.getXref().getId() != null && elem.getXref().getDataSource() != null) {
			if(!elem.getDataNodeType().equals("Unknown")) {
				String url = elem.getDataSource().getIdentifiersOrgUri(elem.getXref().getId());
				if(url != null && !url.equals("")) {
					Resource datanodeRes = data.getDataNodes().get(elem.getXref());
					if(datanodeRes == null) {
						datanodeRes = model.createResource(url);
						
						datanodeRes.addProperty(DC.identifier, model.createResource(url));
						datanodeRes.addLiteral(DCTerms.source, elem.getXref().getDataSource().getFullName());
						datanodeRes.addLiteral(DCTerms.identifier, elem.getXref().getId());
						
						datanodeRes.addProperty(RDF.type, Wp.DataNode);
						switch (elem.getDataNodeType()) {
						case "GeneProduct":
							datanodeRes.addProperty(RDF.type, Wp.GeneProduct);
							break;
						case "Protein":
							datanodeRes.addProperty(RDF.type, Wp.Protein);
							break;
						case "Metabolite":
							datanodeRes.addProperty(RDF.type, Wp.Metabolite);
							break;
						case "Rna":
							datanodeRes.addProperty(RDF.type, Wp.Rna);
							break;
						case "Pathway":
							// TODO
							break;
						case "Complex":
							// TODO
							break;
						default:
							break;
						}
						data.getDataNodes().put(elem.getXref(), datanodeRes);
						data.getPathwayElements().put(elem, datanodeRes);
					}
					// TODO: what to do about those - are they pathway specific?
					for(PublicationXref xref : elem.getBiopaxReferenceManager().getPublicationXRefs()) {
						if(xref.getPubmedId() != null && !xref.getPubmedId().equals("")) {
							String pubmedUrl = Utils.IDENTIFIERS_ORG_URL + "/pubmed/" + xref.getPubmedId();
							datanodeRes.addProperty(DCTerms.bibliographicCitation, model.createResource(pubmedUrl));
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
	
	/**
	 * conversion only GPML vocabulary
	 */
	public static void parseDataNodesGpml(PathwayElement elem, Model model, DataHandlerGpml data) {
		
		Resource datanodeRes = model.createResource(data.getPathwayRes().getURI() + "/DataNode/" + elem.getGraphId());
		if (elem.getGraphId() == null)
			datanodeRes = model.createResource(data.getPathwayRes().getURI() + "/DataNode/" + elem.hashCode());

		datanodeRes.addLiteral(GpmlNew.FONT_STYLE, elem.isItalic() ? "Italic" : "Normal");
		datanodeRes.addLiteral(GpmlNew.LINE_THICKNESS, elem.getLineThickness());
		datanodeRes.addLiteral(GpmlNew.FONT_SIZE, elem.getMFontSize());
		datanodeRes.addLiteral(GpmlNew.FONT_NAME, elem.getFontName());
		datanodeRes.addLiteral(GpmlNew.ALIGN, elem.getAlign().getGpmlName());
		datanodeRes.addLiteral(GpmlNew.GRAPH_ID, elem.getGraphId() != null ? elem.getGraphId() : "");
		if(elem.getGroupRef() != null) datanodeRes.addLiteral(GpmlNew.GROUP_REF, elem.getGroupRef());
		datanodeRes.addLiteral(GpmlNew.COLOR, Utils.colorToHex(elem.getColor()));
		datanodeRes.addLiteral(GpmlNew.CENTER_Y, elem.getMCenterY());
		datanodeRes.addLiteral(GpmlNew.VALIGN, elem.getValign().getGpmlName());
		datanodeRes.addLiteral(GpmlNew.FONT_WEIGHT, elem.isBold() ? "Bold" : "Normal");
		datanodeRes.addLiteral(GpmlNew.FONT_DECORATION, elem.isUnderline() ? "Underline" : "Normal");
		datanodeRes.addLiteral(GpmlNew.FONT_STRIKETHRU, elem.isStrikethru() ? "Strikethru" : "Normal");
		datanodeRes.addLiteral(GpmlNew.HEIGHT, elem.getMHeight());
		datanodeRes.addLiteral(GpmlNew.LINE_STYLE, elem.getLineStyle() != LineStyle.DASHED ? "Solid" : "Broken");
		datanodeRes.addLiteral(GpmlNew.CENTER_X, elem.getMCenterX());
		datanodeRes.addLiteral(GpmlNew.TEXTLABEL, elem.getTextLabel());
		datanodeRes.addLiteral(GpmlNew.WIDTH, elem.getMWidth());
		datanodeRes.addLiteral(GpmlNew.FILL_COLOR, Utils.colorToHex(elem.getFillColor()));
		datanodeRes.addLiteral(GpmlNew.ZORDER, elem.getZOrder());
		if (elem.getShapeType() != null)
			datanodeRes.addLiteral(GpmlNew.SHAPE_TYPE, elem.getShapeType().getName());
		datanodeRes.addLiteral(GpmlNew.TYPE, elem.getDataNodeType());
		
		if(elem.getXref() != null && elem.getXref().getId() != null && elem.getXref().getDataSource() != null) {
			datanodeRes.addLiteral(GpmlNew.XREF_ID, elem.getXref().getId());
			datanodeRes.addLiteral(GpmlNew.XREF_DATASOURCE, elem.getXref().getDataSource().getFullName());
		}
		
		for(String s : elem.getBiopaxRefs()) {
			datanodeRes.addLiteral(GpmlNew.BIOPAX_REF, s);
		}
		
		for(Comment c : elem.getComments()) {
			CommentConverter.parseCommentGpml(c, model, datanodeRes, data);
		}
		
		for(PublicationXref xref : elem.getBiopaxReferenceManager().getPublicationXRefs()) {
			PublicationXrefConverter.parsePublicationXrefGpml(xref, datanodeRes, model, data);
		}
		
		datanodeRes.addProperty(RDF.type, GpmlNew.DATA_NODE);
		
		datanodeRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
		data.getPathwayRes().addProperty(GpmlNew.HAS_DATA_NODE, datanodeRes);
		data.getPathwayElements().put(elem, datanodeRes);
		
	}

	/**
	 * old conversion GPML + WP
	 */
	public static void parseDataNodes(PathwayElement elem, Model model, IDMapper geneMapper, IDMapper metMapper, DataHandlerGpml data) {
		
		String name = elem.getTextLabel().replace("\n", " ");
		
		Resource datanodeRes = model.createResource(data.getPathwayRes().getURI() + "/DataNode/" + elem.getGraphId());
		datanodeRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
		datanodeRes.addProperty(RDF.type, Gpml.DataNode);
		datanodeRes.addProperty(RDF.type, Skos.Concept);
		
		// why this one?
		datanodeRes.addProperty(RDFS.isDefinedBy, Gpml.DataNode);
		datanodeRes.addLiteral(RDFS.label, model.createLiteral(name, "en"));
		
		// GPML RELATED PROPERTIES
		datanodeRes.addLiteral(Gpml.graphid, elem.getGraphId());
		datanodeRes.addLiteral(Gpml.height, elem.getMHeight());
		datanodeRes.addLiteral(Gpml.width, elem.getMWidth());
		datanodeRes.addLiteral(Gpml.zorder, elem.getZOrder());
		datanodeRes.addLiteral(Gpml.centerx, elem.getMCenterX());
		datanodeRes.addLiteral(Gpml.centery, elem.getMCenterY());

		// TODO: Add all font related with a central font resource

		// PUBLICATION REFERENCES
		for(PublicationXref xref : elem.getBiopaxReferenceManager().getPublicationXRefs()) {
			PublicationXrefConverter.parsePublicationXref(xref, datanodeRes, model);
		}
		
		// IDENTIFIERS
		if(elem.getXref() != null && !elem.getXref().getId().equals("") && elem.getXref().getDataSource() != null) {
			// TODO: fix issue with some of the identifiers.org URIs
			Resource idRes = model.createResource(elem.getXref().getDataSource().getIdentifiersOrgUri(elem.getXref().getId()));
			datanodeRes.addProperty(DC.identifier, idRes);
			datanodeRes.addLiteral(DCTerms.identifier, elem.getXref().getId());
			// TODO: why not use DCTerms for source as well?
			datanodeRes.addLiteral(DC.source, elem.getXref().getDataSource().getFullName());
			
			// TODO: mapping to unified ids
			try {
				switch (elem.getDataNodeType()) {
				case "GeneProduct":
					mapGeneProduct(elem, geneMapper, datanodeRes, model);
					break;
				case "Protein":
					mapGeneProduct(elem, geneMapper, datanodeRes, model);
					break;
				case "Metabolite":
					mapMetabolite(elem, metMapper, datanodeRes, model);
					break;
				case "Rna":
					mapGeneProduct(elem, geneMapper, datanodeRes, model);
					break;
				default:
					break;
				}
			} catch (IDMapperException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		switch (elem.getDataNodeType()) {
		case "GeneProduct":
			datanodeRes.addProperty(RDF.type, WpOld.GeneProduct);
			break;
		case "Protein":
			datanodeRes.addProperty(RDF.type, WpOld.Protein);
			break;
		case "Metabolite":
			datanodeRes.addProperty(RDF.type, WpOld.Metabolite);
			break;
		case "Pathway":
			datanodeRes.addProperty(RDF.type, WpOld.Pathway);
			break;
		case "Rna":
			datanodeRes.addProperty(RDF.type, WpOld.RNA);
			break;
		default:
			break;
		}
		
		// GROUP 
		if(elem.getGroupRef() != null) {
			Resource res = model.getResource(data.getPathwayRes().getURI() + "/Group/" + elem.getGroupRef());
			if(res != null) {
				datanodeRes.addProperty(DCTerms.isPartOf, res);
			} else {
				System.out.println("ERRRORRRR!!!");
			}
		}
		
		// TODO: subClass of resource missing (not sure what that is)
		
		
		data.getPathwayElements().put(elem, datanodeRes);
	}
	
	private static void mapMetabolite(PathwayElement elem, IDMapper metMapper, Resource datanodeRes, Model model) throws IDMapperException {
		Set<Xref> resCs = metMapper.mapID(elem.getXref(), DataSource.getExistingBySystemCode("Cs"));
		for(Xref x : resCs) {
			Resource res = model.createResource(Utils.IDENTIFIERS_ORG_URL + "/chemspider/" + x.getId());
			datanodeRes.addProperty(WpOld.bdbChemspider, res);
		}
		Set<Xref> resCh = metMapper.mapID(elem.getXref(), DataSource.getExistingBySystemCode("Ch"));
		for(Xref x : resCh) {
			Resource res = model.createResource(Utils.IDENTIFIERS_ORG_URL + "/hmdb/" + x.getId());
			datanodeRes.addProperty(WpOld.bdbHmdb, res);
		}
	}
	
	
	private static void mapGeneProduct(PathwayElement elem, IDMapper geneMapper, Resource datanodeRes, Model model) throws IDMapperException {
		Set<Xref> resEn = geneMapper.mapID(elem.getXref(), DataSource.getExistingBySystemCode("En"));
		for(Xref x : resEn) {
			Resource res = model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + x.getId());
			datanodeRes.addProperty(WpOld.bdbEnsembl, res);
		}
		Set<Xref> resS = geneMapper.mapID(elem.getXref(), DataSource.getExistingBySystemCode("S"));
		for(Xref x : resS) {
			Resource res = model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + x.getId());
			datanodeRes.addProperty(WpOld.bdbUniprot, res);
		}
		Set<Xref> resH = geneMapper.mapID(elem.getXref(), DataSource.getExistingBySystemCode("H"));
		for(Xref x : resH) {
			Resource res = model.createResource(Utils.IDENTIFIERS_ORG_URL + "/hgnc.symbol/" + x.getId());
			datanodeRes.addProperty(WpOld.bdbHgncSymbol, res);
		}
		Set<Xref> resL = geneMapper.mapID(elem.getXref(), DataSource.getExistingBySystemCode("L"));
		for(Xref x : resL) {
			Resource res = model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + x.getId());
			datanodeRes.addProperty(WpOld.bdbEntrezGene, res);
		}
	}
}
