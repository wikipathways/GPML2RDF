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

import org.pathvisio.core.model.PathwayElement.Comment;
import org.wikipathways.wp2rdf.ontologies.GpmlNew;
import org.wikipathways.wp2rdf.utils.DataHandlerGpml;

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
public class CommentConverter {

	/**
	 * conversion only GPML vocabulary
	 */
	public static void parseCommentGpml(Comment comment, Model model, Resource parent, DataHandlerGpml data) {
		Resource commentRes = model.createResource(data.getPathwayRes().getURI() + "/Comment/" + data.getPathway().getUniqueGraphId());
		
		commentRes.addProperty(RDF.type, GpmlNew.COMMENT);
		
		if(comment.getSource() != null) commentRes.addLiteral(GpmlNew.SOURCE, comment.getSource());
		commentRes.addLiteral(GpmlNew.COMMENT_TEXT, comment.getComment());
		
		parent.addProperty(GpmlNew.HAS_COMMENT, commentRes);
		commentRes.addProperty(DCTerms.isPartOf, parent);
		
		data.getComments().put(comment, commentRes);
	}
}
