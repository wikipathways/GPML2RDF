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
package org.wikipathways.wp2rdf.utils;

import java.io.File;

import org.bridgedb.BridgeDb;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.wikipathways.wp2rdf.ontologies.Biopax_level3;
import org.wikipathways.wp2rdf.ontologies.Freq;
import org.wikipathways.wp2rdf.ontologies.Gpml;
import org.wikipathways.wp2rdf.ontologies.Pav;
import org.wikipathways.wp2rdf.ontologies.Prov;
import org.wikipathways.wp2rdf.ontologies.Skos;
import org.wikipathways.wp2rdf.ontologies.Void;
import org.wikipathways.wp2rdf.ontologies.Wp;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

/**
 * 
 * @author mkutmon
 * @author ryanmiller
 *
 */
public class Utils {
	
	public static final String WP_RDF_URL = "http://rdf.wikipathways.org";
	public static final String WP_URL = "http://www.wikipathways.org";
	public static final String IDENTIFIERS_ORG_URL = "http://identifiers.org";
	public static final String PURL_TAX_URL = "http://purl.obolobrary.org/obo/NCBITaxon_";
	public static final String PUBMED_URL = "http://www.ncbi.nlm.nih.gox/pubmed/";
	
	public static void setModelPrefix(Model model){
		model.setNsPrefix("biopax", Biopax_level3.getURI());
		model.setNsPrefix("gpml", Gpml.getURI());
		model.setNsPrefix("wp", Wp.getURI());
		model.setNsPrefix("xsd", XSD.getURI());
		model.setNsPrefix("rdf", RDF.getURI());
		model.setNsPrefix("rdfs", RDFS.getURI());
		model.setNsPrefix("dcterms", DCTerms.getURI());
		model.setNsPrefix("wprdf", "http://rdf.wikipathways.org/");
		model.setNsPrefix("pubmed", "http://www.ncbi.nlm.nih.gov/pubmed/");
		model.setNsPrefix("foaf", FOAF.getURI());
		model.setNsPrefix("ncbigene", "http://identifiers.org/ncbigene/");
		model.setNsPrefix("cas", "http://identifiers.org/cas/");
		model.setNsPrefix("dc", DC.getURI());
		model.setNsPrefix("skos", Skos.getURI());
		model.setNsPrefix("void", Void.getURI());
		model.setNsPrefix("wprdf", "http://rdf.wikipathways.org/");
		model.setNsPrefix("pav", Pav.getURI());
		model.setNsPrefix("prov", Prov.getURI());
		model.setNsPrefix("dcterms", DCTerms.getURI());
		model.setNsPrefix("hmdb", "http://identifiers.org/hmdb/");
		model.setNsPrefix("freq", Freq.getURI());
	}
	
	public static IDMapper setUpIDMapper(File file) throws IDMapperException, ClassNotFoundException {
		Class.forName("org.bridgedb.rdb.IDMapperRdb");  
		IDMapper mapper = BridgeDb.connect("idmapper-pgdb:" + file.getAbsolutePath());
		return mapper;
	}
}
