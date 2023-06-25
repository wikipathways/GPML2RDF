package org.wikipathways.wp2rdf.bin;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collections;

import org.apache.jena.rdf.model.Model;
import org.pathvisio.core.model.Pathway;
import org.wikipathways.wp2rdf.GpmlConverter;
import org.wikipathways.wp2rdf.io.PathwayReader;

public class CreateGPMLRDF {

    public static void main(String[] args) throws Exception {
    	if (args.length < 3) {
    		System.out.println("Syntax: CreateGPMLRDF <GPML> <OUTPUT> <REVISION>");
    		System.exit(0);
    	}

    	String gpmlFile = args[0];
        String wpid     = gpmlFile.substring(5,11);
        String rev      = args[2];
        String outFile  = args[1];

        InputStream input = new FileInputStream(gpmlFile);
        Pathway pathway = PathwayReader.readPathway(input);
        input.close();

        Model model = GpmlConverter.convertGpml(pathway, wpid, rev, Collections.<String>emptyList());
        FileOutputStream output = new FileOutputStream(outFile);
        model.write(output, "TURTLE");
        output.close();
    }

}
