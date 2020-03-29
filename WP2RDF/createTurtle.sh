#!/bin/bash

wpid=$1
match="/${wpid}/,/Tests/p"

mvn clean test -Dtest=org.wikipathways.wp2rdf.${wpid}Test \
  | sed -n ${match} \
  | grep -v ".bridge" \
  | grep -v "=== ${wpid}" \
  | grep -v "Tests run" \
  > ${wpid}.ttl
