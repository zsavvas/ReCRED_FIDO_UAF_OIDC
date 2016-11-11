#!/usr/bin/env bash

~/tools/admin/openam/bin/ssoadm delete-auth-cfgs -m Test -e / -u amadmin -f ~/tools/pwd.txt
~/tools/admin/openam/bin/ssoadm delete-auth-instances -e / -m ReCredFido -u amadmin -f ~/tools/pwd.txt

~/tools/admin/openam/bin/ssoadm unregister-auth-module -u amadmin -f ~/tools/pwd.txt -a com.verizon.iam.openam.RecredFidoAuth
~/tools/admin/openam/bin/ssoadm delete-svc -u amadmin -f ~/tools/pwd.txt -s iPlanetAMAuthRecredFidoAuthService


~/tools/admin/openam/bin/ssoadm create-svc -u amadmin -f ~/tools/pwd.txt -X ~/git/pdp/recred/openam/recredfido/src/main/resources/amAuthRecredFidoAuth.xml
~/tools/admin/openam/bin/ssoadm register-auth-module -u amadmin -f ~/tools/pwd.txt -a com.verizon.iam.openam.RecredFidoAuth

cp ~/git/pdp/recred/openam/recredfido/target/recredfido-1.0-SNAPSHOT.jar /opt/tomcat/webapps/openam/WEB-INF/lib/
cp ~/git/pdp/recred/openam/recredfido/src/main/resources/amAuthRecredFidoAuth.properties /opt/tomcat/webapps/openam/WEB-INF/classes
cp ~/git/pdp/recred/openam/recredfido/src/main/resources/config/auth/default/RecredFidoAuth.xml /opt/tomcat/webapps/openam/config/auth/default/

~/tools/admin/openam/bin/ssoadm create-auth-instance -m ReCredFido -t RecredFidoAuth -u amadmin -f ~/tools/pwd.txt -e /
~/tools/admin/openam/bin/ssoadm create-auth-cfg -e / -m Test -u amadmin -f ~/tools/pwd.txt
~/tools/admin/openam/bin/ssoadm update-auth-cfg-entr -m Test -e / -u amadmin -f ~/tools/pwd.txt -a "ReCredFido|REQUIRED"

/opt/tomcat/bin/shutdown.sh
sleep 10s
/opt/tomcat/bin/startup.sh
