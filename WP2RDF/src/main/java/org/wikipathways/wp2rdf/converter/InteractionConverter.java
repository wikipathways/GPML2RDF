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
import java.util.List;

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
import org.wikipathways.wp2rdf.ontologies.Wp;
import org.wikipathways.wp2rdf.utils.DataHandlerGpml;
import org.wikipathways.wp2rdf.utils.DataHandlerWp;
import org.wikipathways.wp2rdf.utils.Utils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
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
				for(MLine l : participatingLines) {
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
				
				
				LineType lt = getInteractionType(participatingLines);
				if(lt == null) {
					System.out.println("WARNING - different line types in one interaction");
				} else if (lt.equals(LineType.LINE)) {
					// undirected interactions
					if(target.size() != 0) {
						System.out.println("Problem - undirected with targets should not be there");
					} else {
						String url = Utils.WP_RDF_URL + "/Pathway/" + data.getPwyId() + "_r" + data.getRevision()
								+ "/WP/Interaction/" + e.getGraphId();
						Resource intRes = model.createResource(url);
						intRes.addProperty(RDF.type, Wp.Interaction);
						intRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
						
						for(MLine l : participatingLines) {
							intRes.addProperty(Wp.isAbout, model.createResource(Utils.WP_RDF_URL + "/Pathway/" + data.getPwyId() + "_r" + data.getRevision() + "/Interaction/" + l.getGraphId()));
							data.getPathwayElements().put(l, intRes);
							for(PublicationXref xref : l.getBiopaxReferenceManager().getPublicationXRefs()) {
								if(xref.getPubmedId() != null && !xref.getPubmedId().trim().equals("")) {
									String pubmedUrl = Utils.IDENTIFIERS_ORG_URL + "/pubmed/" + xref.getPubmedId().trim();
									Resource pubmedRes = model.createResource(pubmedUrl);
									intRes.addProperty(DCTerms.bibliographicCitation, pubmedRes);
									pubmedRes.addProperty(RDF.type, Wp.PublicationReference);
									pubmedRes.addProperty(FOAF.page, model.createResource(Utils.PUBMED_URL + xref.getPubmedId().trim()));
									pubmedRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
								}
							}
						}
						
						for(Resource r : source) {
							intRes.addProperty(Wp.participants, r);
							r.addProperty(DCTerms.isPartOf, intRes);
						}

						for(MLine l : regLines) {
							if(!isReversible(l)) {
								String regUrl = Utils.WP_RDF_URL + "/Pathway/" + data.getPwyId() + "_r" + data.getRevision() + "/WP/Interaction/" + l.getGraphId();
								createRegInt(l, data, model, intRes, regUrl);
							} else {
								System.out.println("regulating line is reversible - not allowed " + l.getGraphId());
							}
						}
					}
				} else {
					boolean warning = false;

					if(participatingLines.size() > 1) {
						for(MLine l : participatingLines) {
							if(isReversible(l)) {
								warning = true;
							}
						}
					}
					
					if(!warning) {
						if(participatingLines.size() == 1 && isReversible(participatingLines.get(0))) {
							// special case - reversible interaction
							createReversibleInteraction(participatingLines.get(0), data, model, regLines);
						} else {
							if(source.size() > 0 && target.size() > 0) {
								String url = Utils.WP_RDF_URL + "/Pathway/" + data.getPwyId() + "_r" + data.getRevision()
										+ "/WP/Interaction/" + e.getGraphId();
								Resource intRes = model.createResource(url);
								intRes.addProperty(RDF.type, Wp.Interaction);
								intRes.addProperty(RDF.type, Wp.DirectedInteraction);
								Resource resLt = getLineTypeResource(lt);
								if(resLt != null) intRes.addProperty(RDF.type, resLt);
								intRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
								
								for(MLine l : participatingLines) {
									intRes.addProperty(Wp.isAbout, model.createResource(Utils.WP_RDF_URL + "/Pathway/" + data.getPwyId() + "_r" + data.getRevision() + "/Interaction/" + l.getGraphId()));
									data.getPathwayElements().put(l, intRes);
									for(PublicationXref xref : l.getBiopaxReferenceManager().getPublicationXRefs()) {
										if(xref.getPubmedId() != null && !xref.getPubmedId().trim().equals("")) {
											String pubmedUrl = Utils.IDENTIFIERS_ORG_URL + "/pubmed/" + xref.getPubmedId().trim();
											Resource pubmedRes = model.createResource(pubmedUrl);
											intRes.addProperty(DCTerms.bibliographicCitation, pubmedRes);
											pubmedRes.addProperty(RDF.type, Wp.PublicationReference);
											pubmedRes.addProperty(FOAF.page, model.createResource(Utils.PUBMED_URL + xref.getPubmedId().trim()));
											pubmedRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
										}
									}
								}
								
								for(Resource r : source) {
									intRes.addProperty(Wp.participants, r);
									intRes.addProperty(Wp.source, r);
									r.addProperty(DCTerms.isPartOf, intRes);
								}
								
								for(Resource r : target) {
									intRes.addProperty(Wp.participants, r);
									intRes.addProperty(Wp.target, r);
									r.addProperty(DCTerms.isPartOf, intRes);
								}
								
								for(MLine l : regLines) {
									if(!isReversible(l)) {
										String regUrl = Utils.WP_RDF_URL + "/Pathway/" + data.getPwyId() + "_r" + data.getRevision() + "/WP/Interaction/" + l.getGraphId();
										createRegInt(l, data, model, intRes, regUrl);
									} else {
										System.out.println("regulating line is reversible - not allowed " + l.getGraphId());
									}
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
	
	private static LineType getInteractionType(List<MLine> participatingLines) {
		List<LineType> lineTypes = new ArrayList<LineType>();
		for(MLine l : participatingLines) {
			if(!l.getStartLineType().equals(LineType.LINE)) {
				if(!lineTypes.contains(l.getStartLineType())) lineTypes.add(l.getStartLineType());
			}
			if(!l.getEndLineType().equals(LineType.LINE)) {
				if(!lineTypes.contains(l.getEndLineType())) lineTypes.add(l.getEndLineType());
			}
		}
		if(lineTypes.size() > 1) {
			return null;
		} else if (lineTypes.size() == 1) {
			return lineTypes.get(0);
		} else {
			return LineType.LINE;
		}
	}
	
	private static void createReversibleInteraction(MLine l, DataHandlerWp data, Model model, List<MLine> regLines) {
		if(l.getStartGraphRef() != null && l.getEndGraphRef() != null) {
			PathwayElement start1 = data.getPathway().getElementById(l.getStartGraphRef());
			PathwayElement end1 = data.getPathway().getElementById(l.getEndGraphRef());
			
			Resource resStart1 = data.getDataNodes().get(start1.getXref());
			Resource resEnd1 = data.getDataNodes().get(end1.getXref());
			
			if(resStart1 != null && resEnd1 != null) {
				String url = Utils.WP_RDF_URL + "/Pathway/" + data.getPwyId() + "_r" + data.getRevision() + "/WP/Interaction/" + l.getGraphId() + "_1";
				Resource intRes = model.createResource(url);
				intRes.addProperty(RDF.type, Wp.Interaction);
				intRes.addProperty(RDF.type, Wp.DirectedInteraction);
				Resource lineType = getLineTypeResource(l.getEndLineType());
				if(lineType != null) intRes.addProperty(RDF.type, lineType);
				intRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
				intRes.addProperty(Wp.isAbout, model.createResource(Utils.WP_RDF_URL + "/Pathway/" + data.getPwyId() + "_r" + data.getRevision() + "/Interaction/" + l.getGraphId()));
				intRes.addProperty(Wp.participants, resStart1);
				intRes.addProperty(Wp.source, resStart1);
				resStart1.addProperty(DCTerms.isPartOf, intRes);
				intRes.addProperty(Wp.participants, resEnd1);
				intRes.addProperty(Wp.target, resEnd1);
				resEnd1.addProperty(DCTerms.isPartOf, intRes);
				data.getPathwayElements().put(l, intRes);
				
				String url2 = Utils.WP_RDF_URL + "/Pathway/" + data.getPwyId() + "_r" + data.getRevision() + "/WP/Interaction/" + l.getGraphId() + "_2";
				Resource intRes2 = model.createResource(url2);
				intRes2.addProperty(RDF.type, Wp.Interaction);
				intRes2.addProperty(RDF.type, Wp.DirectedInteraction);
				Resource lineType2 = getLineTypeResource(l.getStartLineType());
				if(lineType2 != null) intRes2.addProperty(RDF.type, lineType2);
				intRes2.addProperty(DCTerms.isPartOf, data.getPathwayRes());
				intRes2.addProperty(Wp.isAbout, model.createResource(Utils.WP_RDF_URL + "/Pathway/" + data.getPwyId() + "_r" + data.getRevision() + "/Interaction/" + l.getGraphId()));
				intRes2.addProperty(Wp.participants, resStart1);
				intRes2.addProperty(Wp.target, resStart1);
				resStart1.addProperty(DCTerms.isPartOf, intRes2);
				intRes2.addProperty(Wp.participants, resEnd1);
				intRes2.addProperty(Wp.source, resEnd1);
				resEnd1.addProperty(DCTerms.isPartOf, intRes2);
				
				
				for(PublicationXref xref : l.getBiopaxReferenceManager().getPublicationXRefs()) {
					if(xref.getPubmedId() != null && !xref.getPubmedId().trim().equals("")) {
						String pubmedUrl = Utils.IDENTIFIERS_ORG_URL + "/pubmed/" + xref.getPubmedId().trim();
						Resource pubmedRes = model.createResource(pubmedUrl);
						intRes.addProperty(DCTerms.bibliographicCitation, pubmedRes);
						intRes2.addProperty(DCTerms.bibliographicCitation, pubmedRes);
						pubmedRes.addProperty(RDF.type, Wp.PublicationReference);
						pubmedRes.addProperty(FOAF.page, model.createResource(Utils.PUBMED_URL + xref.getPubmedId().trim()));
						pubmedRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
					}
				}
				
				for(MLine regLine : regLines) {
					if(!isReversible(regLine)) {
						String regUrl = Utils.WP_RDF_URL + "/Pathway/" + data.getPwyId() + "_r" + data.getRevision() + "/WP/Interaction/" + regLine.getGraphId();
						createRegInt(regLine, data, model, intRes, regUrl + "_1");
						createRegInt(regLine, data, model, intRes2, regUrl + "_2");
					} else {
						System.out.println("regulating line is reversible - not allowed " + regLine.getGraphId());
					}
				}
			}
		}
	}
	
	private static void createRegInt(MLine l, DataHandlerWp data, Model model, Resource parentInt, String url) {
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
			if(regulator != null) {
				
				LineType lt = l.getStartLineType();
				if(lt.equals(LineType.LINE)) {
					lt = l.getEndLineType();
				}
				
				String regUrl = url;
				Resource regIntRes = model.createResource(regUrl);
				data.getPathwayElements().put(l, regIntRes);
				regIntRes.addProperty(RDF.type, Wp.Interaction);
				regIntRes.addProperty(RDF.type, Wp.DirectedInteraction);
				Resource lineType = getLineTypeResource(lt);
				if(lineType != null) {
					regIntRes.addProperty(RDF.type, lineType);
				}
				regIntRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
				regIntRes.addProperty(Wp.isAbout, model.createResource(Utils.WP_RDF_URL + "/Pathway/" + data.getPwyId() + "_r" + data.getRevision() + "/Interaction/" + l.getGraphId()));
				for(PublicationXref xref : l.getBiopaxReferenceManager().getPublicationXRefs()) {
					if(xref.getPubmedId() != null && !xref.getPubmedId().trim().equals("")) {
						String pubmedUrl = Utils.IDENTIFIERS_ORG_URL + "/pubmed/" + xref.getPubmedId().trim();
						Resource pubmedRes = model.createResource(pubmedUrl);
						parentInt.addProperty(DCTerms.bibliographicCitation, pubmedRes);
						pubmedRes.addProperty(RDF.type, Wp.PublicationReference);
						pubmedRes.addProperty(FOAF.page, model.createResource(Utils.PUBMED_URL + xref.getPubmedId().trim()));
						pubmedRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
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
	
	private static Resource getLineTypeResource(LineType lt) {
		if(lt.equals(LineType.ARROW)) {
			return null;
		} else if (lt.equals(LineType.TBAR)) {
			return Wp.Inhibition;
		} else if (lt.equals(MIMShapes.MIM_CATALYSIS)) {
			return Wp.Catalysis;
		} else if (lt.equals(MIMShapes.MIM_CONVERSION)) {
			return Wp.Conversion;
		} else if (lt.equals(MIMShapes.MIM_INHIBITION)) {
			return Wp.Inhibition;
		} else if (lt.equals(MIMShapes.MIM_STIMULATION)) {
			return Wp.Stimulation;
		} else if (lt.equals(MIMShapes.MIM_TRANSLATION)) {
			return Wp.TranscriptionTranslation;
		} else if (lt.equals(MIMShapes.MIM_NECESSARY_STIMULATION)) {
			System.out.println("TODO: necessary stimulation");
		} else if (lt.equals(MIMShapes.MIM_MODIFICATION)) {
			System.out.println("TODO: modification");
		}
		return null;
	}
	
	// checks if line is reversible
	private static boolean isReversible(MLine e) {
		if(!e.getStartLineType().equals(LineType.LINE) && !e.getEndLineType().equals(LineType.LINE)) {
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
		
		intRes.addProperty(RDF.type, Gpml.INTERACTION);
		data.getPathwayRes().addProperty(Gpml.HAS_INTERACTION, intRes);
		intRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
		
		intRes.addLiteral(Gpml.LINE_THICKNESS, e.getLineThickness());
		intRes.addLiteral(Gpml.GRAPH_ID, e.getGraphId());
		intRes.addLiteral(Gpml.COLOR, Utils.colorToHex(e.getColor()));
		intRes.addLiteral(Gpml.LINE_STYLE, e.getLineStyle() != LineStyle.DASHED ? "Solid" : "Broken");
		intRes.addLiteral(Gpml.ZORDER, e.getZOrder());
		intRes.addLiteral(Gpml.CONNECTOR_TYPE, e.getConnectorType().getName());
		
		if(e.getXref() != null && e.getXref().getId() != null && e.getXref().getDataSource() != null) {
			intRes.addLiteral(Gpml.XREF_ID, e.getXref().getId());
			intRes.addLiteral(Gpml.XREF_DATASOURCE, e.getXref().getDataSource().getFullName());
		}
		
		if(e.getGroupRef() != null) intRes.addLiteral(Gpml.GROUP_REF, e.getGroupRef());
		
		// TODO: in schema there is an interaction type but that's not in the data model. 
		
		for(MAnchor a : e.getMAnchors()) {
			if(a.getGraphId() != null) AnchorConverter.parseAnchorGpml(a, model, intRes, data);
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
			intRes.addLiteral(Gpml.BIOPAX_REF, s);
		}
		
		for(Comment c : e.getComments()) {
			CommentConverter.parseCommentGpml(c, model, intRes, data);
		}
		
		for(PublicationXref xref : e.getBiopaxReferenceManager().getPublicationXRefs()) {
			PublicationXrefConverter.parsePublicationXrefGpml(xref, intRes, model, data);
		}
			
		data.getPathwayElements().put(e, intRes);
	}
}
