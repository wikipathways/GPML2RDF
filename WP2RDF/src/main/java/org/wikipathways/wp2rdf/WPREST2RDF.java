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
import org.wikipathways.wp2rdf.ontologies.Wp;
import org.wikipathways.wp2rdf.ontologies.Void;
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
		String folder = "output/wp/";
		Writer output = new FileWriter(folder + "voidWp.ttl");
		Model voidWp = ModelFactory.createDefaultModel();
		Map<String, String> voidInfo = new HashMap<>();
		populateVOID(voidWp, voidInfo);
		voidWp.write(output, "TURTLE");
		output.close();
		folder = "output/gpml/";
		output = new FileWriter(folder + "voidGpml.ttl");
		Model voidGpml = ModelFactory.createDefaultModel();
		voidGpml.write(output, "TURTLE");
		output.close();

	}

	private static void populateVOID(Model voidModel, Map<String, String> voidInfo) {
		//Populate void.ttl
		Calendar now = Calendar.getInstance();
		Literal nowLiteral = voidModel.createTypedLiteral(now);
		String datasetTitle = voidInfo.get("voidDatasetTitle");
		String voidShortCode = voidInfo.get("voidShortCode");
		Literal titleLiteral = voidModel.createLiteral(datasetTitle + " VoID Description", "en");
		Literal descriptionLiteral = voidModel.createLiteral(
			"This is the VoID description for a " + datasetTitle + " dataset.", "en"
		);
		Resource voidBase = voidModel.createResource("http://rdf.wikipathways.org/" + voidShortCode + "/");
		voidBase.addProperty(
			voidModel.createProperty("http://www.w3.org/ns/dcat#landingPage"),
			voidModel.createResource("http://www.wikipathways.org/")
		);
		Resource wpHomeBase = voidModel.createResource("http://www.wikipathways.org/");
		Resource authorResource = voidModel.createResource(voidInfo.get("voidAuthor"));
		Resource apiResource = voidModel.createResource("http://www.wikipathways.org/wpi/webservice/webservice.php");
		Resource mainDatadump = voidModel.createResource(voidInfo.get("voidDownload"));
		Resource license = voidModel.createResource("http://creativecommons.org/licenses/by/3.0/");
		Resource instituteResource = voidModel.createResource(voidInfo.get("voidInstitute"));
		voidBase.addProperty(RDF.type, Void.Dataset);
		voidBase.addProperty(DCTerms.title, titleLiteral);
		voidBase.addProperty(DCTerms.description, descriptionLiteral);
		voidBase.addProperty(FOAF.homepage, wpHomeBase);
		voidBase.addProperty(DCTerms.license, license);
		voidBase.addLiteral(Void.uriSpace, "http://rdf.wikipathways.org/" + voidShortCode + "/");
		voidBase.addLiteral(Void.uriSpace, "http://identifiers.org");
		voidBase.addProperty(Pav.importedBy, authorResource);
		voidBase.addProperty(Pav.importedFrom, apiResource);
		voidBase.addProperty(Pav.importedOn, nowLiteral);
		Resource distribution = voidModel.createResource("http://rdf.wikipathways.org/" + voidShortCode + "/distribution");
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
