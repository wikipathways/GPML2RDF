# GPML2RDF

GPML2RDF creates RDF for WikiPathways [1]. Use cases, examples SPARQL queries, downloads, etc,
are available from the WikiPathways RDF Portal: http://rdf.wikipathways.org/

## Installation

### Setup

Clone or download this repository, and then import as an "Existing Java Project" in Eclipse.

There are two dependencies. You can made them available to Maven using the below instructions.

#### WikiPathways API client 

```shell
git clone https://github.com/wikipathways/wikipathways-api-client-java.git
cd wikipathways-api-client-java
cd org.wikipathways.client
ant test
mvn install:install-file -Dfile=org.wikipathways.client/org.wikipathways.webservice.api.bundle.jar \
  -DgroupId=org.pathvisio -DartifactId=wikipathways-client -Dversion=3.2.1.wprdf -Dpackaging=jar
```

#### PathVisio Core

```shell
git clone https://github.com/egonw/pathvisio-1.git
cd pathvisio
git checkout -b bridgedb/version3 bridgedb/version3
ant clean core.jar
mvn install:install-file -Dfile=modules/org.pathvisio.core.jar -DgroupId=org.pathvisio \
  -DartifactId=pathvisio-core -Dversion=3.4.0-bridgedb-3.0.1 -Dpackaging=jar
```

### Compiling

There is a `pom.xml` and you can compile the code with:

```shell
mvn clean install
```

### Try different pathways

Add GPML file in resources directory --> Open WP2RDF class --> Change pathwayFile location


1.Waagmeester A, Kutmon M, Riutta A, Miller R, Willighagen EL, Evelo CT, Pico AR. Using the Semantic Web for Rapid Integration of WikiPathways with Other Biological Online Data Resources. PLOS Computational Biology. 2016 Jun;12(6):e1004989+. Available from: http://dx.doi.org/10.1371/journal.pcbi.1004989
