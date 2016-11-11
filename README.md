## Integration of FIDO UAF and OpenID Connect
This repository consists of an implementation of the FIDO UAF protocol (inspired from  the Ebay implementation which can be found https://github.com/eBay/UAF) and an implementation of a custom authentication module within OpenAM (https://forgerock.org/openam/) that enables the authentication of users with the FIDO UAF protocol.

### Installation
**Prerequisities**
  - Maven at least v3.1
  - Tomcat Server
  - Git

### OpenAM 
In order to have an OpenID Connect Provider we will install the OpenAM implementation alongside with the necessary tools in order to install our custom authentication module that will communicate with the FIDO UAF Server to perform authentication according to the FIDO UAF Specification.

##### OpenAM Basic Installation
1. Create a free account at https://backstage.forgerock.com/#!/ to obtain the source code
2. Visit https://stash.forgerock.org/projects/OPENAM to gain the git clone command
3. Clone the repository
4. Compile the repository by visiting the top directory and executing *mvn clean install*
5. Rename the war file that is generated in the openam/openam-server/target directory to openam.war
6. Copy the openam.war file to the Tomcat server webapps directory
7. Visit server_ip:8080/openam
8. Make the default configuration for openam
9. Login to OpenAM
10. Add an Oauth2/Open Id connect provider. To do so, go to dashboard -> Configure OAuth provider -> Configure OpenID Connect. After that press the create button.

##### OpenAM Administration Tools Installation
1. Download zip from here http://maven.forgerock.org/repo/repo/org/forgerock/openam/openam-distribution-ssoadmintools/14.0.0-SNAPSHOT/
2. Extract the zip
3. Run setup file 

##### OpenAM Custom Authentication Module Installation
1. Clone this repository
2. Change directory to the folder of the Authentication Module
3. Follow the instructions on this link in order to compile and install the custom Authentication Module https://wikis.forgerock.org/confluence/display/openam/Write+a+custom+authentication+module#Writeacustomauthenticationmodule-Compileyourcustomauthenticationmodule. We have also included a file that contains an automated way to install the custom authentication module but needs to be adjusted according to your setup paths (installlcustAuthMod.sh)

#### Configuring OpenAM to use the newly installed Module
1. Login to OpenAM (server_ip:8080/openam, credentials are the ones that you set during the initial configuration of OpenAM)
2. You need to create a new authentication chain. To do this go to Authentication->Chains->Add chain
3. Give a name for the chain and then select the authentication module you want to use
4. Then go to Authentication->Settings
5. Set the Administrator Authentication Configuration to ldapService and Organization Authentication Configuration to $Name_of_your_newly_created_chain
6. Now by default the custom authentication module will be used. To use the regular ldapService to access the administrator console visit server_ip:8080/openam/console


### Compiling and Installing FIDO UAF Server
```sh
1. cd fido-uaf-core
2. mvn clean install
3. cd ../fidouaf
4. mvn clean install 
5. cp fidouaf/target/fidouaf-ci.war $TOMCAT/webapps
```
Visit server_ip:8080/fidouaf-ci/about to verify that the FIDO Server is running

### Adding the FIDO Endpoint to OpenAM
1. Login to OpenAM
2. Press the Configuration Button then on the Authentication Tab select the name of your custom authentication module
3. Make sure that the base url of the FIDO Server Endpoint match the url of your deployment.

This research has been fully funded by the European Commission as part of the ReCRED project (Horizon H2020 Framework Programme of the European Union under GA number 653417).






