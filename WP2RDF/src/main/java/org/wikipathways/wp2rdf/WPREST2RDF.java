package org.wikipathways.wp2rdf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bridgedb.bio.Organism;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.wikipathways.webservice.WSCurationTag;
import org.pathvisio.wikipathways.webservice.WSPathwayInfo;
import org.wikipathways.client.WikiPathwaysClient;
import org.wikipathways.wp2rdf.utils.Utils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class WPREST2RDF {

	@SuppressWarnings("serial")
	private static final List<String> INCLUDED_TAGS = new ArrayList<String>() {{
		add("Curation:AnalysisCollection");
		add("Curation:Reactome_Approved");
	}};

	@SuppressWarnings("serial")
	private static final Map<Organism,String> SPECIES = new HashMap<Organism,String>() {{
		put(Organism.HomoSapiens, "human");
	}};

	public static void main(String[] args) throws NumberFormatException, ConverterException, IOException {
		URL url = new URL("http://webservice.wikipathways.org");
		WikiPathwaysClient client = new WikiPathwaysClient(url);

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
				if (gotTags && isIncludedTag(tags)) {
					Model pathwayModel = ModelFactory.createDefaultModel();
					Utils.setModelPrefix(pathwayModel);

					Pathway p = WikiPathwaysClient.toPathway(client.getPathway(pwInfo.getId(), Integer.parseInt(pwInfo.getRevision())));

					// New conversion of the pathway in GPML vocabulary
					GpmlConverter.convertGpml(p, pwInfo.getId(), pwInfo.getRevision(), pathwayModel);

					String folder = "output/gpml/" + SPECIES.get(organism) + "/";
					new File(folder).mkdirs();
					FileWriter output = new FileWriter(folder + pwInfo.getId() +".ttl");
					pathwayModel.write(output, "TURTLE");
					output.close();

					// New conversion of the pathway in WP vocabulary
					pathwayModel = ModelFactory.createDefaultModel();
					Utils.setModelPrefix(pathwayModel);
					GpmlConverter.convertWp(p, pwInfo.getId(), pwInfo.getRevision(), pathwayModel);

					folder = "output/wp/" + SPECIES.get(organism) + "/";
					new File(folder).mkdirs();
					output = new FileWriter(folder + pwInfo.getId() +".ttl");
					pathwayModel.write(output, "TURTLE");
					output.close();
				}
			}
		}
	}

	private static boolean isIncludedTag(WSCurationTag[] tags) {
		for (WSCurationTag tag : tags) {
			if (INCLUDED_TAGS.contains(tag.getName())) return true;
		}
		return false;
	}

}
