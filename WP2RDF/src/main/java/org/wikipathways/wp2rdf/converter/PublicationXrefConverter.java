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

import java.io.UnsupportedEncodingException;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;
import org.bridgedb.Xref;
import org.pathvisio.core.biopax.PublicationXref;
import org.wikipathways.wp2rdf.GpmlConverter;
import org.wikipathways.wp2rdf.ontologies.Gpml;
import org.wikipathways.wp2rdf.ontologies.Wp;
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
	public static void parsePublicationXrefWp(PublicationXref xref, Resource parent, Model model, IDMapperStack mapper) {
		if (xref.getPubmedId() == null && xref.getSource() == null) return;
		String pmid = xref.getPubmedId() == null ? "" : xref.getPubmedId().trim();
		String source = xref.getSource() == null ? "" : xref.getSource().trim();
		if (pmid.contains("10.") || source.contains("10.")) {
			if (!pmid.contains("10.")) pmid = source;
			if (pmid.startsWith("DOI: ")) pmid = pmid.substring(4).trim();
			if (pmid.startsWith("https://doi.org/")) pmid = pmid.replace("https://doi.org/","").trim();
			if (pmid.startsWith("http://doi.org/")) pmid = pmid.replace("http://doi.org/","").trim();
			if (pmid.startsWith("10.") && !pmid.contains(" ")) {
				Resource pubXrefRes = model.createResource("https://doi.org/" + pmid);
				pubXrefRes.addProperty(RDF.type, Wp.PublicationReference);
				parent.addProperty(DCTerms.references, pubXrefRes);
				pubXrefRes.addProperty(DCTerms.isPartOf, parent);
				pubXrefRes.addProperty(FOAF.page, pubXrefRes);
			}
			return;
		}
		if (pmid.length() == 0) return;
		try {
			Integer.parseInt(pmid);
			Resource pubXrefRes = model.createResource(Utils.IDENTIFIERS_ORG_URL + "/pubmed/" + pmid);
			pubXrefRes.addProperty(RDF.type, Wp.PublicationReference);
			parent.addProperty(DCTerms.references, pubXrefRes);
			pubXrefRes.addProperty(DCTerms.isPartOf, parent);
			pubXrefRes.addProperty(FOAF.page, model.createResource(Utils.PUBMED_URL + pmid));
			if (mapper != null) {
				Xref litXref = new Xref(pmid, DataSource.register("Pbm", "PubMed").asDataSource());
				try {
					GpmlConverter.getUnifiedIdentifiers(model, mapper, litXref, pubXrefRes);
				} catch (Exception e) {
					// something wrong with the mapping; ignore
				}
			}
		} catch (NumberFormatException exception) {
			// invalid Pubmed ID
		}
	}
	
	/**
	 * conversion only GPML vocabulary
	 */
	public static void parsePublicationXrefGpml(PublicationXref xref, Resource parent, Model model, DataHandlerGpml data) {
		Resource pubXrefRes = null;
		if (xref.getPubmedId() != null && xref.getPubmedId().trim().length() != 0) {
			pubXrefRes = model.createResource(Utils.IDENTIFIERS_ORG_URL + "/pubmed/" + xref.getPubmedId().trim());
		} else {
			pubXrefRes = model.createResource(data.getPathwayRes().getURI() + "/pub/" + xref.getId().trim());
		}
		pubXrefRes.addProperty(RDF.type, Gpml.PUBLICATION_XREF);
		pubXrefRes.addLiteral(Gpml.ID, xref.getPubmedId() != null ? xref.getPubmedId() : "");
		pubXrefRes.addLiteral(Gpml.DATABASE, "Pubmed");
		
		pubXrefRes.addProperty(DCTerms.isPartOf, parent);			
		parent.addProperty(Gpml.HAS_PUBLICATION_XREF, pubXrefRes);
		
		data.getPubXrefs().put(xref, pubXrefRes);
	}
}
