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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;

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
	
	
	
	public static IDMapperStack createBridgeDbMapper(Properties prop) throws ClassNotFoundException, IDMapperException{
		Class.forName("org.apache.derby.jdbc.ClientDriver");
		Class.forName("org.bridgedb.rdb.IDMapperRdb");
		DataSourceTxt.init();
		File dir = new File(prop.getProperty("bridgefiles")); //TODO Get Refector to get them directly form bridgedb.org
		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".bridge");
		    }
		};
	
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
		//ENSEMBL
		System.out.println(idXref);
		Set<Xref> unifiedEnsemblIdXref = mapper.mapID(idXref, DataSource.getExistingBySystemCode("En"));
		Iterator<Xref> iter = unifiedEnsemblIdXref.iterator();
		while (iter.hasNext()){
			Xref unifiedId = (Xref) iter.next();
			String unifiedEnsemblDataNodeIdentifier = URLEncoder.encode(unifiedId.getId(), "UTF-8");
			Resource unifiedEnsemblIdResource = model.createResource("http://identifiers.org/ensembl/"+unifiedEnsemblDataNodeIdentifier);
			internalWPDataNodeResource.addProperty(Wp.bdbEnsembl, unifiedEnsemblIdResource);
		}
		//Uniprot
		Set<Xref> unifiedUniprotIdXref = mapper.mapID(idXref, DataSource.getExistingBySystemCode("S"));
		Iterator<Xref> iterUniprot = unifiedUniprotIdXref.iterator();
		while (iterUniprot.hasNext()){
			Xref unifiedUniprotId = (Xref) iterUniprot.next();
			String unifiedUniprotDataNodeIdentifier = URLEncoder.encode(unifiedUniprotId.getId(), "UTF-8");
			Resource unifiedUniprotIdResource = model.createResource("http://identifiers.org/uniprot/"+unifiedUniprotDataNodeIdentifier);
			internalWPDataNodeResource.addProperty(Wp.bdbUniprot, unifiedUniprotIdResource);
		}
		//Entrez Gene
		Set<Xref> unifiedEntrezGeneIdXref = mapper.mapID(idXref, DataSource.getExistingBySystemCode("L"));
		Iterator<Xref> iterEntrezGene = unifiedEntrezGeneIdXref.iterator();
		while (iterEntrezGene.hasNext()){
			Xref unifiedEntrezGeneId = (Xref) iterEntrezGene.next();
			String unifiedEntrezGeneDataNodeIdentifier = URLEncoder.encode(unifiedEntrezGeneId.getId(), "UTF-8");
			Resource unifiedEntrezGeneIdResource = model.createResource("http://identifiers.org/ncbigene/"+unifiedEntrezGeneDataNodeIdentifier);
			internalWPDataNodeResource.addProperty(Wp.bdbEntrezGene, unifiedEntrezGeneIdResource);
		}
		
		//HGNC Symbols
		Set<Xref> unifiedHGNCSymboXref = mapper.mapID(idXref, DataSource.getExistingBySystemCode("H"));
		Iterator<Xref> iterhgncsymbol = unifiedHGNCSymboXref.iterator();
		while (iterhgncsymbol.hasNext()){
			Xref unifiedHGNCSymbol = (Xref) iterhgncsymbol.next();
			String unifiedHGNCSymbolDataNodeIdentifier = URLEncoder.encode(unifiedHGNCSymbol.getId(), "UTF-8");
			Resource unifiedHGNCSymbolResource = model.createResource("http://identifiers.org/hgnc.symbol/"+unifiedHGNCSymbolDataNodeIdentifier);
			internalWPDataNodeResource.addProperty(Wp.bdbHgncSymbol, unifiedHGNCSymbolResource);
		}
		//HMDB
		Set<Xref> unifiedHmdbIdXref = mapper.mapID(idXref, DataSource.getExistingBySystemCode("Ch"));
		Iterator<Xref> iterHmdb = unifiedHmdbIdXref.iterator();
		while (iterHmdb.hasNext()){
			Xref unifiedHmdbId = (Xref) iterHmdb.next();
			String unifiedHmdbDataNodeIdentifier = URLEncoder.encode(unifiedHmdbId.getId(),"UTF-8");
			Resource unifiedHmdbIdResource = model.createResource("http://identifiers.org/hmdb/"+unifiedHmdbDataNodeIdentifier);
			internalWPDataNodeResource.addProperty(Wp.bdbHmdb, unifiedHmdbIdResource);
			//createCHEMINFBits(model,
			//		internalWPDataNodeResource, CHEMINF.CHEMINF_000408, unifiedHmdbDataNodeIdentifier
			//);
		}
		//CHEMSPIDER
		Set<Xref> unifiedChemspiderIdXref = mapper.mapID(idXref, DataSource.getExistingBySystemCode("Cs"));
		Iterator<Xref> iterChemspider = unifiedChemspiderIdXref.iterator();
		while (iterChemspider.hasNext()){
			Xref unifiedChemspiderId = (Xref) iterChemspider.next();
			String unifiedChemspiderDataNodeIdentifier = URLEncoder.encode(unifiedChemspiderId.getId(), "UTF-8");
			Resource unifiedChemspiderIdResource = model.createResource("http://identifiers.org/chemspider/"+unifiedChemspiderDataNodeIdentifier);
			internalWPDataNodeResource.addProperty(Wp.bdbChemspider, unifiedChemspiderIdResource);
			//createCHEMINFBits(model,
			//		internalWPDataNodeResource, CHEMINF.CHEMINF_000405, unifiedChemspiderDataNodeIdentifier
			//);
		}
		// Wikidata
		Set<Xref> unifiedWikidataIdXref = mapper.mapID(idXref, DataSource.getExistingBySystemCode("Wd"));
		Iterator<Xref> iterWikidata = unifiedWikidataIdXref.iterator();
		while (iterWikidata.hasNext()){
			Xref unifiedWikidataId = (Xref) iterWikidata.next();
			String unifiedWikidataDataNodeIdentifier = URLEncoder.encode(unifiedWikidataId.getId(), "UTF-8");
			Resource unifiedWikidataIdResource = model.createResource("http://www.wikidata.org/entity/"+unifiedWikidataDataNodeIdentifier);
			internalWPDataNodeResource.addProperty(Wp.bdbWikidata, unifiedWikidataIdResource);
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
		
	public static Model convertWp(Pathway p, String wpId, String revision) throws FileNotFoundException, IOException, ClassNotFoundException, IDMapperException {
		Model pathwayModel = ModelFactory.createDefaultModel();
		Utils.setModelPrefix(pathwayModel);
		convertWp(p, wpId, revision, pathwayModel, null);
		return pathwayModel;
	}
	
	public static void convertWp(Pathway p, String wpId, String revision, Model pathwayModel, IDMapperStack mapper) throws FileNotFoundException, IOException, ClassNotFoundException, IDMapperException {
		
		
		Resource pathwayRes = PathwayConverter.parsePathwayInfoWp(p, wpId, revision, pathwayModel);
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
				InteractionConverter.parseInteractionWp((MLine)e, pathwayModel, data);
			}
		}
	}
	
}

