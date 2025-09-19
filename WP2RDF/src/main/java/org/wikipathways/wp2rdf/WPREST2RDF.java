// WP2RDF
// Conversion from GPML pathways to RDF
// Copyright 2015-2018 BiGCaT Bioinformatics
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
package org.wikipathways.wp2rdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.bridgedb.IDMapperStack;
import org.bridgedb.bio.Organism;
import org.pathvisio.core.model.Pathway;
import org.wikipathways.wp2rdf.ontologies.Biopax_level3;
import org.wikipathways.wp2rdf.ontologies.Gpml;
import org.wikipathways.wp2rdf.ontologies.Pav;
import org.wikipathways.wp2rdf.ontologies.Void;
import org.wikipathways.wp2rdf.ontologies.Wp;
import org.wikipathways.wp2rdf.utils.Utils;

public class WPREST2RDF {

	@SuppressWarnings("serial")
	private static final List<String> EXCLUDED_PATHWAYS = new ArrayList<String>() {{
		add("WP4");     // sandbox
		add("WP2582");  // metabolite test pathway
	}};

	@SuppressWarnings("serial")
	private static final List<String> INCLUDED_TAGS = new ArrayList<String>() {{
		add("Curation:AnalysisCollection");
		add("Curation:Reactome_Approved");
	}};

	@SuppressWarnings("serial")
	private static final List<String> EXTRA_TAGS = new ArrayList<String>() {{
		// the additional Community tags
		add("Curation:AOP");
		add("Curation:IEM");
		add("Curation:CIRM_Related");
		add("Curation:COVID19");
		add("Curation:Lipids");
		add("Curation:RareDiseases");
		add("Curation:SGD_Approved");
		add("Curation:WormBase_Approved");
		// the additional curation tags
		add("Curation:NeedsWork");
		add("Curation:Hypothetical");
		add("Curation:Homology_Converted");
    }};

	@SuppressWarnings("serial")
	private static final List<String> BLACKLIST_TAGS = new ArrayList<String>() {{
		add("Curation:ProposedDeletion");
		add("Curation:Tutorial");
	}};

	@SuppressWarnings("serial")
	private static final Map<Organism,String> SPECIES = new HashMap<Organism,String>() {
		void add(Organism organism) {
			put(organism, organism.shortName() != null ? organism.shortName() : organism.code());
		}
	{
		add(Organism.AnophelesGambiae);
		add(Organism.ArabidopsisThaliana);
		add(Organism.BosTaurus);
		add(Organism.BacillusSubtilis);
		add(Organism.CaenorhabditisElegans);
		add(Organism.CanisFamiliaris);
		add(Organism.DanioRerio);
		add(Organism.DrosophilaMelanogaster);
		add(Organism.EquusCaballus);
		add(Organism.EscherichiaColi);
		add(Organism.GallusGallus);
		add(Organism.GibberellaZeae);
		add(Organism.HomoSapiens);
		add(Organism.MusMusculus);
		add(Organism.MycobacteriumTuberculosis);
		add(Organism.OryzaSativa);
		add(Organism.PanTroglodytes);
		add(Organism.PlasmodiumFalciparum);
		add(Organism.PopulusTrichocarpa);
		add(Organism.RattusNorvegicus);
		add(Organism.SaccharomycesCerevisiae);
		add(Organism.SolanumLycopersicum);
		add(Organism.ZeaMays);
	}};

	private static void populateVOID(Model voidModel, String downloadFilePattern, String domain) {
		// set prefixes
		voidModel.setNsPrefix("biopax", "http://www.biopax.org/release/biopax-level3.owl#");
		voidModel.setNsPrefix("cito", "http://purl.org/spar/cito/");
		voidModel.setNsPrefix("dcat", "http://www.w3.org/ns/dcat#");
		voidModel.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
		voidModel.setNsPrefix("freq", "http://purl.org/cld/freq/");
		voidModel.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
		voidModel.setNsPrefix("gpml", "http://vocabularies.wikipathways.org/gpml#");
		voidModel.setNsPrefix("hmdb", "https://identifiers.org/hmdb/");
		voidModel.setNsPrefix("ncbigene", "https://identifiers.org/ncbigene/");
		voidModel.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
		voidModel.setNsPrefix("pav", "http://purl.org/pav/");
		voidModel.setNsPrefix("prov", "http://www.w3.org/ns/prov#");
		voidModel.setNsPrefix("pubmed", "http://www.ncbi.nlm.nih.gov/pubmed/");
		voidModel.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		voidModel.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		voidModel.setNsPrefix("skos", "http://www.w3.org/2004/02/skos/core#");
		voidModel.setNsPrefix("void", "http://rdfs.org/ns/void#");
		voidModel.setNsPrefix("wp", "http://vocabularies.wikipathways.org/wp#");
		voidModel.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");

		// populate void.ttl
		Calendar now = Calendar.getInstance();
		String date = new SimpleDateFormat("yyyyMMdd").format(now.getTime());
		Literal nowLiteral = voidModel.createTypedLiteral(now);
		
		// define the dataset description info
		Resource dsDescription = voidModel.createResource(domain + date + "/datasetDescription/");
		dsDescription.addProperty(RDF.type, Void.DatasetDescription);
		Literal titleLiteral = voidModel.createLiteral("WikiPathways RDF VoID Description", "en");
		dsDescription.addLiteral(DCTerms.title, titleLiteral);
		Literal descriptionLiteral = voidModel.createLiteral(
			"This is the VoID description for this WikiPathways RDF dataset created on " + date + ".", "en"
		);
		dsDescription.addLiteral(DCTerms.description, descriptionLiteral);
		dsDescription.addProperty(Pav.createdWith, voidModel.createResource("https://github.com/wikipathways/GPML2RDF/tree/v4"));
		dsDescription.addProperty(Pav.createdBy, voidModel.createResource("https://jenkins.bigcat.unimaas.nl/job/GPML%20to%20GPML%20+%20WP%20RDF/"));
		dsDescription.addLiteral(Pav.createdOn, nowLiteral);
		dsDescription.addLiteral(DCTerms.issued, nowLiteral);
		dsDescription.addLiteral(DCTerms.modified, nowLiteral);
		dsDescription.addLiteral(Pav.lastUpdateOn, nowLiteral);

		// define the dataset info
		Resource voidBase = voidModel.createResource(domain + date + "/rdf/");
		dsDescription.addProperty(FOAF.primaryTopic, voidBase);
		voidBase.addProperty(
			voidModel.createProperty("http://www.w3.org/ns/dcat#landingPage"),
			voidModel.createResource("http://www.wikipathways.org/")
		);
		Resource wpHomeBase = voidModel.createResource("http://www.wikipathways.org/");
		Resource authorResource = voidModel.createResource("https://jenkins.bigcat.unimaas.nl/job/GPML%20to%20GPML%20+%20WP%20RDF/");
		Resource apiResource = voidModel.createResource("http://www.wikipathways.org/wpi/webservice/webservice.php");
		Resource license = voidModel.createResource("http://creativecommons.org/publicdomain/zero/1.0/");
		Resource instituteResource = voidModel.createResource("http://www.wikidata.org/entity/Q1137652");
		voidBase.addProperty(RDF.type, Void.Dataset);
		voidBase.addProperty(FOAF.homepage, wpHomeBase);
		voidBase.addProperty(DCTerms.license, license);
		voidBase.addLiteral(DCTerms.title, "WikiPathways RDF " + date);
		voidBase.addLiteral(DCTerms.description, "WikiPathways RDF data dump of the Curated and Reactome pathways created on " + date + ".");
		voidBase.addLiteral(Void.uriSpace, "http://rdf.wikipathways.org/wp/");
		voidBase.addLiteral(Void.uriSpace, "https://identifiers.org");
		voidBase.addProperty(Pav.importedBy, authorResource);
		voidBase.addProperty(Pav.importedFrom, apiResource);
		voidBase.addProperty(Pav.importedOn, nowLiteral);
		voidBase.addProperty(
			voidModel.createProperty("http://purl.org/dc/terms/accuralPeriodicity"),
			voidModel.createResource("http://purl.org/cld/freq/monthly")
		);
		voidBase.addProperty(Pav.createdBy, authorResource);
		voidBase.addProperty(Pav.createdAt, instituteResource);		 
		voidBase.addLiteral(Pav.createdOn, nowLiteral);
		voidBase.addLiteral(DCTerms.issued, nowLiteral);
		voidBase.addProperty(DCTerms.subject, Biopax_level3.Pathway);
		voidBase.addProperty(DCTerms.subject, Wp.Binding);
		voidBase.addProperty(DCTerms.subject, Wp.Catalysis);
		voidBase.addProperty(DCTerms.subject, Wp.Complex);
		voidBase.addProperty(DCTerms.subject, Wp.ComplexBinding);
		voidBase.addProperty(DCTerms.subject, Wp.DirectedInteraction);
		voidBase.addProperty(DCTerms.subject, Wp.GeneProduct);
		voidBase.addProperty(DCTerms.subject, Wp.Inhibition);
		voidBase.addProperty(DCTerms.subject, Wp.Interaction);
		voidBase.addProperty(DCTerms.subject, Wp.Metabolite);
		voidBase.addProperty(DCTerms.subject, Wp.Pathway);
		voidBase.addProperty(DCTerms.subject, Wp.PublicationReference);
		voidBase.addProperty(DCTerms.subject, Wp.Stimulation);
		voidBase.addProperty(DCTerms.subject, Wp.TranscriptionTranslation);
		voidBase.addProperty(DCTerms.subject, Wp.Pathway);
		voidBase.addProperty(DCTerms.subject, Wp.Protein);
		voidBase.addProperty(DCTerms.subject, Wp.Rna);
		voidBase.addProperty(
			voidModel.createProperty("http://www.w3.org/ns/dcat#theme"),
			voidModel.createResource("http://semanticscience.org/resource/SIO_001107")
		);
		voidBase.addProperty(Void.exampleResource, voidModel.createResource("https://identifiers.org/ncbigene/2678"));
		voidBase.addProperty(Void.exampleResource, voidModel.createResource("https://identifiers.org/pubmed/15215856"));
		voidBase.addProperty(Void.exampleResource, voidModel.createResource("https://identifiers.org/hmdb/HMDB02005"));
		voidBase.addProperty(Void.exampleResource, voidModel.createResource("https://rdf.wikipathways.org/WP15"));
		voidBase.addProperty(Void.exampleResource, voidModel.createResource("https://identifiers.org/obo.chebi/17242"));

		for (Organism organism : SPECIES.keySet()) {
			voidBase.addProperty(
				DCTerms.subject,
				voidModel.createResource("https://identifiers.org/taxonomy/" + organism.taxonomyID().getId())
			);
		}
		voidBase.addProperty(Void.vocabulary, Biopax_level3.NAMESPACE);
		voidBase.addProperty(Void.vocabulary, voidModel.createResource(Wp.getURI()));
		voidBase.addProperty(Void.vocabulary, voidModel.createResource(Gpml.getURI()));
		voidBase.addProperty(Void.vocabulary, FOAF.NAMESPACE);
		voidBase.addProperty(Void.vocabulary, Pav.NAMESPACE);

		// create two the distributions
		String[] codes = {"wp", "gpml"};
		for (String code : codes) {
			Resource distribution = voidModel.createResource(domain + date + "/rdf/" + code);
			voidBase.addProperty(Void.subset, distribution);
			distribution.addProperty(RDF.type, voidModel.createResource("http://www.w3.org/ns/dcat#Distribution"));
			distribution.addLiteral(
				voidModel.createProperty("http://www.w3.org/ns/dcat#mediaType"), "application/zip"
			);
			Resource mainDatadump = voidModel.createResource(
				downloadFilePattern.replaceAll("#CODE#", code).replaceAll("#DATE#", date)
			);
			distribution.addProperty(
				voidModel.createProperty("http://www.w3.org/ns/dcat#downloadURL"), mainDatadump
			);
			distribution.addProperty(Void.dataDump, mainDatadump);
		}

		// create link sets

		// WPRDF - Wikidata
		createLinkSet(voidModel.createResource(domain + date + "/linkset/wikidata"), 
			"WPRDF to Wikidata Linkset", voidBase,
			voidModel.createResource("http://www.wikidata.org/entity/Q2013"), Wp.bdbWikidata, license, nowLiteral
		);
		// WPRDF - ChEBI
		createLinkSet(voidModel.createResource(domain + date + "/linkset/chebi"), 
			"WPRDF to ChEBI Linkset", voidBase,
			voidModel.createResource("http://identifiers.org/chebi/CHEBI:138307"), Wp.bdbChEBI, license, nowLiteral
		);
		// WPRDF - Ensembl
		createLinkSet(voidModel.createResource(domain + date + "/linkset/ensembl"), 
            "WPRDF to Ensembl Linkset", voidBase,
			voidModel.createResource("http://identifiers.org/ensembl/ENSG00000100031"), Wp.bdbEnsembl, license, nowLiteral
		);
		// WPRDF - InChIKey
		createLinkSet(voidModel.createResource(domain + date + "/linkset/inchikey"), 
			"WPRDF to InChIKey Linkset", voidBase,
			voidModel.createResource("http://identifiers.org/inchikey/ADVPTQAUNPRNPO-REOHCLBHSA-N"), Wp.bdbInChIKey, license, nowLiteral
		);
		// WPRDF - LIPID MAPS
		createLinkSet(voidModel.createResource(domain + date + "/linkset/lipidmaps"), 
			"WPRDF to LIPID MAPS Linkset", voidBase,
			voidModel.createResource("http://identifiers.org/lipidmaps/LMFA01050243"), Wp.bdbLipidMaps, license, nowLiteral
		);
		// WPRDF - PubChem
		createLinkSet(voidModel.createResource(domain + date + "/linkset/pubchem"), 
			"WPRDF to PubChem Linkset", voidBase,
			voidModel.createResource("http://rdf.ncbi.nlm.nih.gov/pubchem/compound/CID101770"), Wp.bdbPubChem, license, nowLiteral
		);
		// WPRDF - UniProt
		createLinkSet(voidModel.createResource(domain + date + "/linkset/uniprot"), 
			"WPRDF to UniProt Linkset", voidBase,
			voidModel.createResource("http://identifiers.org/uniprot/17928"), Wp.bdbUniprot, license, nowLiteral
		);
		// WPRDF - Rhea
		createLinkSet(voidModel.createResource(domain + date + "/linkset/rhea"), 
			"WPRDF to Rhea Linkset", voidBase,
			voidModel.createResource("http://identifiers.org/rhea/35868"), Wp.bdbRhea, license, nowLiteral
		);
		// WPRDF - Complex Portal
		createLinkSet(voidModel.createResource(domain + date + "/linkset/complexportal"), 
			"WPRDF to Complex Portal Linkset", voidBase,
			voidModel.createResource("http://identifiers.org/complexportal/CPX-373"), Wp.bdbComplexPortal, license, nowLiteral
		);
    }

	private static void createLinkSet(Resource linkset, String title, Resource voidBase, Resource objectTarget,
			Property linkPredicate, Resource license, Literal creationDate) {
		linkset.addProperty(RDF.type, Void.Linkset);
		linkset.addProperty(Void.subjectsTarget, voidBase);
		linkset.addProperty(DCTerms.title, title);
		linkset.addProperty(Void.objectsTarget, objectTarget);
		linkset.addProperty(Void.linkPredicate, linkPredicate);
		linkset.addProperty(DCTerms.license, license);
		linkset.addLiteral(Pav.createdOn, creationDate);
	}

	public static IDMapperStack maps () throws Exception {
		final Properties prop = new Properties();
		String derbyFolder = "/tmp/" + System.getProperty("OPSBRIDGEDB", "OPSBRIDGEDB");
		if (new File(derbyFolder).exists()) {
  	        prop.load(new FileInputStream(derbyFolder + "/config.properties"));
		    IDMapperStack mapper = GpmlConverter.createBridgeDbMapper(prop);
		    return mapper;
		} else {
			System.out.println("WARN: BridgeDb config file folder does not exist: " + derbyFolder);
		}
		return new IDMapperStack();
	}

}
