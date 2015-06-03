package org.wikipathways.wp2rdf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import org.bridgedb.bio.Organism;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.wikipathways.webservice.WSPathwayInfo;
import org.wikipathways.client.WikiPathwaysClient;
import org.wikipathways.wp2rdf.utils.Utils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class WPREST2RDF {

	public static void main(String[] args) throws NumberFormatException, ConverterException, IOException {
		URL url = new URL("http://webservice.wikipathways.org");
		WikiPathwaysClient client = new WikiPathwaysClient(url);
		
		WSPathwayInfo [] pathways = client.listPathways(Organism.ArabidopsisThaliana);
		for(WSPathwayInfo i : pathways) {
			System.out.println(i.getId() + "\t" + i.getRevision());
			
			File output = new File("resources/testcollection/" + i.getId() +".ttl");
			
			Model pathwayModel = ModelFactory.createDefaultModel();
			Utils.setModelPrefix(pathwayModel);
			
			Pathway p = WikiPathwaysClient.toPathway(client.getPathway(i.getId(), Integer.parseInt(i.getRevision())));

			// New conversion of the pathway in GPML vocabulary
			GpmlConverter.convertGpml(p, i.getId(), i.getRevision(), pathwayModel);
			
			// New conversion of the pathway in WP vocabulary
			GpmlConverter.convertWp(p, i.getId(), i.getRevision(), pathwayModel);
			
			pathwayModel.write(new FileWriter(output), "TURTLE");
		}
	}

}
