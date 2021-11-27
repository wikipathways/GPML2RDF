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

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.pathvisio.core.model.PathwayElement.Comment;
import org.wikipathways.wp2rdf.ontologies.Gpml;
import org.wikipathways.wp2rdf.utils.DataHandlerGpml;

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
		
		commentRes.addProperty(RDF.type, Gpml.COMMENT);
		
		if(comment.getSource() != null) commentRes.addLiteral(Gpml.SOURCE, comment.getSource());
		String commentStr = comment.getComment();
		if (commentStr != null) commentRes.addLiteral(Gpml.COMMENT_TEXT, commentStr);
		
		parent.addProperty(Gpml.HAS_COMMENT, commentRes);
		commentRes.addProperty(DCTerms.isPartOf, parent);
		
		data.getComments().put(comment, commentRes);
	}
	
}
