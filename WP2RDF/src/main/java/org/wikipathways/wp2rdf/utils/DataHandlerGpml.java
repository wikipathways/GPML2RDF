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
package org.wikipathways.wp2rdf.utils;

import java.util.HashMap;
import java.util.Map;

import org.pathvisio.core.biopax.PublicationXref;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.model.PathwayElement.Comment;
import org.pathvisio.core.model.PathwayElement.MAnchor;
import org.pathvisio.core.model.PathwayElement.MPoint;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * 
 * @author mkutmon
 * @author ryanmiller
 *
 */
public class DataHandlerGpml {
	private Map<PathwayElement, Resource> pathwayElements;
	private Map<MPoint, Resource> points;
	private Map<MAnchor, Resource> anchors;
	private Map<Comment, Resource> comments;
	private Map<PublicationXref, Resource> pubXrefs;

	private Pathway pathway;
	private Resource pathwayRes;

	public DataHandlerGpml(Pathway p, Resource pathwayRes) {
		pathway = p;
		this.pathwayRes = pathwayRes;
		pathwayElements = new HashMap<PathwayElement, Resource>();
		points = new HashMap<MPoint, Resource>();
		anchors = new HashMap<MAnchor, Resource>();
		comments = new HashMap<Comment, Resource>();
		pubXrefs = new HashMap<PublicationXref, Resource>();
	}
	
	public Map<PublicationXref, Resource> getPubXrefs() {
		return pubXrefs;
	}

	public Map<Comment, Resource> getComments() {
		return comments;
	}

	public Map<PathwayElement, Resource> getPathwayElements() {
		return pathwayElements;
	}

	public Map<MPoint, Resource> getPoints() {
		return points;
	}

	public Map<MAnchor, Resource> getAnchors() {
		return anchors;
	}

	public Pathway getPathway() {
		return pathway;
	}

	public Resource getPathwayRes() {
		return pathwayRes;
	}
}
