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

import org.bridgedb.Xref;
import org.bridgedb.bio.Organism;
import org.pathvisio.core.biopax.PublicationXref;
import org.pathvisio.core.model.OntologyTag;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement.Comment;
import org.wikipathways.wp2rdf.ontologies.Gpml;
import org.wikipathways.wp2rdf.ontologies.Skos;
import org.wikipathways.wp2rdf.ontologies.Wp;
import org.wikipathways.wp2rdf.utils.Utils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * 
 * @author mkutmon
 * @author ryanmiller
 *
 */
public class PathwayConverter {

	/**
	 * conversion only WP vocabulary
	 * semantic information about the pathway
	 */
	public static Resource parsePathwayInfoWp(Pathway p, String wpId, String revision, Model model) {
		String name = p.getMappInfo().getMapInfoName();
		String url = Utils.IDENTIFIERS_ORG_URL + "/wikipathways/" + wpId ;
		Resource pwyRes = model.createResource(url + "_r" + revision);
  		Xref orgTaxId = Organism.fromLatinName(p.getMappInfo().getOrganism()).taxonomyID();
  		Resource pwyOrgRes = model.createResource(Utils.PURL_TAX_URL + orgTaxId.getId());
		
		
  		pwyRes.addProperty(Wp.organism, pwyOrgRes);
		pwyRes.addLiteral(Wp.organismName, p.getMappInfo().getOrganism());

		for (OntologyTag o : p.getOntologyTags()){
			pwyRes.addProperty(Wp.ontologyTag, model.createResource(Utils.PURL_OBO_LIB + o.getId().replace(":", "_"))); }
		
		pwyRes.addProperty(Wp.isAbout, model.createResource(Utils.WP_RDF_URL + "/Pathway/" + wpId + "_r" + revision));
		
		pwyRes.addProperty(FOAF.page, model.createResource(Utils.WP_URL + "/instance/" + wpId + "_r" + revision));
		pwyRes.addProperty(RDF.type, Wp.Pathway);
		pwyRes.addProperty(RDF.type, Skos.Collection);
		pwyRes.addProperty(DC.identifier, model.createResource(url));
		pwyRes.addLiteral(DCTerms.identifier, wpId);
		pwyRes.addLiteral(DC.title, model.createLiteral(name, "en"));
		pwyRes.addLiteral(DC.source, "Wikipathways");
		
		for(Comment o : p.getMappInfo().getComments()) 
		{
			if(o.getSource() != null) 
			{
				if(o.getSource().equals("WikiPathways-description")) 
				{
					pwyRes.addProperty(DCTerms.description, o.getComment());
				}
			}
		}
		for(PublicationXref x : p.getMappInfo().getBiopaxReferenceManager().getPublicationXRefs()) {
			PublicationXrefConverter.parsePublicationXrefWp(x, pwyRes, model);
		}
		
		return pwyRes;
	}
	
	/**
	 * conversion only GPML vocabulary
	 */
	public static Resource parsePathwayInfoGpml(Pathway p, String wpId, String revision, Model model) {

		Resource pwyRes = model.createResource(Utils.WP_RDF_URL + "/Pathway/" + wpId + "_r" + revision);
		
		// Required Attributes
		pwyRes.addLiteral(Gpml.ORGANISM, p.getMappInfo().getOrganism());
		pwyRes.addLiteral(Gpml.BOARD_HEIGHT, p.getMappInfo().getMBoardHeight());
		pwyRes.addLiteral(Gpml.BOARD_WIDTH, p.getMappInfo().getMBoardWidth());
		pwyRes.addLiteral(Gpml.NAME, p.getMappInfo().getMapInfoName());
		
		// Optional Attributes
		if(p.getMappInfo().getVersion() != null) pwyRes.addLiteral(Gpml.VERSION, p.getMappInfo().getVersion());
		if(p.getMappInfo().getCopyright() != null) pwyRes.addLiteral(Gpml.LICENSE, p.getMappInfo().getCopyright());
		if(p.getMappInfo().getAuthor() != null) pwyRes.addLiteral(Gpml.AUTHOR, p.getMappInfo().getAuthor());
		if(p.getMappInfo().getEmail() != null) pwyRes.addLiteral(Gpml.EMAIL, p.getMappInfo().getEmail());
		if(p.getMappInfo().getMaintainer() != null) pwyRes.addLiteral(Gpml.MAINTAINER, p.getMappInfo().getMaintainer());
		if(p.getMappInfo().getLastModified() != null) pwyRes.addLiteral(Gpml.LAST_MODIFIED, p.getMappInfo().getLastModified());
		if(p.getMappInfo().getMapInfoDataSource() != null) pwyRes.addLiteral(Gpml.DATA_SOURCE, p.getMappInfo().getMapInfoDataSource());
		
		for(String s : p.getMappInfo().getBiopaxRefs()) {
			pwyRes.addLiteral(Gpml.BIOPAX_REF, s);
		}
		return pwyRes;
	}
}
