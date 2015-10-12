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

import java.util.Set;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.pathvisio.core.biopax.PublicationXref;
import org.pathvisio.core.model.LineStyle;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.model.PathwayElement.Comment;
import org.wikipathways.wp2rdf.ontologies.Gpml;
import org.wikipathways.wp2rdf.ontologies.Wp;
import org.wikipathways.wp2rdf.utils.DataHandlerGpml;
import org.wikipathways.wp2rdf.utils.DataHandlerWp;
import org.wikipathways.wp2rdf.utils.Utils;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * 
 * @author mkutmon
 * @author ryanmiller
 *
 */
public class DataNodeConverter {

	/**
	 * conversion only WP vocabulary
	 * semantic information about a data node
	 * @param metMapper 
	 * @param geneMapper 
	 */
	public static void parseDataNodeWp(PathwayElement elem, Model model, DataHandlerWp data, Pathway p) {
		
		//final Properties prop = new Properties();
		//prop.load(new FileInputStream("config.properties"));
		
		if(elem.getXref() != null && elem.getXref().getId() != null && elem.getXref().getDataSource() != null) {
			if(!elem.getDataNodeType().equals("Unknown")) {
				if (elem.getXref().getId() != null && elem.getXref().getId().trim().length() > 0) {
					String url = elem.getDataSource().getIdentifiersOrgUri(elem.getXref().getId());
					if(url != null && !url.equals("")) {
						Resource datanodeRes = data.getDataNodes().get(elem.getXref());
						if(datanodeRes == null) {
							datanodeRes = model.createResource(url.trim().replaceAll(" ", "_"));

							datanodeRes.addProperty(DC.identifier, model.createResource(url.trim().replaceAll(" ", "_")));
							datanodeRes.addLiteral(DCTerms.source, elem.getXref().getDataSource().getFullName());
							datanodeRes.addLiteral(DCTerms.identifier, elem.getXref().getId());

							datanodeRes.addProperty(RDF.type, Wp.DataNode);
							switch (elem.getDataNodeType()) {
							case "GeneProduct":
								datanodeRes.addProperty(RDF.type, Wp.GeneProduct);
								// add id mapping step
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("homo sapiens") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										DataSource ds2 = null;
										DataSource ds3 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Hs_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
											datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds2 = ds2.getBySystemCode("H");
										Set<Xref> crossrefs2 = mapper.mapID(elem.getXref(), ds2);
										for(Xref ref: crossrefs2){
										datanodeRes.addProperty(Wp.bdbHgncSymbol, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/hgnc.symbol/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds3 = ds3.getBySystemCode("S");
										Set<Xref> crossrefs3 = mapper.mapID(elem.getXref(), ds3);
										for(Xref ref: crossrefs3){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("anopheles gambiae") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										DataSource ds2 = null;
										DataSource ds3 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Ag_Derby_Ensembl_Metazoa_28.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
											datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds2 = ds2.getBySystemCode("H");
										Set<Xref> crossrefs2 = mapper.mapID(elem.getXref(), ds2);
										for(Xref ref: crossrefs2){
										datanodeRes.addProperty(Wp.bdbHgncSymbol, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/hgnc.symbol/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds3 = ds3.getBySystemCode("S");
										Set<Xref> crossrefs3 = mapper.mapID(elem.getXref(), ds3);
										for(Xref ref: crossrefs3){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("arabidopsis thaliana") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										DataSource ds2 = null;
										DataSource ds3 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/At_Derby_Ensembl_Plant_28.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
											datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds2 = ds2.getBySystemCode("H");
										Set<Xref> crossrefs2 = mapper.mapID(elem.getXref(), ds2);
										for(Xref ref: crossrefs2){
										datanodeRes.addProperty(Wp.bdbHgncSymbol, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/hgnc.symbol/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds3 = ds3.getBySystemCode("S");
										Set<Xref> crossrefs3 = mapper.mapID(elem.getXref(), ds3);
										for(Xref ref: crossrefs3){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("bacillus subtilis") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										DataSource ds2 = null;
										DataSource ds3 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Bs_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
											datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds2 = ds2.getBySystemCode("H");
										Set<Xref> crossrefs2 = mapper.mapID(elem.getXref(), ds2);
										for(Xref ref: crossrefs2){
										datanodeRes.addProperty(Wp.bdbHgncSymbol, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/hgnc.symbol/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds3 = ds3.getBySystemCode("S");
										Set<Xref> crossrefs3 = mapper.mapID(elem.getXref(), ds3);
										for(Xref ref: crossrefs3){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("bos taurus") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										DataSource ds2 = null;
										DataSource ds3 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Bt_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
											datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds2 = ds2.getBySystemCode("H");
										Set<Xref> crossrefs2 = mapper.mapID(elem.getXref(), ds2);
										for(Xref ref: crossrefs2){
										datanodeRes.addProperty(Wp.bdbHgncSymbol, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/hgnc.symbol/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds3 = ds3.getBySystemCode("S");
										Set<Xref> crossrefs3 = mapper.mapID(elem.getXref(), ds3);
										for(Xref ref: crossrefs3){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("caenorhabditis elegans") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										DataSource ds2 = null;
										DataSource ds3 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Ce_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
											datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds2 = ds2.getBySystemCode("H");
										Set<Xref> crossrefs2 = mapper.mapID(elem.getXref(), ds2);
										for(Xref ref: crossrefs2){
										datanodeRes.addProperty(Wp.bdbHgncSymbol, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/hgnc.symbol/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds3 = ds3.getBySystemCode("S");
										Set<Xref> crossrefs3 = mapper.mapID(elem.getXref(), ds3);
										for(Xref ref: crossrefs3){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("canis familiaris") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										DataSource ds2 = null;
										DataSource ds3 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Cf_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
											datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds2 = ds2.getBySystemCode("H");
										Set<Xref> crossrefs2 = mapper.mapID(elem.getXref(), ds2);
										for(Xref ref: crossrefs2){
										datanodeRes.addProperty(Wp.bdbHgncSymbol, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/hgnc.symbol/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds3 = ds3.getBySystemCode("S");
										Set<Xref> crossrefs3 = mapper.mapID(elem.getXref(), ds3);
										for(Xref ref: crossrefs3){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("danio rerio") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										DataSource ds2 = null;
										DataSource ds3 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Dr_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
											datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds2 = ds2.getBySystemCode("H");
										Set<Xref> crossrefs2 = mapper.mapID(elem.getXref(), ds2);
										for(Xref ref: crossrefs2){
										datanodeRes.addProperty(Wp.bdbHgncSymbol, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/hgnc.symbol/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds3 = ds3.getBySystemCode("S");
										Set<Xref> crossrefs3 = mapper.mapID(elem.getXref(), ds3);
										for(Xref ref: crossrefs3){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("Drosohpila melanogaster") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										DataSource ds2 = null;
										DataSource ds3 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Dm_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
											datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds2 = ds2.getBySystemCode("H");
										Set<Xref> crossrefs2 = mapper.mapID(elem.getXref(), ds2);
										for(Xref ref: crossrefs2){
										datanodeRes.addProperty(Wp.bdbHgncSymbol, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/hgnc.symbol/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds3 = ds3.getBySystemCode("S");
										Set<Xref> crossrefs3 = mapper.mapID(elem.getXref(), ds3);
										for(Xref ref: crossrefs3){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("escherichia coli") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										DataSource ds2 = null;
										DataSource ds3 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Ec_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
											datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds2 = ds2.getBySystemCode("H");
										Set<Xref> crossrefs2 = mapper.mapID(elem.getXref(), ds2);
										for(Xref ref: crossrefs2){
										datanodeRes.addProperty(Wp.bdbHgncSymbol, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/hgnc.symbol/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds3 = ds3.getBySystemCode("S");
										Set<Xref> crossrefs3 = mapper.mapID(elem.getXref(), ds3);
										for(Xref ref: crossrefs3){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("gallus gallus") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										DataSource ds2 = null;
										DataSource ds3 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Gg_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
											datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds2 = ds2.getBySystemCode("H");
										Set<Xref> crossrefs2 = mapper.mapID(elem.getXref(), ds2);
										for(Xref ref: crossrefs2){
										datanodeRes.addProperty(Wp.bdbHgncSymbol, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/hgnc.symbol/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds3 = ds3.getBySystemCode("S");
										Set<Xref> crossrefs3 = mapper.mapID(elem.getXref(), ds3);
										for(Xref ref: crossrefs3){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("mus musculus") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										DataSource ds2 = null;
										DataSource ds3 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Mm_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
											datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds2 = ds2.getBySystemCode("H");
										Set<Xref> crossrefs2 = mapper.mapID(elem.getXref(), ds2);
										for(Xref ref: crossrefs2){
										datanodeRes.addProperty(Wp.bdbHgncSymbol, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/hgnc.symbol/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds3 = ds3.getBySystemCode("S");
										Set<Xref> crossrefs3 = mapper.mapID(elem.getXref(), ds3);
										for(Xref ref: crossrefs3){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("oryza sativa") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										DataSource ds2 = null;
										DataSource ds3 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Oj_Derby_Ensembl_Plant_28.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
											datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds2 = ds2.getBySystemCode("H");
										Set<Xref> crossrefs2 = mapper.mapID(elem.getXref(), ds2);
										for(Xref ref: crossrefs2){
										datanodeRes.addProperty(Wp.bdbHgncSymbol, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/hgnc.symbol/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds3 = ds3.getBySystemCode("S");
										Set<Xref> crossrefs3 = mapper.mapID(elem.getXref(), ds3);
										for(Xref ref: crossrefs3){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("pan troglodytes") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										DataSource ds2 = null;
										DataSource ds3 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Pt_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
											datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds2 = ds2.getBySystemCode("H");
										Set<Xref> crossrefs2 = mapper.mapID(elem.getXref(), ds2);
										for(Xref ref: crossrefs2){
										datanodeRes.addProperty(Wp.bdbHgncSymbol, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/hgnc.symbol/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds3 = ds3.getBySystemCode("S");
										Set<Xref> crossrefs3 = mapper.mapID(elem.getXref(), ds3);
										for(Xref ref: crossrefs3){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("rattus norvegicus") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										DataSource ds2 = null;
										DataSource ds3 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Rn_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
											datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds2 = ds2.getBySystemCode("H");
										Set<Xref> crossrefs2 = mapper.mapID(elem.getXref(), ds2);
										for(Xref ref: crossrefs2){
										datanodeRes.addProperty(Wp.bdbHgncSymbol, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/hgnc.symbol/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds3 = ds3.getBySystemCode("S");
										Set<Xref> crossrefs3 = mapper.mapID(elem.getXref(), ds3);
										for(Xref ref: crossrefs3){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("saccharomyces cerevisiae") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										DataSource ds2 = null;
										DataSource ds3 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Sc_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
											datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds2 = ds2.getBySystemCode("H");
										Set<Xref> crossrefs2 = mapper.mapID(elem.getXref(), ds2);
										for(Xref ref: crossrefs2){
										datanodeRes.addProperty(Wp.bdbHgncSymbol, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/hgnc.symbol/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds3 = ds3.getBySystemCode("S");
										Set<Xref> crossrefs3 = mapper.mapID(elem.getXref(), ds3);
										for(Xref ref: crossrefs3){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("zea mays") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										DataSource ds2 = null;
										DataSource ds3 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Zm_Derby_Ensembl_Plant_28.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
											datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds2 = ds2.getBySystemCode("H");
										Set<Xref> crossrefs2 = mapper.mapID(elem.getXref(), ds2);
										for(Xref ref: crossrefs2){
										datanodeRes.addProperty(Wp.bdbHgncSymbol, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/hgnc.symbol/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds3 = ds3.getBySystemCode("S");
										Set<Xref> crossrefs3 = mapper.mapID(elem.getXref(), ds3);
										for(Xref ref: crossrefs3){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								break;
								
							case "Protein":
								datanodeRes.addProperty(RDF.type, Wp.Protein);
								// add id mapping step
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("homo sapiens") )
									{
										DataSource ds = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Hs_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to

										ds = ds.getBySystemCode("S");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("anopheles gambiae") )
									{
										DataSource ds = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Ag_Derby_Ensembl_Metazoa_28.bridge");
										//here you get crossreferences from all databases the element id maps to

										ds = ds.getBySystemCode("S");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("arabidopsis thaliana") )
									{
										DataSource ds = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/At_Derby_Ensembl_Plant_28.bridge");
										//here you get crossreferences from all databases the element id maps to

										ds = ds.getBySystemCode("S");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("bacillus subtilis") )
									{
										DataSource ds = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Bs_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to

										ds = ds.getBySystemCode("S");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("bos taurus") )
									{
										DataSource ds = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Bt_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to

										ds = ds.getBySystemCode("S");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("caenorhabditis elegans") )
									{
										DataSource ds = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Ce_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to

										ds = ds.getBySystemCode("S");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("canis familiaris") )
									{
										DataSource ds = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Cf_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to

										ds = ds.getBySystemCode("S");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("drosophila melanogaster") )
									{
										DataSource ds = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Dm_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to

										ds = ds.getBySystemCode("S");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("danio rerio") )
									{
										DataSource ds = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Dr_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to

										ds = ds.getBySystemCode("S");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("escherichia coli") )
									{
										DataSource ds = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Ec_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to

										ds = ds.getBySystemCode("S");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("gallus gallus") )
									{
										DataSource ds = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Gg_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to

										ds = ds.getBySystemCode("S");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("mus musculus") )
									{
										DataSource ds = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Mm_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to

										ds = ds.getBySystemCode("S");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("oryza sativa") )
									{
										DataSource ds = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Oj_Derby_Ensembl_Plant_28.bridge");
										//here you get crossreferences from all databases the element id maps to

										ds = ds.getBySystemCode("S");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("pan troglodytes") )
									{
										DataSource ds = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Pt_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to

										ds = ds.getBySystemCode("S");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("rattus norvegicus") )
									{
										DataSource ds = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Rn_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to

										ds = ds.getBySystemCode("S");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("saccharomyces cerevisiae") )
									{
										DataSource ds = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Sc_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to

										ds = ds.getBySystemCode("S");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("zea mays") )
									{
										DataSource ds = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Zm_Derby_Ensembl_Plant_28.bridge");
										//here you get crossreferences from all databases the element id maps to

										ds = ds.getBySystemCode("S");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbUniprot, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/uniprot/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								break;
								
							case "Metabolite":
								datanodeRes.addProperty(RDF.type, Wp.Metabolite);
								// add id mapping step
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									
										DataSource ds = null;
										DataSource ds1 = null;
										DataSource ds2 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/metabolites_20150409.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("Ch");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbHmdb, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/hmdb/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("Cs");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbChemspider, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/chemspider/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds2 = ds2.getBySystemCode("Ce");
										Set<Xref> crossrefs2 = mapper.mapID(elem.getXref(), ds2);
										for(Xref ref: crossrefs2){
										datanodeRes.addProperty(Wp.bdbChEBI, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/chebi/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
									
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								break;
								
							case "Rna":
								datanodeRes.addProperty(RDF.type, Wp.Rna);
								// add id mapping step
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("homo sapiens") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Hs_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("anopheles gambiae") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Ag_Derby_Ensembl_Metazoa_28.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("arabidopsis thaliana") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/At_Derby_Ensembl_Plant_28.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("bacillus subtilis") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Bs_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("bos taurus") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Bt_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("caenorhabditis elegans") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Ce_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("canis familiaris") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Cf_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("drosophila melanogaster") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Dm_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("danio rerio") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Dr_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("escherichia coli") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Ec_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("gallus gallus") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Gg_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("mus musculus") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Mm_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("oryza sativa") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Oj_Derby_Ensembl_Plant_28.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("pan troglodytes") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Pt_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("rattus norvegicus") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Rn_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("saccharomyces cerevisiae") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Sc_Derby_Ensembl_80.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								try 
								{
									Class.forName("org.bridgedb.rdb.IDMapperRdb");
									// set up switch case for different species to select correct bridge file
									if (p.getMappInfo().getOrganism().equalsIgnoreCase("zea mays") )
									{
										DataSource ds = null;
										DataSource ds1 = null;
										
										IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
												+ "/tmp/OPSBRIDGEDB/Zm_Derby_Ensembl_Plant_28.bridge");
										//here you get crossreferences from all databases the element id maps to
										ds = ds.getBySystemCode("L");
										Set<Xref> crossrefs = mapper.mapID(elem.getXref(), ds);
										for(Xref ref: crossrefs){
										datanodeRes.addProperty(Wp.bdbEntrezGene, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ncbigene/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
										ds1 = ds1.getBySystemCode("En");
										Set<Xref> crossrefs1 = mapper.mapID(elem.getXref(), ds1);
										for(Xref ref: crossrefs1){
										datanodeRes.addProperty(Wp.bdbEnsembl, model.createResource(Utils.IDENTIFIERS_ORG_URL + "/ensembl/" + ref.getId().trim().replaceAll(" ", "_")) );
										}
										
									}
									
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IDMapperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NullPointerException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								
								
								break;
							case "Pathway":
								// TODO
								break;
							case "Complex":
								// TODO
								break;
							default:
								break;
							}
									
							
							data.getDataNodes().put(elem.getXref(), datanodeRes);
							data.getPathwayElements().put(elem, datanodeRes);
						}
						// TODO: what to do about those - are they pathway specific?
						for(PublicationXref xref : elem.getBiopaxReferenceManager().getPublicationXRefs()) {
							if(xref.getPubmedId() != null && !xref.getPubmedId().equals("")) {
								String pubmedUrl = Utils.IDENTIFIERS_ORG_URL + "/pubmed/" + xref.getPubmedId();
								datanodeRes.addProperty(DCTerms.bibliographicCitation, model.createResource(pubmedUrl.trim()));
							}
						}

						datanodeRes.addProperty(Wp.isAbout, model.createResource(Utils.WP_RDF_URL + "/Pathway/" + data.getPwyId() + "_r" + data.getRevision() +
								"/DataNode/" + elem.getGraphId()));
						datanodeRes.addLiteral(RDFS.label, elem.getTextLabel().replace("\n", " ").trim());
						datanodeRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
					}
				}
			}
		}
	}
	
	/**
	 * conversion only GPML vocabulary
	 */
	public static void parseDataNodesGpml(PathwayElement elem, Model model, DataHandlerGpml data) {
		
		Resource datanodeRes = model.createResource(data.getPathwayRes().getURI() + "/DataNode/" + elem.getGraphId());
		if (elem.getGraphId() == null)
			datanodeRes = model.createResource(data.getPathwayRes().getURI() + "/DataNode/" + elem.hashCode());

		datanodeRes.addLiteral(Gpml.FONT_STYLE, elem.isItalic() ? "Italic" : "Normal");
		datanodeRes.addLiteral(Gpml.LINE_THICKNESS, elem.getLineThickness());
		datanodeRes.addLiteral(Gpml.FONT_SIZE, elem.getMFontSize());
		datanodeRes.addLiteral(Gpml.FONT_NAME, elem.getFontName());
		datanodeRes.addLiteral(Gpml.ALIGN, elem.getAlign().getGpmlName());
		datanodeRes.addLiteral(Gpml.GRAPH_ID, elem.getGraphId() != null ? elem.getGraphId() : "");
		if(elem.getGroupRef() != null) datanodeRes.addLiteral(Gpml.GROUP_REF, elem.getGroupRef());
		datanodeRes.addLiteral(Gpml.COLOR, Utils.colorToHex(elem.getColor()));
		datanodeRes.addLiteral(Gpml.CENTER_Y, elem.getMCenterY());
		datanodeRes.addLiteral(Gpml.VALIGN, elem.getValign().getGpmlName());
		datanodeRes.addLiteral(Gpml.FONT_WEIGHT, elem.isBold() ? "Bold" : "Normal");
		datanodeRes.addLiteral(Gpml.FONT_DECORATION, elem.isUnderline() ? "Underline" : "Normal");
		datanodeRes.addLiteral(Gpml.FONT_STRIKETHRU, elem.isStrikethru() ? "Strikethru" : "Normal");
		datanodeRes.addLiteral(Gpml.HEIGHT, elem.getMHeight());
		datanodeRes.addLiteral(Gpml.LINE_STYLE, elem.getLineStyle() != LineStyle.DASHED ? "Solid" : "Broken");
		datanodeRes.addLiteral(Gpml.CENTER_X, elem.getMCenterX());
		datanodeRes.addLiteral(Gpml.TEXTLABEL, elem.getTextLabel());
		datanodeRes.addLiteral(Gpml.WIDTH, elem.getMWidth());
		datanodeRes.addLiteral(Gpml.FILL_COLOR, Utils.colorToHex(elem.getFillColor()));
		datanodeRes.addLiteral(Gpml.ZORDER, elem.getZOrder());
		if (elem.getShapeType() != null)
			datanodeRes.addLiteral(Gpml.SHAPE_TYPE, elem.getShapeType().getName());
		datanodeRes.addLiteral(Gpml.TYPE, elem.getDataNodeType());
		
		if(elem.getXref() != null && elem.getXref().getId() != null && elem.getXref().getDataSource() != null) {
			datanodeRes.addLiteral(Gpml.XREF_ID, elem.getXref().getId());
			datanodeRes.addLiteral(Gpml.XREF_DATASOURCE, elem.getXref().getDataSource().getFullName());
		}
		
		for(String s : elem.getBiopaxRefs()) {
			datanodeRes.addLiteral(Gpml.BIOPAX_REF, s);
		}
		
		for(Comment c : elem.getComments()) {
			CommentConverter.parseCommentGpml(c, model, datanodeRes, data);
		}
		
		for(PublicationXref xref : elem.getBiopaxReferenceManager().getPublicationXRefs()) {
			PublicationXrefConverter.parsePublicationXrefGpml(xref, datanodeRes, model, data);
		}
		
		datanodeRes.addProperty(RDF.type, Gpml.DATA_NODE);
		
		datanodeRes.addProperty(DCTerms.isPartOf, data.getPathwayRes());
		data.getPathwayRes().addProperty(Gpml.HAS_DATA_NODE, datanodeRes);
		data.getPathwayElements().put(elem, datanodeRes);
		
	}
}
