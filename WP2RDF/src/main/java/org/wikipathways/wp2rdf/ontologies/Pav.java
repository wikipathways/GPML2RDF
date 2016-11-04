package org.wikipathways.wp2rdf.ontologies;
 
import com.hp.hpl.jena.rdf.model.*;
 
/**
 * Vocabulary definitions from https://pav-ontology.googlecode.com/svn/trunk/pav.owl 
 * @author Auto-generated by schemagen on 14 Sep 2012 15:21 
 */
public class Pav {
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://purl.org/pav/";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    /** <p>An agent that originated or gave existence to the work that is expressed by 
     *  the digital resource. The author of the content of a resource is different 
     *  from the creator of that digital resource (although they are often the same). 
     *  See pav:createdBy for a discussion.</p>
     */
    public static final Property authoredBy = m_model.createProperty( "http://purl.org/pav/authoredBy" );
    
    /** <p>The date this resource was authored. pav:authoredBy gives the authoring agent. 
     *  Note that pav:authoredOn is different from pav:createdOn, although they are 
     *  often the same. See pav:createdBy for a discussion.</p>
     */
    public static final Property authoredOn = m_model.createProperty( "http://purl.org/pav/authoredOn" );
    
    /** <p>An agent that provided any sort of help in conceiving the work that is expressed 
     *  by the digital artifact. The date of contribution can be expressed using pav:contributedOn 
     *  - note however that there is no relationship in PAV identifying which contributor 
     *  contributed when or what, such lineage should rather be expressed using a 
     *  process-centric model such as OPM or PROV. Note that as pav:contributedBy 
     *  identifies only agents that contributed to the work, and not agents that made 
     *  the digital artifact, it is more precise than dct:contributor. See pav:createdBy 
     *  for a discussion.</p>
     */
    public static final Property contributedBy = m_model.createProperty( "http://purl.org/pav/contributedBy" );
    
    /** <p>The date this resource was contributed to. pav:contributedBy provides the 
     *  agent that contributed.</p>
     */
    public static final Property contributedOn = m_model.createProperty( "http://purl.org/pav/contributedOn" );
    
    /** <p>The geo-location of the agent that created the annotation.</p> */
    public static final Property createdAt = m_model.createProperty( "http://purl.org/pav/createdAt" );
    
    /** <p>An entity primary responsible for making the digital artifact. This property 
     *  is distinct from pav:authoredBy, which identifies who authored the knowledge 
     *  expressed by this resource, and pav:curatedBy, which identifies who curated 
     *  the knowledge into its current form. pav:createdBy is therefore more specific 
     *  than dct:createdBy - which might or might not be interpreted to cover these 
     *  creator. For instance, the author wrote 'this species has bigger wings than 
     *  normal' in his log book. The curator, going through the log book and identifying 
     *  important knowledge, formalizes this as 'locus perculus has wingspan &gt; 
     *  0.5m'. The creator enters this knowledge as a digital resource in the knowledge 
     *  system, thus creating the digital artifact (say as JSON, RDF, XML or HTML). 
     *  A different example is a news article. pav:authoredBy indicates the journalist 
     *  who wrote the article. pav:curatedBy can indicate the editor who made the 
     *  article conform to the news paper's style. pav:createdBy can indicate who 
     *  put the article on the web site. The software tool used by the creator to 
     *  make the digital resource (say Protege, Wordpress or OpenOffice) can be indicated 
     *  with pav:createdWith. The date the digital resource was created can be indicated 
     *  with pav:createdOn. The location the agent was when creating the digital resource 
     *  can be made using pav:createdAt.</p>
     */
    public static final Property createdBy = m_model.createProperty( "http://purl.org/pav/createdBy" );
    
    /** <p>The date of creation of the resource.</p> */
    public static final Property createdOn = m_model.createProperty( "http://purl.org/pav/createdOn" );
    
    /** <p>The software/tool used by the creator when making the digital resource. For 
     *  instance: Protege, Wordpress, LibreOffice.</p>
     */
    public static final Property createdWith = m_model.createProperty( "http://purl.org/pav/createdWith" );
    
    /** <p>An agent specialist responsible for shaping the expression in an appropriate 
     *  format. Often the primary agent responsible for ensuring the quality of the 
     *  representation. The curator is different from the creator of the digital resource 
     *  (although they are often the same), see pav:createdBy for a discussion.</p>
     */
    public static final Property curatedBy = m_model.createProperty( "http://purl.org/pav/curatedBy" );
    
    /** <p>The date this resource was curated. pav:curatedBy gives the agents that performed 
     *  the curation.</p>
     */
    public static final Property curatedOn = m_model.createProperty( "http://purl.org/pav/curatedOn" );
    
    public static final Property curates = m_model.createProperty( "http://purl.org/pav/curates" );
    
    /** <p>Derived from a different resource. Derivation conserns itself with derived 
     *  knowledge. If this resource has the same content as the other resource, but 
     *  has simply been transcribed to fit a different model (like XML -&gt; RDF or 
     *  SQL -&gt; CVS), use pav:importedFrom. If the content has been further refined 
     *  or modified, use pav:derivedFrom.</p>
     */
    public static final Property derivedFrom = m_model.createProperty( "http://purl.org/pav/derivedFrom" );
    
    /** <p>An entity responsible for importing the data. The importer is usually a software 
     *  entity which has done the transcription from the original source. See pav:importedFrom 
     *  for a discussion of import vs. retrieve vs. derived.</p>
     */
    
    public static final Property derivedDate = m_model.createProperty( "http://purl.org/pav/derivedDate" );
    
    public static final Property derivedBy = m_model.createProperty( "http://purl.org/pav/derivedBy" );
    
    public static final Property hasVersion = m_model.createProperty( "http://purl.org/pav/hasVersion" );

    public static final Property importedBy = m_model.createProperty( "http://purl.org/pav/importedBy" );
    
    /** <p>The original source of the imported information. Import means that the content 
     *  has been preserved, but transcribed somehow, for instance to fit a different 
     *  model. Examples of import are when the original was JSON and the current resource 
     *  is RDF, or where the original was an document scan, and this resource is the 
     *  text found through OCR. The difference between prov:derivedFrom and prov:importedFrom 
     *  is that the imported resource conveys the same knowledge/content as the original, 
     *  while a derived resource has somehow modified that knowledge to convey something 
     *  else. If the resource has been copied verbatim from the original (e.g. downloaded), 
     *  use pav:retrievedFrom. To indicate which agent(s) performed the import, use 
     *  pav:importedBy. Use pav:importedOn to indicate when it happened.</p>
     */
    public static final Property importedFrom = m_model.createProperty( "http://purl.org/pav/importedFrom" );
    
    /** <p>The date this resource was imported. See pav:importedFrom for a discussion 
     *  about import vs. retrieval.</p>
     */
    public static final Property importedOn = m_model.createProperty( "http://purl.org/pav/importedOn" );
    
    /** <p>The date of the last import of the resource. This property is used if this 
     *  version has been updated due to a re-import, rather than the import creating 
     *  new resources related using pav:previousVersion.</p>
     */
    public static final Property lastRefreshedOn = m_model.createProperty( "http://purl.org/pav/lastRefreshedOn" );
    
    /** <p>The date of the last update of the resource. An update is a change which did 
     *  not warrant making a new resource related using pav:previousVersion, for instance 
     *  correcting a spelling mistake.</p>
     */
    public static final Property lastUpdateOn = m_model.createProperty( "http://purl.org/pav/lastUpdateOn" );
    
    /** <p>The previous version of a resource in a lineage. For instance a news article 
     *  updated to correct factual information would point to the previous version 
     *  of the article with pav:previousVersion. If however the content has significantly 
     *  changed so that the two resources no longer share lineage (say a new news 
     *  article that talks about the same facts), they should be related using pav:derivedFrom. 
     *  A version number of this resource can be provided using the data property 
     *  pav:version.</p>
     */
    public static final Property previousVersion = m_model.createProperty( "http://purl.org/pav/previousVersion" );
    
    /** <p>The provider of the encoded information (PubMed, UniProt, Science Commons). 
     *  The provider might not coincide with the dct:publisher.</p>
     */
    public static final Property providedBy = m_model.createProperty( "http://purl.org/pav/providedBy" );
    
    /** <p>An entity responsible for retrieving the data from an external source. The 
     *  importer is usually a software entity which has done the retrieval from the 
     *  original source without performing any transcription. See pav:importedFrom 
     *  for a discussion of import vs. retrieve vs. derived.</p>
     */
    public static final Property retrievedBy = m_model.createProperty( "http://purl.org/pav/retrievedBy" );
    
    /** <p>The URI where a resource has been retrieved from. Retrieval indicates that 
     *  this resource has the same representation as the original resource. If the 
     *  resource has been somewhat transformed, use pav:importedFrom instead. The 
     *  time of the retrieval should be indicated using pav:retrievedOn. The agent 
     *  may be indicated with pav:retrievedBy.</p>
     */
    public static final Property retrievedFrom = m_model.createProperty( "http://purl.org/pav/retrievedFrom" );
    
    /** <p>The date this resource was retrieved.</p> */
    public static final Property retrievedOn = m_model.createProperty( "http://purl.org/pav/retrievedOn" );
    
    /** <p>A source which was accessed (but not retrieved or imported).</p> */
    public static final Property sourceAccessedAt = m_model.createProperty( "http://purl.org/pav/sourceAccessedAt" );
    
    /** <p>The agent who accessed the source</p> */
    public static final Property sourceAccessedBy = m_model.createProperty( "http://purl.org/pav/sourceAccessedBy" );
    
    /** <p>The date when the original source has been accessed to create the resource.</p> */
    public static final Property sourceAccessedOn = m_model.createProperty( "http://purl.org/pav/sourceAccessedOn" );
    
    /** <p>The date when the original source has been last accessed and verified.</p> */
    public static final Property sourceLastAccessedOn = m_model.createProperty( "http://purl.org/pav/sourceLastAccessedOn" );
    
    /** <p>The version number of a resource. This is a freetext string, typical values 
     *  are "1.5" or "21". The URI identifying the previous version can be provided 
     *  using prov:previousVersion.</p>
     */
    public static final Property version = m_model.createProperty( "http://purl.org/pav/version" );
    
}
