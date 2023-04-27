package org.wikipathways.wp2rdf.bin;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collections;

import org.apache.jena.rdf.model.Model;
import org.bridgedb.IDMapperStack;
import org.pathvisio.core.model.Pathway;
import org.wikipathways.wp2rdf.GpmlConverter;
import org.wikipathways.wp2rdf.WPREST2RDF;
import org.wikipathways.wp2rdf.io.PathwayReader;

public class CreateRDF {

    public static void main(String[] args) throws Exception {
        String gpmlFile = args[0];
        String wpid     = gpmlFile.substring(5,gpmlFile.indexOf("."));
        String rev      = args[2];
        String outFile  = args[1];

        // DataSourceTxt.init();
        InputStream input = new FileInputStream(gpmlFile);
        Pathway pathway = PathwayReader.readPathway(input);
        input.close();

        IDMapperStack stack = WPREST2RDF.maps();
        Model model = GpmlConverter.convertWp(pathway, wpid, rev, stack, Collections.<String>emptyList());
        FileOutputStream output = new FileOutputStream(outFile);
        model.write(output, "TURTLE");
        output.flush();
        output.close();
    }

}
