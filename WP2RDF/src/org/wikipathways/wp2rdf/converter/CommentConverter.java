package org.wikipathways.wp2rdf.converter;

import org.pathvisio.core.model.PathwayElement.Comment;
import org.wikipathways.wp2rdf.ontologies.GpmlNew;
import org.wikipathways.wp2rdf.utils.DataStorage;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;

public class CommentConverter {

	public static void parseCommentGpml(Comment comment, Model model, Resource parent, DataStorage data) {
		Resource commentRes = model.createResource(data.getPathwayRes().getURI() + "/Comment/" + data.getPathway().getUniqueGraphId());
		
		commentRes.addProperty(DC.type, GpmlNew.COMMENT);
		
		if(comment.getSource() != null) commentRes.addLiteral(GpmlNew.SOURCE, comment.getSource());
		commentRes.addLiteral(GpmlNew.COMMENT_TEXT, comment.getComment());
		
		parent.addProperty(GpmlNew.HAS_COMMENT, commentRes);
		commentRes.addProperty(DCTerms.isPartOf, parent);
		
		data.getComments().put(comment, commentRes);
	}
}
