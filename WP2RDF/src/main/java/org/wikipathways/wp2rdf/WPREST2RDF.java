package org.wikipathways.wp2rdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;
import org.bridgedb.bio.Organism;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.wikipathways.webservice.WSCurationTag;
import org.pathvisio.wikipathways.webservice.WSPathwayInfo;
import org.wikipathways.client.WikiPathwaysClient;
import org.wikipathways.wp2rdf.ontologies.Biopax_level3;
import org.wikipathways.wp2rdf.ontologies.Gpml;
import org.wikipathways.wp2rdf.ontologies.Pav;
import org.wikipathways.wp2rdf.ontologies.Void;
import org.wikipathways.wp2rdf.ontologies.Wp;
import org.wikipathways.wp2rdf.utils.Utils;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;

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
		add(Organism.EscherichiaColi);
		add(Organism.GallusGallus);
		add(Organism.GibberellaZeae);
		add(Organism.HomoSapiens);
		add(Organism.MusMusculus);
		add(Organism.OryzaSativa);
		add(Organism.PanTroglodytes);
		add(Organism.RattusNorvegicus);
		add(Organism.SaccharomycesCerevisiae);
		add(Organism.ZeaMays);
	}};

	public static void main(String[] args) throws NumberFormatException, ConverterException, IOException, ClassNotFoundException, IDMapperException {
		URL url = new URL("http://webservice.wikipathways.org");
		WikiPathwaysClient client = new WikiPathwaysClient(url);
		IDMapperStack mapper = WPREST2RDF.maps();
		
		boolean doAll = System.getProperty("doAll", "false").equals("true");

		for (Organism organism : SPECIES.keySet()) {
			System.out.println("Processing species: " + organism);
			WSPathwayInfo [] pathways = client.listPathways(organism);
			System.out.println("  found #pathways: " + pathways.length);
			for(WSPathwayInfo pwInfo : pathways) {
				System.out.println("  pathway: " + pwInfo.getId() + "\t" + pwInfo.getRevision());

				// to be converted? only if it has the right tag
				boolean gotTags = false;
				WSCurationTag[] tags = null;
				for (int i=0; !gotTags && i<=5; i++) {
					try {
						tags = client.getCurationTags(pwInfo.getId());
						gotTags = true;
					} catch (Throwable exception) {}
				}
				if (!gotTags) {
					System.out.println("Failed to get curation tags for " + pwInfo.getId());
				}
				if ((doAll || (gotTags && isIncludedTag(tags))) &&
				    !EXCLUDED_PATHWAYS.contains(pwInfo.getId())) {
					Model pathwayModel = ModelFactory.createDefaultModel();
					Utils.setModelPrefix(pathwayModel);

					Pathway p = WikiPathwaysClient.toPathway(client.getPathway(pwInfo.getId(), Integer.parseInt(pwInfo.getRevision())));

					// New conversion of the pathway in GPML vocabulary
					GpmlConverter.convertGpml(p, pwInfo.getId(), pwInfo.getRevision(), pathwayModel);

					String folder = "output/gpml/" + SPECIES.get(organism).replace(" ", "_") + "/";
					new File(folder).mkdirs();
					FileWriter output = new FileWriter(folder + pwInfo.getId() +".ttl");
					pathwayModel.write(output, "TURTLE");
					output.close();

					// New conversion of the pathway in WP vocabulary
					pathwayModel = ModelFactory.createDefaultModel();
					Utils.setModelPrefix(pathwayModel);
					GpmlConverter.convertWp(p, pwInfo.getId(), pwInfo.getRevision(), pathwayModel, mapper);

					folder = "output/wp/" + SPECIES.get(organism).replace(" ", "_") + "/";
					new File(folder).mkdirs();
					output = new FileWriter(folder + pwInfo.getId() +".ttl");
					pathwayModel.write(output, "TURTLE");
					output.close();
				}
			}
		}
		
		// create VoID files
		String folder = "output/";
		Writer output = new FileWriter(folder + "void.ttl");
		Model voidWp = ModelFactory.createDefaultModel();
		populateVOID(voidWp);
		voidWp.write(output, "TURTLE");
		output.close();

	}

	private static void populateVOID(Model voidModel) {
		// set prefixes
		voidModel.setNsPrefix("prov", "http://www.w3.org/ns/prov#");
		voidModel.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
		voidModel.setNsPrefix("hmdb", "http://identifiers.org/hmdb/");
		voidModel.setNsPrefix("freq", "http://purl.org/cld/freq/");
		voidModel.setNsPrefix("pubmed", "http://www.ncbi.nlm.nih.gov/pubmed/");
		voidModel.setNsPrefix("wp", "http://vocabularies.wikipathways.org/wp#");
		voidModel.setNsPrefix("void", "http://rdfs.org/ns/void#");
		voidModel.setNsPrefix("biopax", "http://www.biopax.org/release/biopax-level3.owl#");
		voidModel.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
		voidModel.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		voidModel.setNsPrefix("pav", "http://purl.org/pav/");
		voidModel.setNsPrefix("ncbigene", "http://identifiers.org/ncbigene/");
		voidModel.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		voidModel.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		voidModel.setNsPrefix("gpml", "http://vocabularies.wikipathways.org/gpml#");
		voidModel.setNsPrefix("skos", "http://www.w3.org/2004/02/skos/core#");

		// populate void.ttl
		Calendar now = Calendar.getInstance();
		String date = "" + now.get(Calendar.YEAR) + now.get(Calendar.MONTH) + now.get(Calendar.DAY_OF_MONTH);
		Literal nowLiteral = voidModel.createTypedLiteral(now);
		
		// define the dataset description info
		Resource dsDescription = voidModel.createResource("http://rdf.wikipathways.org/" + date + "/datasetDescription/");
		dsDescription.addProperty(RDF.type, Void.DatasetDescription);
		Literal titleLiteral = voidModel.createLiteral("WikiPathways RDF VoID Description", "en");
		dsDescription.addLiteral(DCTerms.title, titleLiteral);
		Literal descriptionLiteral = voidModel.createLiteral(
			"This is the VoID description for this WikiPathways RDF dataset.", "en"
		);
		dsDescription.addProperty(Pav.createdWith, voidModel.createResource("https://github.com/wikipathways/GPML2RDF/tree/v3"));
		dsDescription.addLiteral(DCTerms.description, descriptionLiteral);

		
		
		// define the dataset info
		Resource voidBase = voidModel.createResource("http://rdf.wikipathways.org/" + date + "/");
		voidBase.addProperty(
			voidModel.createProperty("http://www.w3.org/ns/dcat#landingPage"),
			voidModel.createResource("http://www.wikipathways.org/")
		);
		Resource wpHomeBase = voidModel.createResource("http://www.wikipathways.org/");
		Resource authorResource = voidModel.createResource("https://jenkins.bigcat.unimaas.nl/job/GPML%20to%20GPML%20+%20WP%20RDF/");
		Resource apiResource = voidModel.createResource("http://www.wikipathways.org/wpi/webservice/webservice.php");
		Resource mainDatadump = voidModel.createResource("http://data.wikipathways.org/" + date + "/rdf/wikipathways-" + date + "-rdf-wp.zip");
		Resource license = voidModel.createResource("http://creativecommons.org/licenses/by/3.0/");
		Resource instituteResource = voidModel.createResource("http://maastichtuniversity.nl/");
		voidBase.addProperty(RDF.type, Void.Dataset);
		voidBase.addProperty(FOAF.homepage, wpHomeBase);
		voidBase.addProperty(DCTerms.license, license);
		voidBase.addLiteral(Void.uriSpace, "http://rdf.wikipathways.org/wp/");
		voidBase.addLiteral(Void.uriSpace, "http://identifiers.org");
		voidBase.addProperty(Pav.importedBy, authorResource);
		voidBase.addProperty(Pav.importedFrom, apiResource);
		voidBase.addProperty(Pav.importedOn, nowLiteral);
		Resource distribution = voidModel.createResource("http://rdf.wikipathways.org/wp/distribution");
		distribution.addProperty(RDF.type, voidModel.createResource("http://www.w3.org/ns/dcat#Distribution"));
		distribution.addLiteral(
			voidModel.createProperty("http://www.w3.org/ns/dcat#mediaType"), "application/zip"
		);
		distribution.addProperty(
			voidModel.createProperty("http://www.w3.org/ns/dcat#downloadURL"), mainDatadump
		);
		voidBase.addProperty(
			voidModel.createProperty("http://www.w3.org/ns/dcat#distribution"), distribution
		);
		voidBase.addProperty(
			voidModel.createProperty("http://purl.org/dc/terms/accuralPeriodicity"),
			voidModel.createResource("http://purl.org/cld/freq/irregular")
		);
		voidBase.addProperty(Pav.createdBy, authorResource);
		voidBase.addProperty(Pav.createdAt, instituteResource);		 
		voidBase.addLiteral(Pav.createdOn, nowLiteral);
		voidBase.addProperty(DCTerms.subject, Biopax_level3.Pathway);
		voidBase.addProperty(
			voidModel.createProperty("http://www.w3.org/ns/dcat#theme"),
			voidModel.createResource("http://semanticscience.org/resource/SIO_001107")
		);
		voidBase.addProperty(Void.exampleResource, voidModel.createResource("http://identifiers.org/ncbigene/2678"));
		voidBase.addProperty(Void.exampleResource, voidModel.createResource("http://identifiers.org/pubmed/15215856"));
		voidBase.addProperty(Void.exampleResource, voidModel.createResource("http://identifiers.org/hmdb/HMDB02005"));
		voidBase.addProperty(Void.exampleResource, voidModel.createResource("http://rdf.wikipathways.org/WP15"));
		voidBase.addProperty(Void.exampleResource, voidModel.createResource("http://identifiers.org/obo.chebi/17242"));

		for (Organism organism : SPECIES.keySet()) {
			voidBase.addProperty(
				DCTerms.subject,
				voidModel.createResource("http://identifiers.org/taxonomy/" + organism.taxonomyID())
			);
		}
		voidBase.addProperty(Void.vocabulary, Biopax_level3.NAMESPACE);
		voidBase.addProperty(Void.vocabulary, voidModel.createResource(Wp.getURI()));
		voidBase.addProperty(Void.vocabulary, voidModel.createResource(Gpml.getURI()));
		voidBase.addProperty(Void.vocabulary, FOAF.NAMESPACE);
		voidBase.addProperty(Void.vocabulary, Pav.NAMESPACE);
	}

	private static boolean isIncludedTag(WSCurationTag[] tags) {
		for (WSCurationTag tag : tags) {
			if (INCLUDED_TAGS.contains(tag.getName())) return true;
		}
		return false;
	}
	
	public static IDMapperStack maps () throws FileNotFoundException, IOException, ClassNotFoundException, IDMapperException{
		final Properties prop = new Properties();
		prop.load(new FileInputStream("/tmp/OPSBRIDGEDB/config.properties"));
		IDMapperStack mapper = GpmlConverter.createBridgeDbMapper(prop);
		return mapper;
		
	}

}
