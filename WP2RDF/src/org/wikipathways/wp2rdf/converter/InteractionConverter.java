package org.wikipathways.wp2rdf.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pathvisio.core.biopax.PublicationXref;
import org.pathvisio.core.model.LineStyle;
import org.pathvisio.core.model.LineType;
import org.pathvisio.core.model.MLine;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.model.PathwayElement.Comment;
import org.pathvisio.core.model.PathwayElement.MAnchor;
import org.pathvisio.core.model.PathwayElement.MPoint;
import org.pathvisio.core.view.MIMShapes;
import org.wikipathways.wp2rdf.ontologies.Gpml;
import org.wikipathways.wp2rdf.ontologies.GpmlNew;
import org.wikipathways.wp2rdf.ontologies.Wp;
import org.wikipathways.wp2rdf.utils.DataStorage;
import org.wikipathways.wp2rdf.utils.Utils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;

public class InteractionConverter {

	public static void parseInteractionGpml(MLine e, Model model, DataStorage data) {
		Resource intRes = model.createResource(data.getPathwayRes().getURI() + "/Interaction/" + e.getGraphId());
		
		intRes.addProperty(DC.type, GpmlNew.INTERACTION);
		data.getPathwayRes().addProperty(GpmlNew.HAS_INTERACTION, intRes);
		intRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
		
		intRes.addLiteral(GpmlNew.LINE_THICKNESS, e.getLineThickness());
		intRes.addLiteral(GpmlNew.GRAPH_ID, e.getGraphId());
		intRes.addLiteral(GpmlNew.COLOR, Utils.colorToHex(e.getColor()));
		intRes.addLiteral(GpmlNew.LINE_STYLE, e.getLineStyle() != LineStyle.DASHED ? "Solid" : "Broken");
		intRes.addLiteral(GpmlNew.ZORDER, e.getZOrder());
		intRes.addLiteral(GpmlNew.CONNECTOR_TYPE, e.getConnectorType().getName());
		
		if(e.getXref() != null && e.getXref().getId() != null && e.getXref().getDataSource() != null) {
			intRes.addLiteral(GpmlNew.XREF_ID, e.getXref().getId());
			intRes.addLiteral(GpmlNew.XREF_DATASOURCE, e.getXref().getDataSource().getFullName());
		}
		
		if(e.getGroupRef() != null) intRes.addLiteral(GpmlNew.GROUP_REF, e.getGroupRef());
		
		// TODO: in schema there is an interaction type but that's not in the data model. 
		
		for(MAnchor a : e.getMAnchors()) {
			AnchorConverter.parseAnchorGpml(a, model, intRes, data);
		}
		
		for(MPoint p : e.getMPoints()) {
			if(p.equals(e.getStartPoint())) {
				PointConverter.parsePointGpml(p, model, intRes, data, e.getStartLineType().getName());
			} else if (p.equals(e.getEndPoint())) {
				PointConverter.parsePointGpml(p, model, intRes, data, e.getEndLineType().getName());
			} else {
				PointConverter.parsePointGpml(p, model, intRes, data, null);
			}
		}

		for(String s : e.getBiopaxRefs()) {
			intRes.addLiteral(GpmlNew.BIOPAX_REF, s);
		}
		
		for(Comment c : e.getComments()) {
			CommentConverter.parseCommentGpml(c, model, intRes, data);
		}
		
		for(PublicationXref xref : e.getBiopaxReferenceManager().getPublicationXRefs()) {
			PublicationXrefConverter.parsePublicationXrefGpml(xref, intRes, model, data);
		}
			
		data.getPathwayElements().put(e, intRes);
	}
	
	public static void parseInteractionSemantics(MLine e, Model model, DataStorage data) {
	
		List<MLine> participatingLines = new ArrayList<MLine>();
		participatingLines.add(e);
		List<MLine> regLines = new ArrayList<MLine>();
		
		// TODO: implement better way to get lines that are connected to anchor in PV
		for(MAnchor a : e.getMAnchors()) {
			for(MPoint p : data.getPoints().keySet()) {
				if(p.getGraphRef() != null) {
					if(p.getGraphRef().equals(a.getGraphId())) {
						MLine interactingLine = (MLine) p.getParent();
						
						
						if(interactingLine.getStartGraphRef() != null && interactingLine.getEndGraphRef() != null) {
							if(interactingLine.getStartGraphRef().equals(a.getGraphId())) {
								if(interactingLine.getStartLineType().equals(LineType.LINE)) {
									if(!participatingLines.contains(interactingLine)) participatingLines.add(interactingLine);
								} else {
									if(!regLines.contains(interactingLine)) regLines.add(interactingLine);
								}
							} 
							if(interactingLine.getEndGraphRef().equals(a.getGraphId())) {
								if(interactingLine.getEndLineType().equals(LineType.LINE)) {
									if(!participatingLines.contains(interactingLine)) participatingLines.add(interactingLine);
								} else {
									if(!regLines.contains(interactingLine)) regLines.add(interactingLine);
								}
							} 
						}
					}
				}
			}			
		}
		
		List<Resource> participants = new ArrayList<Resource>();
		for(MLine l : participatingLines) {
			if(l.getStartGraphRef() != null) {
				PathwayElement pwEle = data.getPathway().getElementById(l.getStartGraphRef());
				if(pwEle != null) {
					Resource res = data.getPathwayElements().get(pwEle);
					if(res != null && res.getProperty(DC.identifier) != null) {
						participants.add(res);
					}
				}
			}
			if(l.getEndGraphRef() != null) {
				PathwayElement pwEle = data.getPathway().getElementById(l.getEndGraphRef());
				if(pwEle != null) {
					Resource res = data.getPathwayElements().get(pwEle);
					if(res != null && res.getProperty(DC.identifier) != null) {
						participants.add(res);
					}
				}
			}
		}
		
		if(participants.size() >= 2) {
			// find out what is input and what is output
			List<Resource> sources = new ArrayList<Resource>();
			List<Resource> targets = new ArrayList<Resource>();
			Map<LineType, List<Resource>> intTypes = new HashMap<LineType, List<Resource>>();
			
			for(MLine l : participatingLines) {
				for(Resource r : participants) {
					if(l.getStartGraphRef().equals(r.getProperty(Gpml.graphid).getString())) {
						if(l.getStartLineType().equals(LineType.LINE)) {
							if(!sources.contains(r)) {
								sources.add(r);
							}
						} else {
							if(!targets.contains(r)) {
								targets.add(r);
							}
							if(intTypes.containsKey(l.getStartLineType())) {
								intTypes.get(l.getStartLineType()).add(r);
							} else {
								ArrayList<Resource> list = new ArrayList<Resource>();
								list.add(r);
								intTypes.put(l.getStartLineType(), list);
							}
						}
					}
					if(l.getEndGraphRef().equals(r.getProperty(Gpml.graphid).getString())) {
						if(l.getEndLineType().equals(LineType.LINE)) {
							if(!sources.contains(r)) {
								sources.add(r);
							}
						} else {
							if(!targets.contains(r)) {
								targets.add(r);
							}
							if(intTypes.containsKey(l.getEndLineType())) {
								intTypes.get(l.getEndLineType()).add(r);
							} else {
								ArrayList<Resource> list = new ArrayList<Resource>();
								list.add(r);
								intTypes.put(l.getEndLineType(), list);
							}
						}
					}
				}
			}
			if(intTypes.size() == 1) {
				LineType lineType = intTypes.keySet().iterator().next();
				Resource interaction = model.createResource(data.getPathwayRes().getURI() + "/Interaction/" + data.getPathway().getUniqueGraphId());
				createInteraction(lineType, sources, targets, interaction, data);
				for(MLine l : participatingLines) {
					Resource res = data.getPathwayElements().get(l);
					res.addProperty(DCTerms.isPartOf, interaction);
				}
			} else {
				System.out.println("ERRRORRRR!!!");
			}
		}
		
//		System.out.println(e.getGraphId() + "\t" + participants.size());
		
//		System.out.println("Regulating Lines\t" + regLines.size());
//		for(MLine l : regLines) {
//			System.out.println("\t>> " + l.getGraphId());
//		}
		
		
		
//		if(e.getStartGraphRef() != null && e.getEndGraphRef() != null) {
//			Resource start = data.getPathwayElements().get(data.getPathway().getElementById(e.getStartGraphRef()));
//			Resource end = data.getPathwayElements().get(data.getPathway().getElementById(e.getEndGraphRef()));
//			if(start != null && end != null) {
//				if(start.getProperty(DC.identifier) != null && end.getProperty(DC.identifier) != null) {
//					System.out.println(e.getGraphId() + "\t" + e.getStartLineType() + "\t" + e.getEndLineType());
//
//					LineType startLine = e.getStartLineType();
//					LineType endLine = e.getEndLineType();
//						
//					Resource interaction1 = null;
//					Resource interaction2 = null;
//					if(startLine.equals(LineType.LINE) && endLine.equals(LineType.LINE)) {
//						interaction1 = model.createResource(data.getPathwayRes().getURI() + "/Interaction/" + data.getPathway().getUniqueGraphId());
//						interaction1.addProperty(RDF.type, Wp.Interaction);
//						interaction1.addProperty(RDF.type, Wp.Relation);
//						interaction1.addProperty(DCTerms.isPartOf, data.getPathwayRes());
//						interaction1.addProperty(Wp.hasParticipant, start);
//						interaction1.addProperty(Wp.hasParticipant, end);
//						interaction1.addProperty(RDF.type, Wp.UndirectedInteraction);
//						data.getPathwayElements().get(e).addProperty(DCTerms.isPartOf, interaction1);
//					} else {					
//						if(!startLine.equals(LineType.LINE)) {
//							// TODO: get better id!!!
//							interaction1 = model.createResource(data.getPathwayRes().getURI() + "/Interaction/" + data.getPathway().getUniqueGraphId());
//							createInteraction(startLine, end, start, interaction1, data);
//							data.getPathwayElements().get(e).addProperty(DCTerms.isPartOf, interaction1);
//						} 
//						if(!endLine.equals(LineType.LINE)) {
//							// TODO: get better id!!!
//							interaction2 = model.createResource(data.getPathwayRes().getURI() + "/Interaction/" + data.getPathway().getUniqueGraphId());
//							createInteraction(startLine, start, end, interaction2, data);
//							data.getPathwayElements().get(e).addProperty(DCTerms.isPartOf, interaction2);
//						}
//					}
//					
//					
//				} else {
////					System.out.println("not identified");
//				}
//			} else {
////				System.out.println("No datanode");
//			}
//		} else {
////			System.out.println("no graph refs at start / end " + e.getGraphId());
//		}
	}	
	
	private static void createInteraction(LineType lineType, List<Resource> source, List<Resource> target, Resource intRes, DataStorage data) {
		intRes.addProperty(RDF.type, Wp.Interaction);
		intRes.addProperty(RDF.type, Wp.Relation);
		intRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
		for(Resource r : source) {
			intRes.addProperty(Wp.hasParticipant, r);
		}
		for(Resource r : target) {
			intRes.addProperty(Wp.hasParticipant, r);
		}
		
		if(lineType.equals(LineType.ARROW)) {
			mapDirectedInteraction(intRes, source, target, null);
		} else if (lineType.equals(LineType.TBAR)) {
			mapDirectedInteraction(intRes, source, target, Wp.Inhibition);
		} else if(lineType.equals(MIMShapes.MIM_CONVERSION)) {
			mapDirectedInteraction(intRes, source, target, Wp.Conversion);
		} else if(lineType.equals(MIMShapes.MIM_INHIBITION)) {
			mapDirectedInteraction(intRes, source, target, Wp.Inhibition);
		} else if(lineType.equals(MIMShapes.MIM_MODIFICATION)) {
			mapDirectedInteraction(intRes, source, target, Wp.Modification);
		} else if(lineType.equals(MIMShapes.MIM_NECESSARY_STIMULATION)) {
			mapDirectedInteraction(intRes, source, target, Wp.NecessaryStimulation);
		} else if(lineType.equals(MIMShapes.MIM_STIMULATION)) {
			mapDirectedInteraction(intRes, source, target, Wp.Stimulation);
		} else if(lineType.equals(MIMShapes.MIM_TRANSLATION)) {
			mapDirectedInteraction(intRes, source, target, Wp.TranscriptionTranslation);
		} else if(lineType.equals(MIMShapes.MIM_BINDING)) {
			mapDirectedInteraction(intRes, source, target, Wp.Binding);
		}
	}
	
	private static void mapDirectedInteraction(Resource intRes, List<Resource> source, List<Resource> target, Resource interactionType) {
		intRes.addProperty(RDF.type, Wp.DirectedInteraction);
		if(interactionType != null) intRes.addProperty(RDF.type, interactionType);
		for(Resource r : source) {
			intRes.addProperty(Wp.source, r);
		}
		for(Resource r : target) {
			intRes.addProperty(Wp.target, r);
		}
	}
}
