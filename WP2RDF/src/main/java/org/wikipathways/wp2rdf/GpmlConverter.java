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
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;
import org.bridgedb.Xref;
import org.bridgedb.bio.DataSourceTxt;
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
import org.wikipathways.wp2rdf.ontologies.Wp;
import org.wikipathways.wp2rdf.utils.DataHandlerGpml;
import org.wikipathways.wp2rdf.utils.DataHandlerWp;
import org.wikipathways.wp2rdf.utils.Utils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Class that converts a pathway
 * in a RDF model - only GPML vocabulary
 * 
 * This work is based on Andra Waagmeester's work.
 * 
 * @author mkutmon
 * @author ryanmiller
 * @author DeniseSl22
 *
 */
public class GpmlConverter {
	
	
	
	public static IDMapperStack createBridgeDbMapper(Properties prop) throws ClassNotFoundException, IDMapperException{
		Class.forName("org.apache.derby.jdbc.ClientDriver");
		Class.forName("org.bridgedb.rdb.IDMapperRdb");
		DataSourceTxt.init();
		File dir = new File(prop.getProperty("bridgefiles")); //TODO Get Refactor to get them directly from bridgedb.org -> could be done with wget? Or from Dockerised version of bridgeDb?
		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".bridge");
		    }
		};

		DataSource.register("Wd", "Wikidata");
		DataSource.register("Ck", "KEGG Compound");
		DataSource.register("Lm", "LIPID MAPS");
		File[] bridgeDbFiles = dir.listFiles(filter);
		IDMapperStack mapper = new IDMapperStack();
		for (File bridgeDbFile : bridgeDbFiles) {
			System.out.println(bridgeDbFile.getAbsolutePath());
			mapper.addIDMapper(
				BridgeDb.connect("idmapper-pgdb:" + bridgeDbFile.getAbsolutePath())
			);
		}
		return mapper;
	}

	public static void getUnifiedIdentifiers(Model model, IDMapper  mapper, Xref idXref, Resource internalWPDataNodeResource) throws IDMapperException, UnsupportedEncodingException {
//		System.out.println(idXref);
		//ENSEMBL
		outputBridgeDbMapping(model, mapper, idXref, internalWPDataNodeResource,
			"En", "http://identifiers.org/ensembl/", Wp.bdbEnsembl
		);
		//Uniprot
		outputBridgeDbMapping(model, mapper, idXref, internalWPDataNodeResource,
			"S", "http://identifiers.org/uniprot/", Wp.bdbUniprot
		);
		//Entrez Gene
		outputBridgeDbMapping(model, mapper, idXref, internalWPDataNodeResource,
			"L", "http://identifiers.org/ncbigene/", Wp.bdbEntrezGene
		);
		//HGNC Symbols
		outputBridgeDbMapping(model, mapper, idXref, internalWPDataNodeResource,
			"H", "http://identifiers.org/hgnc.symbol/", Wp.bdbHgncSymbol
		);

		// Metabolites

		//HMDB
		outputBridgeDbMapping(model, mapper, idXref, internalWPDataNodeResource,
			"Ch", "http://identifiers.org/hmdb/", Wp.bdbHmdb
		);
		//CHEMSPIDER
		outputBridgeDbMapping(model, mapper, idXref, internalWPDataNodeResource,
			"Cs", "http://identifiers.org/chemspider/", Wp.bdbChemspider
		);
		//ChEBI
		outputBridgeDbMapping(model, mapper, idXref, internalWPDataNodeResource,
			"Ce", "http://identifiers.org/chebi/", Wp.bdbChEBI
		);
		// Wikidata
		outputBridgeDbMapping(model, mapper, idXref, internalWPDataNodeResource,
			"Wd", "http://www.wikidata.org/entity/", Wp.bdbWikidata
		);
		// PubChem Compound
		outputBridgeDbMapping(model, mapper, idXref, internalWPDataNodeResource,
			"Cpc", "http://rdf.ncbi.nlm.nih.gov/pubchem/compound/CID", Wp.bdbPubChem
		);
		// Kegg Compound
				outputBridgeDbMapping(model, mapper, idXref, internalWPDataNodeResource,
					"Ck", "http://identifiers.org/kegg.compound/", Wp.bdbKeggCompound
				); 
		// LipidMaps
				outputBridgeDbMapping(model, mapper, idXref, internalWPDataNodeResource,
					"Lm", "http://identifiers.org/lipidmaps/", Wp.bdbLipidMaps
				); 
		// InChIKey
		if (mapper != null) {
			try {
				DataSource source = DataSource.getExistingBySystemCode("Ik");
				Set<Xref> unifiedIdXref = mapper.mapID(idXref, source);
				Iterator<Xref> iter = unifiedIdXref.iterator();
				while (iter.hasNext()){
					Xref unifiedId = (Xref) iter.next();
					String inchikey = unifiedId.getId();
					Resource inchiResource = model.createResource("http://identifiers.org/inchikey/" + inchikey);
					internalWPDataNodeResource.addProperty(Wp.bdbInChIKey, inchiResource);
					Resource neutralResource = model.createResource("http://identifiers.org/inchikey/" + inchikey.substring(0,inchikey.length()-1) + "N");
					internalWPDataNodeResource.addProperty(RDFS.seeAlso, neutralResource);
				}
			} catch (Exception exception) {
				System.out.println("InChIKey exception: " + exception.getMessage());
			}
		}

		// Interactions

		// Reactome
		outputBridgeDbMapping(model, mapper, idXref, internalWPDataNodeResource,
			"Re", "http://identifiers.org/reactome/", Wp.bdbReactome
		);
		// Rhea
		outputBridgeDbMapping(model, mapper, idXref, internalWPDataNodeResource,
			"Rh", "http://identifiers.org/rhea/", Wp.bdbRhea
		);

		// Complexes

		// Complex Portal
		outputBridgeDbMapping(model, mapper, idXref, internalWPDataNodeResource,
			"Cpx", "http://identifiers.org/complexportal/", Wp.bdbComplexPortal
		);

		// Literature references

		// DOI
		outputBridgeDbMapping(model, mapper, idXref, internalWPDataNodeResource,
			"Pbd", "https://doi.org/", OWL.sameAs
		);
	}

	private static void outputBridgeDbMapping(Model model, IDMapper mapper, Xref idXref,
			Resource internalWPDataNodeResource, String sourceCode, String uriPrefix, Property predicate)
	throws IDMapperException, UnsupportedEncodingException {
		if (mapper == null) return; // OK, not BridgeDb mapping files; just return
		DataSource source = null;
		try {
			source = DataSource.getExistingBySystemCode(sourceCode);
		} catch (Exception exception) {
			System.out.println("Unknown system code (mapping files not loaded?): " + sourceCode);
			return;
		}
		// first, if we already have a $foo identifier, also output is as mapped identifier
		if (sourceCode.equals(idXref.getDataSource().getSystemCode())) {
			// here too, we need to make sure to output proper identifiers.org IRIs for ChEBI IDs.
			if ("Ce".equals(sourceCode)) {
				String nodeIdentifier = idXref.getId();
				if (nodeIdentifier.startsWith("CHEBI:")) {
					Resource unifiedlIdResource = model.createResource(uriPrefix+nodeIdentifier);
					internalWPDataNodeResource.addProperty(predicate, unifiedlIdResource);
				} else { // just digits
					nodeIdentifier = "CHEBI:" + nodeIdentifier;
					Resource unifiedlIdResource = model.createResource(uriPrefix+nodeIdentifier);
					internalWPDataNodeResource.addProperty(predicate, unifiedlIdResource);
				}
			} else {
				Resource unifiedlIdResource = model.createResource(uriPrefix + idXref.getId());
				internalWPDataNodeResource.addProperty(predicate, unifiedlIdResource);
			}
		}
		// now, use BridgeDb to find additional identifiers
		Set<Xref> unifiedIdXref = mapper.mapID(idXref, source);
		Iterator<Xref> iter = unifiedIdXref.iterator();
		// the next if clause is to handle new HMDB00xxxxx-style identifiers and a BridgeDb
		// mapping file that does not recognize them yet (causing an empty iter)
		if (!iter.hasNext() && "Ch".equals(idXref.getDataSource().getSystemCode())) {
			String origIdentifier = idXref.getId();
			if (origIdentifier.length() == 11) { // HMDB00xxxxx -> HMDBxxxxx
				idXref = new Xref(origIdentifier.replace("HMDB00", "HMDB"), idXref.getDataSource());
				unifiedIdXref = mapper.mapID(idXref, source);
				iter = unifiedIdXref.iterator();
			}
		}
		while (iter.hasNext()){
			Xref unifiedId = (Xref) iter.next();
			if ("Ce".equals(sourceCode)) {
				String unifiedDataNodeIdentifier = unifiedId.getId();
				if (unifiedDataNodeIdentifier.startsWith("CHEBI:")) {
					Resource unifiedlIdResource = model.createResource(uriPrefix+unifiedDataNodeIdentifier);
					internalWPDataNodeResource.addProperty(predicate, unifiedlIdResource);
				} else { // just digits
					unifiedDataNodeIdentifier = "CHEBI:" + unifiedDataNodeIdentifier;
					Resource unifiedlIdResource = model.createResource(uriPrefix+unifiedDataNodeIdentifier);
					internalWPDataNodeResource.addProperty(predicate, unifiedlIdResource);
				}
			} else if ("Ch".equals(sourceCode)) {
				String unifiedDataNodeIdentifier = unifiedId.getId();
				if (unifiedDataNodeIdentifier.length() == 9) {
					unifiedDataNodeIdentifier = unifiedDataNodeIdentifier.replace("HMDB", "HMDB00");
				}
				Resource unifiedlIdResource = model.createResource(uriPrefix+unifiedDataNodeIdentifier);
				internalWPDataNodeResource.addProperty(predicate, unifiedlIdResource);
			} else {
				String unifiedDataNodeIdentifier = URLEncoder.encode(unifiedId.getId(), "UTF-8");
  			    Resource unifiedlIdResource = model.createResource(uriPrefix+unifiedDataNodeIdentifier);
			    internalWPDataNodeResource.addProperty(predicate, unifiedlIdResource);
			}
			//createCHEMINFBits(model,
			//		internalWPDataNodeResource, CHEMINF.CHEMINF_000405, unifiedChemspiderDataNodeIdentifier
			//);
		}
	}

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

	public static Model convertWp(Pathway p, String wpId, String revision, IDMapperStack mapper, List<String> tags) throws FileNotFoundException, IOException, ClassNotFoundException, IDMapperException {
		Model pathwayModel = ModelFactory.createDefaultModel();
		Utils.setModelPrefix(pathwayModel);
		convertWp(p, wpId, revision, pathwayModel, mapper, tags);
		return pathwayModel;
	}

	public static Model convertWp(Pathway p, String wpId, String revision, List<String> tags) throws FileNotFoundException, IOException, ClassNotFoundException, IDMapperException {
		Model pathwayModel = ModelFactory.createDefaultModel();
		Utils.setModelPrefix(pathwayModel);
		convertWp(p, wpId, revision, pathwayModel, null, tags);
		return pathwayModel;
	}
	
	public static void convertWp(Pathway p, String wpId, String revision, Model pathwayModel, IDMapperStack mapper, List<String> tags) throws FileNotFoundException, IOException, ClassNotFoundException, IDMapperException {
		
		
		Resource pathwayRes = PathwayConverter.parsePathwayInfoWp(p, wpId, revision, pathwayModel, tags);
		DataHandlerWp data = new DataHandlerWp(p, wpId, revision, pathwayRes);
		
		//IDMapperStack mapper = WPREST2RDF.maps();
		
		for(PathwayElement e : p.getDataObjects()) {
			if(e.getObjectType().equals(ObjectType.DATANODE)) {
				if(e.getXref() != null && e.getXref().getId() != null && e.getXref().getDataSource() != null){
					String dataNodeDataSource = (e.getXref().getDataSource().getFullName());
					
					Xref idXref = new Xref(e.getXref().getId(), DataSource.getExistingByFullName(dataNodeDataSource));
					DataNodeConverter.parseDataNodeWp(e, pathwayModel, data, p, mapper, idXref);
				}


				
			}
		}
		for(PathwayElement e : p.getDataObjects()) {
			if(e.getObjectType().equals(ObjectType.GROUP)) {
				GroupConverter.parseComplexWp((MGroup) e, pathwayModel, data);
			}
		}
		for(PathwayElement e : p.getDataObjects()) {
			if(e.getObjectType().equals(ObjectType.LINE)) {
				InteractionConverter.parseInteractionWp((MLine)e, pathwayModel, data, mapper);
			}
		}
	}
	
}

