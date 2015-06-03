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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pathvisio.core.biopax.PublicationXref;
import org.pathvisio.core.model.LineStyle;
import org.pathvisio.core.model.LineType;
import org.pathvisio.core.model.MLine;
import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.model.PathwayElement.Comment;
import org.pathvisio.core.model.PathwayElement.MAnchor;
import org.pathvisio.core.model.PathwayElement.MPoint;
import org.pathvisio.core.view.MIMShapes;
import org.wikipathways.wp2rdf.ontologies.Gpml;
import org.wikipathways.wp2rdf.ontologies.GpmlNew;
import org.wikipathways.wp2rdf.ontologies.Wp;
import org.wikipathways.wp2rdf.ontologies.WpOld;
import org.wikipathways.wp2rdf.utils.DataHandlerGpml;
import org.wikipathways.wp2rdf.utils.DataHandlerWp;
import org.wikipathways.wp2rdf.utils.Utils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * 
 * @author mkutmon
 * @author ryanmiller
 *
 */
public class InteractionConverter {

	/**
	 * conversion only WP vocabulary
	 * semantic information about interactions
	 */
	public static void parseInteractionWp(MLine e, Model model, DataHandlerWp data) {
		
		if(!data.getPathwayElements().containsKey(e)) {
			boolean ignore = pointingTowardsLine(e, data);
			
			if(!ignore) {
				List<MLine> participatingLines = new ArrayList<MLine>();
				participatingLines.add(e);
				List<MLine> regLines = new ArrayList<MLine>();
				
				System.out.println(e.getGraphId() + " \t anchors: " + e.getMAnchors().size());
				for(MAnchor a : e.getMAnchors()) {
					for(PathwayElement currLine : data.getPathway().getDataObjects()) {
						if(currLine.getObjectType().equals(ObjectType.LINE)) {
							if(currLine.getStartGraphRef() != null) {
								if(currLine.getStartGraphRef().equals(a.getGraphId())) {
									if(currLine.getStartLineType().equals(LineType.LINE)) {
										if(!participatingLines.contains(currLine)) participatingLines.add((MLine)currLine);
									} else {
										if(!regLines.contains(currLine)) regLines.add((MLine)currLine);
									}
								} 
							}		
							if(currLine.getEndGraphRef() != null) {
								if(currLine.getEndGraphRef().equals(a.getGraphId())) {
									if(currLine.getEndLineType().equals(LineType.LINE)) {
										if(!participatingLines.contains(currLine)) participatingLines.add((MLine)currLine);
									} else {
										if(!regLines.contains(currLine)) regLines.add((MLine)currLine);
									}
								}
							}
						}
					}
				}
				
				List<Resource> source = new ArrayList<Resource>();
				List<Resource> target = new ArrayList<Resource>();
				System.out.println("Number participating lines: " + participatingLines.size());
				System.out.println("Number regulating lines: " + regLines.size());
				boolean isdirected = false;
				for(MLine l : participatingLines) {
					if(!l.getStartLineType().equals(LineType.LINE) || !l.getEndLineType().equals(LineType.LINE)) {
						isdirected = true;
					}
	
					if(l.getStartGraphRef() != null) {
						PathwayElement pwEle = data.getPathway().getElementById(l.getStartGraphRef());
						if(pwEle != null) {
							Resource res = data.getDataNodes().get(pwEle.getXref());
							if(res != null) {
								if(l.getStartLineType().equals(LineType.LINE)) {
									source.add(res);
								} else {
									target.add(res);
								}
							}
						}
					}
					if(l.getEndGraphRef() != null) {
						PathwayElement pwEle = data.getPathway().getElementById(l.getEndGraphRef());
						if(pwEle != null) {
							Resource res = data.getDataNodes().get(pwEle.getXref());
							if(res != null) {
								if(l.getEndLineType().equals(LineType.LINE)) {
									source.add(res);
								} else {
									target.add(res);
								}
							}
						}
					}
				}
				
				String warningMsg = "";
				if(isdirected) {
					boolean warning = false;

					if(participatingLines.size() > 1) {
						for(MLine l : participatingLines) {
							if(isReversible(l)) {
								warning = true;
							}
						}
					}
					if(!warning) {
						if(source.size() > 0 && target.size() > 0) {
							String url = Utils.WP_RDF_URL + "/Pathway/" + data.getPwyId() + "_r" + data.getRevision()
									+ "/WP/Interaction/" + e.getGraphId();
							Resource intRes = model.createResource(url);
							intRes.addProperty(RDF.type, Wp.Interaction);
							intRes.addProperty(RDF.type, Wp.DirectedInteraction);
							intRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
							
							for(MLine l : participatingLines) {
								intRes.addProperty(Wp.isAbout, model.createResource(Utils.WP_RDF_URL + "/Pathway/" + data.getPwyId() + "_r" + data.getRevision() + "/Interaction/" + l.getGraphId()));
								data.getPathwayElements().put(l, intRes);
								for(PublicationXref xref : l.getBiopaxReferenceManager().getPublicationXRefs()) {
									if(xref.getPubmedId() != null && !xref.getPubmedId().equals("")) {
										String pubmedUrl = Utils.IDENTIFIERS_ORG_URL + "/pubmed/" + xref.getPubmedId();
										intRes.addProperty(DCTerms.bibliographicCitation, model.createResource(pubmedUrl));
									}
								}
							}
							
							for(Resource r : source) {
								intRes.addProperty(Wp.participants, r);
								intRes.addProperty(Wp.source, r);
								r.addProperty(DCTerms.isPartOf, intRes);
								System.out.println("Source: " + r.getURI());
							}
							
							for(Resource r : target) {
								intRes.addProperty(Wp.participants, r);
								intRes.addProperty(Wp.target, r);
								r.addProperty(DCTerms.isPartOf, intRes);
								System.out.println("Target: " + r.getURI());
							}
							
							for(MLine l : regLines) {
								if(!isReversible(l)) {
									createRegInt(l, data, model, intRes);
								} else {
									System.out.println("regulating line is reversible - not allowed " + l.getGraphId());
								}
							}
						}
					} else {
						warningMsg = "[WARNING:] at least one of the participating lines in this complex interaction is reversible, so we cannot resolve the interaction."
								+ "\n\tInvolved lines: ";
						for(MLine l : participatingLines) {
							warningMsg = warningMsg + l.getGraphId() + ", ";
						}
						
					}
				}
				System.err.println(warningMsg);
			}
		}
	}
	
	private static void createRegInt(MLine l, DataHandlerWp data, Model model, Resource parentInt) {
		if(l.getStartGraphRef() != null && l.getEndGraphRef() != null) {
			PathwayElement startElem = data.getPathway().getElementById(l.getStartGraphRef());
			PathwayElement endElem = data.getPathway().getElementById(l.getEndGraphRef());
			Resource regulator = null;
			if(startElem == null) {
				// start = anchor - hopefully? 
				if(endElem != null) {
					regulator = data.getPathwayElements().get(endElem);
				}
			} else {
				regulator = data.getPathwayElements().get(startElem);
			}
			System.out.println(regulator);
			if(regulator != null) {
				String regUrl = Utils.WP_RDF_URL + "/Pathway/" + data.getPwyId() + "_r" + data.getRevision() + "/WP/Interaction/" + l.getGraphId();
				Resource regIntRes = model.createResource(regUrl);
				regIntRes.addProperty(RDF.type, Wp.Interaction);
				regIntRes.addProperty(RDF.type, Wp.DirectedInteraction);
				regIntRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
				regIntRes.addProperty(Wp.isAbout, model.createResource(Utils.WP_RDF_URL + "/Pathway/" + data.getPwyId() + "_r" + data.getRevision() + "/Interaction/" + l.getGraphId()));
				for(PublicationXref xref : l.getBiopaxReferenceManager().getPublicationXRefs()) {
					if(xref.getPubmedId() != null && !xref.getPubmedId().equals("")) {
						String pubmedUrl = Utils.IDENTIFIERS_ORG_URL + "/pubmed/" + xref.getPubmedId();
						parentInt.addProperty(DCTerms.bibliographicCitation, model.createResource(pubmedUrl));
					}
				}
				regIntRes.addProperty(Wp.participants, parentInt);
				regIntRes.addProperty(Wp.participants, regulator);
				regIntRes.addProperty(Wp.source, regulator);
				regIntRes.addProperty(Wp.target, parentInt);
				regulator.addProperty(DCTerms.isPartOf, regIntRes);
				data.getPathwayElements().put(l, regIntRes);
			}
		}
	}
	
	
	// checks if line is reversible
	private static boolean isReversible(MLine e) {
		if(!e.getStartLineType().equals(LineType.LINE) && !e.getEndLineType().equals(LineType.LINE)) {
			System.out.println(e.getStartLineType() + "\t" + e.getEndLineType());
			return true;
		} 
		return false;
	}

	// check if line is pointing towards another line - will be handled with baseline
	private static boolean pointingTowardsLine(MLine e, DataHandlerWp data) {
		boolean ignore = false;
		if(e.getStartGraphRef() != null) {
			PathwayElement elem = data.getPathway().getElementById(e.getStartGraphRef());
			if(elem == null) {
				// TODO: it is an anchor / hopefully? bug?
				// ignore line
				ignore = true;
			}
		}	
		if(e.getEndGraphRef() != null) {
			PathwayElement elem = data.getPathway().getElementById(e.getEndGraphRef());
			if(elem == null) {
				// TODO: it is an anchor / hopefully? bug?
				// ignore line
				ignore = true;
			}
		}
		return ignore;
	}
	
	/**
	 * conversion only GPML vocabulary
	 */
	public static void parseInteractionGpml(MLine e, Model model, DataHandlerGpml data) {
		Resource intRes = model.createResource(data.getPathwayRes().getURI() + "/Interaction/" + e.getGraphId());
		
		intRes.addProperty(RDF.type, GpmlNew.INTERACTION);
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
	
	/**
	 * first try of resolving complex interactions
	 * still work in progress!!
	 */
	public static void parseInteractionSemantics(MLine e, Model model, DataHandlerGpml data) {
	
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
	}	
	
	private static void createInteraction(LineType lineType, List<Resource> source, List<Resource> target, Resource intRes, DataHandlerGpml data) {
		intRes.addProperty(RDF.type, WpOld.Interaction);
		intRes.addProperty(RDF.type, WpOld.Relation);
		intRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
		for(Resource r : source) {
			intRes.addProperty(WpOld.hasParticipant, r);
		}
		for(Resource r : target) {
			intRes.addProperty(WpOld.hasParticipant, r);
		}
		
		if(lineType.equals(LineType.ARROW)) {
			mapDirectedInteraction(intRes, source, target, null);
		} else if (lineType.equals(LineType.TBAR)) {
			mapDirectedInteraction(intRes, source, target, WpOld.Inhibition);
		} else if(lineType.equals(MIMShapes.MIM_CONVERSION)) {
			mapDirectedInteraction(intRes, source, target, WpOld.Conversion);
		} else if(lineType.equals(MIMShapes.MIM_INHIBITION)) {
			mapDirectedInteraction(intRes, source, target, WpOld.Inhibition);
		} else if(lineType.equals(MIMShapes.MIM_MODIFICATION)) {
			mapDirectedInteraction(intRes, source, target, WpOld.Modification);
		} else if(lineType.equals(MIMShapes.MIM_NECESSARY_STIMULATION)) {
			mapDirectedInteraction(intRes, source, target, WpOld.NecessaryStimulation);
		} else if(lineType.equals(MIMShapes.MIM_STIMULATION)) {
			mapDirectedInteraction(intRes, source, target, WpOld.Stimulation);
		} else if(lineType.equals(MIMShapes.MIM_TRANSLATION)) {
			mapDirectedInteraction(intRes, source, target, WpOld.TranscriptionTranslation);
		} else if(lineType.equals(MIMShapes.MIM_BINDING)) {
			mapDirectedInteraction(intRes, source, target, WpOld.Binding);
		}
	}
	
	private static void mapDirectedInteraction(Resource intRes, List<Resource> source, List<Resource> target, Resource interactionType) {
		intRes.addProperty(RDF.type, WpOld.DirectedInteraction);
		if(interactionType != null) intRes.addProperty(RDF.type, interactionType);
		for(Resource r : source) {
			intRes.addProperty(WpOld.source, r);
		}
		for(Resource r : target) {
			intRes.addProperty(WpOld.target, r);
		}
	}
}
