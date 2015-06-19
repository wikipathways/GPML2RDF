package org.wikipathways.wp2rdf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.bridgedb.bio.Organism;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.wikipathways.webservice.WSPathwayInfo;
import org.wikipathways.client.WikiPathwaysClient;
import org.wikipathways.wp2rdf.utils.Utils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class WPREST2RDF {

	@SuppressWarnings("serial")
	public static void main(String[] args) throws NumberFormatException, ConverterException, IOException {
		URL url = new URL("http://webservice.wikipathways.org");
		WikiPathwaysClient client = new WikiPathwaysClient(url);

		Map<Organism,String> species = new HashMap<Organism,String>() {{
			put(Organism.ArabidopsisThaliana, "arabidopsisthaliana");
		}};

		for (Organism organism : species.keySet()) {
			WSPathwayInfo [] pathways = client.listPathways(organism);
			for(WSPathwayInfo pwInfo : pathways) {
				System.out.println(pwInfo.getId() + "\t" + pwInfo.getRevision());

				Model pathwayModel = ModelFactory.createDefaultModel();
				Utils.setModelPrefix(pathwayModel);

				Pathway p = WikiPathwaysClient.toPathway(client.getPathway(pwInfo.getId(), Integer.parseInt(pwInfo.getRevision())));

				// New conversion of the pathway in GPML vocabulary
				GpmlConverter.convertGpml(p, pwInfo.getId(), pwInfo.getRevision(), pathwayModel);

				String folder = "output/gpml/" + species.get(organism) + "/";
				new File(folder).mkdirs();
				FileWriter output = new FileWriter(folder + pwInfo.getId() +".ttl");
				pathwayModel.write(output, "TURTLE");
				output.close();

				// New conversion of the pathway in WP vocabulary
				pathwayModel = ModelFactory.createDefaultModel();
				Utils.setModelPrefix(pathwayModel);
				GpmlConverter.convertWp(p, pwInfo.getId(), pwInfo.getRevision(), pathwayModel);

				folder = "output/wp/" + species.get(organism) + "/";
				new File(folder).mkdirs();
				output = new FileWriter(folder + pwInfo.getId() +".ttl");
				pathwayModel.write(output, "TURTLE");
				output.close();
			}
		}
	}

}
