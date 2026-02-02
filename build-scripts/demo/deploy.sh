#!/bin/bash

sudo service phrweb stop
sudo rm "/var/phrweb/phrweb.jar"
sudo cp "/home/pciadmin/codeship_upload/PhrWeb.jar" "/var/phrweb/phrweb.jar"
sudo chmod +x "/var/phrweb/phrweb.jar"
sudo service phrweb start

sudo service external-api stop
sudo rm "/var/external_api/external-api.jar"
sudo cp "/home/pciadmin/codeship_upload/ExternalAPI.jar" "/var/external_api/external-api.jar"
sudo chmod +x "/var/external_api/external-api.jar"
sudo service external-api start

sudo service palatium-care-integration stop
sudo rm "/var/palatium_care_integration/palatium-care-integration.jar"
sudo cp "/home/pciadmin/codeship_upload/PalatiumCareIntegration.jar" "/var/palatium_care_integration/palatium-care-integration.jar"
sudo chmod +x "/var/palatium_care_integration/palatium-care-integration.jar"
sudo service palatium-care-integration start

sudo /etc/init.d/tomcat6 stop
sudo rm -rf /var/lib/tomcat6/webapps/exchange.war
sudo rm -rf /var/lib/tomcat6/webapps/exchange
sudo rm -rf /var/lib/tomcat6/webapps/AuditReport.war
sudo rm -rf /var/lib/tomcat6/webapps/AuditReport
sudo rm -rf /var/lib/tomcat6/webapps/openxds-web.war
sudo rm -rf /var/lib/tomcat6/webapps/openxds-web
sudo rm -rf /var/lib/tomcat6/webapps/adtsender.war
sudo rm -rf /var/lib/tomcat6/webapps/adtsender
sudo cp /home/pciadmin/codeship_upload/exchange.war /var/lib/tomcat6/webapps/
sudo cp /home/pciadmin/codeship_upload/AuditReport.war /var/lib/tomcat6/webapps/
sudo cp /home/pciadmin/codeship_upload/openxds-web.war /var/lib/tomcat6/webapps/
sudo cp /home/pciadmin/codeship_upload/adtsender.war /var/lib/tomcat6/webapps/
source /home/pciadmin/current_deployed_exchange_version/build_number.txt
build_number=$((build_number + 1))
sed -i "1s/.*/build_number=$build_number/" /home/pciadmin/current_deployed_exchange_version/build_number.txt
sudo /etc/init.d/tomcat6 start


