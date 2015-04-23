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
package org.wikipathways.wp2rdf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.MGroup;
import org.pathvisio.core.model.MLine;
import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.view.MIMShapes;
import org.wikipathways.wp2rdf.converter.DataNodeConverter;
import org.wikipathways.wp2rdf.converter.GroupConverter;
import org.wikipathways.wp2rdf.converter.InteractionConverter;
import org.wikipathways.wp2rdf.converter.LabelConverter;
import org.wikipathways.wp2rdf.converter.LineConverter;
import org.wikipathways.wp2rdf.converter.PathwayConverter;
import org.wikipathways.wp2rdf.converter.ShapeConverter;
import org.wikipathways.wp2rdf.converter.StateConverter;
import org.wikipathways.wp2rdf.utils.DataStorage;
import org.wikipathways.wp2rdf.utils.Utils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * 
 * @author mkutmon
 * @author ryanmiller
 *
 */
public class WP2RDF {

	public static void main(String[] args) {
//		File pathwayFile = new File("resources/WP2447_75221.gpml");
//		File pathwayFile = new File("resources/WP2059_79765.gpml");
//		File pathwayFile = new File("resources/test-simple-conversion.gpml");
		File pathwayFile = new File("resources/test-advanced-conversion.gpml");

		File bridgeDbFile = new File("/home/tina/Data/BridgeDb/Hs_Derby_20130701.bridge");
		File bridgeDbFileMet = new File("/home/tina/Data/BridgeDb/metabolites_20140516.bridge");

		
		try {	
			MIMShapes.registerShapes();
			Pathway p = PathwayReader.readPathway(pathwayFile);
			
			
			IDMapper geneMapper = Utils.setUpIDMapper(bridgeDbFile);
			IDMapper metMapper = Utils.setUpIDMapper(bridgeDbFileMet);
			
			// TODO: get pathway id
			String wpId = "WP2447";
			// TODO: get revision
			String revision = "75221";
			
			Model pathwayModel = ModelFactory.createDefaultModel();
			Utils.setModelPrefix(pathwayModel);
			
			Resource pathwayRes = PathwayConverter.parsePathwayInfo(p, wpId, revision, pathwayModel);
			DataStorage data = new DataStorage(p, pathwayRes);
			
			for(PathwayElement element : p.getDataObjects()) {
				if(element.getObjectType().equals(ObjectType.DATANODE)) {
					DataNodeConverter.parseDataNodes(element, pathwayModel, geneMapper, metMapper, data);
				} else if (element.getObjectType().equals(ObjectType.LABEL)) {
					LabelConverter.parseLabel(element, pathwayModel, data);
				} else if (element.getObjectType().equals(ObjectType.LINE)) {
					LineConverter.parseLineGpml(element, pathwayModel, data);
				} else if (element.getObjectType().equals(ObjectType.SHAPE)) {
					ShapeConverter.parseShape(element, pathwayModel, data);
				} else if (element.getObjectType().equals(ObjectType.STATE)) {
					StateConverter.parseState(element, pathwayModel, data);
				} else if (element.getObjectType().equals(ObjectType.GROUP)) {
					GroupConverter.parseGroup((MGroup) element, pathwayModel, data);
				}
			}
			
			// retrieve semantic interactions
			for(PathwayElement element : p.getDataObjects()) {
				if (element.getObjectType().equals(ObjectType.LINE)) {
					InteractionConverter.parseInteractionSemantics((MLine)element, pathwayModel, data);
				}
			}
			
			pathwayModel.write(new FileWriter(new File("output.owl")), "TURTLE");
		} catch (ConverterException e) {
			System.out.println("couldn't read pathway file " + pathwayFile.getAbsolutePath());
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IDMapperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
