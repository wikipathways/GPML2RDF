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
package org.wikipathways.wp2rdf.converter;

import org.pathvisio.core.model.PathwayElement;
import org.wikipathways.wp2rdf.ontologies.GpmlNew;
import org.wikipathways.wp2rdf.utils.DataHandler;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * 
 * @author mkutmon
 * @author ryanmiller
 *
 */
public class InfoBoxConverter {
	
	/**
	 * conversion only GPML vocabulary
	 */
	public static void parseInfoBoxGpml(PathwayElement e, Model model, DataHandler data) {
		String graphId = e.getGraphId();
		if(e.getGraphId() == null) {
			graphId = data.getPathway().getUniqueGraphId();
		}
		Resource infoboxRes = model.createResource(data.getPathwayRes().getURI() + "/InfoBox/" + graphId);

		infoboxRes.addProperty(RDF.type, GpmlNew.INFO_BOX);
		infoboxRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());		
		data.getPathwayRes().addProperty(GpmlNew.HAS_INFO_BOX, infoboxRes);
		
		infoboxRes.addLiteral(GpmlNew.CENTER_X, e.getMCenterX());
		infoboxRes.addLiteral(GpmlNew.CENTER_Y, e.getMCenterY());
		
		data.getPathwayElements().put(e, infoboxRes);
	}
}
