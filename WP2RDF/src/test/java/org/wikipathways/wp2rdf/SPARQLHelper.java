/* Copyright (C) 2013  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   - Neither the name of the <organization> nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.wikipathways.wp2rdf;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.PrefixMapping;

public class SPARQLHelper {

	public static StringMatrix sparql(Model model, String queryString)
			throws Exception {
		StringMatrix table = null;

		// now the Jena part
		Query query = QueryFactory.create(queryString);
        PrefixMapping prefixMap = query.getPrefixMapping();
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        ResultSet results = qexec.execSelect();
		table = convertIntoTable(prefixMap, results);

		return table;
	}

	public static StringMatrix sparql(String endpoint, String queryString)
			throws Exception {
		StringMatrix table = null;

		// use Apache for doing the SPARQL query
		DefaultHttpClient httpclient = new DefaultHttpClient();
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("query", queryString));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
		HttpPost httppost = new HttpPost(endpoint);
		httppost.setEntity(entity);
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity responseEntity = response.getEntity();
		InputStream in = responseEntity.getContent();

		// now the Jena part
		ResultSet results = ResultSetFactory.fromXML(in);
		// also use Jena for getting the prefixes...
		// Query query = QueryFactory.create(queryString);
		// PrefixMapping prefixMap = query.getPrefixMapping();
		table = convertIntoTable(null, results);

		in.close();
		return table;
	}

	private static StringMatrix convertIntoTable(
			PrefixMapping prefixMap, ResultSet results) {
		StringMatrix table = new StringMatrix();
		int rowCount = 0;
		while (results.hasNext()) {
			rowCount++;
			QuerySolution soln = results.nextSolution();
			Iterator<String> varNames = soln.varNames();
			while (varNames.hasNext()) {
				String varName = varNames.next();
				int colCount = -1;
				if (table.hasColumn(varName)) {
					colCount = table.getColumnNumber(varName);
				} else {
					colCount = table.getColumnCount() + 1;
					table.setColumnName(colCount, varName);
				}
				RDFNode node = soln.get(varName);
				if (node != null) {
					if (node.isResource()) {
						Resource resource = (Resource)node;
						table.set(rowCount, colCount,
							resource.getURI()
						);
					} else if (node.isLiteral()) {
						Literal literal = (Literal)node;
						table.set(rowCount, colCount, "" + literal.getValue());
					}
				}
			}
		}
		return table;
	}

	public static String[] split(PrefixMapping prefixMap, Resource resource) {
		String uri = resource.getURI();
		if (uri == null) {
			return new String[] {null, null};
		}
		if (prefixMap == null) {
			return new String[] {uri,null};
		}
		Map<String,String> prefixMapMap = prefixMap.getNsPrefixMap();
		Set<String> prefixes = prefixMapMap.keySet();
		String[] split = { null, null };
		for (String key : prefixes){
			String ns = prefixMapMap.get(key);
			if (uri.startsWith(ns)) {
				split[0] = key;
				split[1] = uri.substring(ns.length());
				return split;
			}
		}
		split[1] = uri;
		return split;
	}
}
