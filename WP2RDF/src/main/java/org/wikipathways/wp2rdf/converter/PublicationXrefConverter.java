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
import org.wikipathways.wp2rdf.ontologies.Gpml;
import org.wikipathways.wp2rdf.ontologies.GpmlNew;
import org.wikipathways.wp2rdf.ontologies.Wp;
import org.wikipathways.wp2rdf.ontologies.WpOld;
import org.wikipathways.wp2rdf.utils.DataHandlerGpml;
import org.wikipathways.wp2rdf.utils.Utils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * 
 * @author mkutmon
 * @author ryanmiller
 *
 */
public class PublicationXrefConverter {
	
	/**
	 * conversion only WP vocabulary
	 * semantic information about a publication xref
	 */
	public static void parsePublicationXrefWp(PublicationXref xref, Resource parent, Model model) {
		if (xref.getPubmedId() == null || xref.getPubmedId().trim().length() == 0) return; // Reconsider...
		Resource pubXrefRes = model.createResource(Utils.IDENTIFIERS_ORG_URL + "/pubmed/" + xref.getPubmedId());
		pubXrefRes.addProperty(RDF.type, Wp.publicationReference);
		parent.addProperty(DCTerms.references, pubXrefRes);
		pubXrefRes.addProperty(DCTerms.isPartOf, parent);
		pubXrefRes.addProperty(FOAF.page, model.createResource(Utils.PUBMED_URL + xref.getPubmedId()));
	}
	
	/**
	 * conversion only GPML vocabulary
	 */
	public static void parsePublicationXrefGpml(PublicationXref xref, Resource parent, Model model, DataHandlerGpml data) {
		Resource pubXrefRes = model.createResource(Utils.IDENTIFIERS_ORG_URL + "/pubmed/" + xref.getPubmedId());
		if (xref.getPubmedId() == null || xref.getPubmedId().trim().length() == 0)
			pubXrefRes = model.createResource(data.getPathwayRes().getURI() + "/pub/" + xref.getId());
		pubXrefRes.addProperty(RDF.type, GpmlNew.PUBLICATION_XREF);
		pubXrefRes.addLiteral(GpmlNew.ID, xref.getPubmedId() != null ? xref.getPubmedId() : "");
		pubXrefRes.addLiteral(GpmlNew.DATABASE, "Pubmed");
		
		pubXrefRes.addProperty(DCTerms.isPartOf, parent);			
		parent.addProperty(GpmlNew.HAS_PUBLICATION_XREF, pubXrefRes);
		
		data.getPubXrefs().put(xref, pubXrefRes);
	}
	
	/**
	 * old conversion GPML + WP
	 */
	public static void parsePublicationXref(PublicationXref xref, Resource parent, Model model) {
		Resource pubXrefRes = model.createResource(Utils.IDENTIFIERS_ORG_URL + "/pubmed/" + xref.getPubmedId());
		pubXrefRes.addProperty(RDF.type, WpOld.PublicationReference);
		parent.addProperty(DCTerms.references, pubXrefRes);
		pubXrefRes.addProperty(DCTerms.isPartOf, parent);			
		pubXrefRes.addLiteral(Gpml.biopaxref, xref.getId());
		pubXrefRes.addProperty(FOAF.page, model.createResource(Utils.PUBMED_URL + xref.getPubmedId()));
	}
}
