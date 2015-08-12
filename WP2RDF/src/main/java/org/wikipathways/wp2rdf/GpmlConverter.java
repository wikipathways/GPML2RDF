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

import org.pathvisio.core.model.MGroup;
import org.pathvisio.core.model.MLine;
import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.wikipathways.wp2rdf.converter.DataNodeConverter;
import org.wikipathways.wp2rdf.converter.GraphicalLineConverter;
import org.wikipathways.wp2rdf.converter.GroupConverter;
import org.wikipathways.wp2rdf.converter.InfoBoxConverter;
import org.wikipathways.wp2rdf.converter.InteractionConverter;
import org.wikipathways.wp2rdf.converter.LabelConverter;
import org.wikipathways.wp2rdf.converter.PathwayConverter;
import org.wikipathways.wp2rdf.converter.ShapeConverter;
import org.wikipathways.wp2rdf.converter.StateConverter;
import org.wikipathways.wp2rdf.utils.DataHandlerGpml;
import org.wikipathways.wp2rdf.utils.DataHandlerWp;
import org.wikipathways.wp2rdf.utils.Utils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Class that converts a pathway
 * in a RDF model - only GPML vocabulary
 * 
 * This work is based on Andra Waagmeester's work.
 * 
 * @author mkutmon
 * @author ryanmiller
 *
 */
public class GpmlConverter {

	public static Model convertGpml(Pathway p, String wpId, String revision) {
		Model pathwayModel = ModelFactory.createDefaultModel();
		Utils.setModelPrefix(pathwayModel);
		convertGpml(p, wpId, revision, pathwayModel);
		return pathwayModel;
	}
	
	public static void convertGpml(Pathway p, String wpId, String revision, Model pathwayModel) {
		Resource pathwayRes = PathwayConverter.parsePathwayInfoGpml(p, wpId, revision, pathwayModel);
		DataHandlerGpml data = new DataHandlerGpml(p, pathwayRes);
		
		for(PathwayElement e : p.getDataObjects()) {
			if(e.getObjectType().equals(ObjectType.DATANODE)) {
				DataNodeConverter.parseDataNodesGpml(e, pathwayModel, data);
			} else if(e.getObjectType().equals(ObjectType.STATE)) {
				StateConverter.parseStateGpml(e, pathwayModel, data);
			} else if(e.getObjectType().equals(ObjectType.LINE)) {
				InteractionConverter.parseInteractionGpml((MLine) e, pathwayModel, data);
			} else if(e.getObjectType().equals(ObjectType.GRAPHLINE)) {
				GraphicalLineConverter.parseInteractionGpml((MLine) e, pathwayModel, data);
			} else if(e.getObjectType().equals(ObjectType.LABEL)) {
				LabelConverter.parseLabelGpml(e, pathwayModel, data);
			} else if(e.getObjectType().equals(ObjectType.SHAPE)) {
				ShapeConverter.parseShapeGpml(e, pathwayModel, data);
			} else if(e.getObjectType().equals(ObjectType.GROUP)) {
				GroupConverter.parseGroupGpml((MGroup) e, pathwayModel, data);
			} else if(e.getObjectType().equals(ObjectType.INFOBOX)) {
				InfoBoxConverter.parseInfoBoxGpml(e, pathwayModel, data);
			}
		}
	}
		
	public static Model convertWp(Pathway p, String wpId, String revision) {
		Model pathwayModel = ModelFactory.createDefaultModel();
		Utils.setModelPrefix(pathwayModel);
		convertWp(p, wpId, revision, pathwayModel);
		return pathwayModel;
	}
	
	public static void convertWp(Pathway p, String wpId, String revision, Model pathwayModel) {
		Resource pathwayRes = PathwayConverter.parsePathwayInfoWp(p, wpId, revision, pathwayModel);
		DataHandlerWp data = new DataHandlerWp(p, wpId, revision, pathwayRes);
		
		for(PathwayElement e : p.getDataObjects()) {
			if(e.getObjectType().equals(ObjectType.DATANODE)) {
				DataNodeConverter.parseDataNodeWp(e, pathwayModel, data);
			}
		}
		for(PathwayElement e : p.getDataObjects()) {
			if(e.getObjectType().equals(ObjectType.GROUP)) {
				GroupConverter.parseComplexWp((MGroup) e, pathwayModel, data);
			}
		}
		for(PathwayElement e : p.getDataObjects()) {
			if(e.getObjectType().equals(ObjectType.LINE)) {
				InteractionConverter.parseInteractionWp((MLine)e, pathwayModel, data);
			}
		}
	}
	
}

