#!/bin/bash

wpid=$1
match="/${wpid}/,/Tests/{/${wpid}/b;/Tests/b;p}"

mvn clean test -Dtest=org.wikipathways.wp2rdf.${wpid}Test | sed -n ${match} | grep -v ".bridge" > ${wpid}.ttl
