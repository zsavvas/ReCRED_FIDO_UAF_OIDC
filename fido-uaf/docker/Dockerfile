FROM docker.io/netways/sles-12-x86_64

#TODO get Oracle java from a another place
COPY jre-8u91-linux-x64.rpm /home

#Install Java
RUN rpm -ivh /home/jre-8u91-linux-x64.rpm

#Install Tomcat
ENV TOMCAT_MAJOR 8
ENV TOMCAT_VERSION 8.5.3
ENV TOMCAT_TGZ_URL https://www.apache.org/dist/tomcat/tomcat-$TOMCAT_MAJOR/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz

ENV TOMCAT_HOME /usr/local/tomcat
ENV CATALINA_HOME $TOMCAT_HOME/apache-tomcat-$TOMCAT_VERSION
ENV PATH $CATALINA_HOME/bin:$PATH
RUN mkdir -p "$TOMCAT_HOME"
WORKDIR $TOMCAT_HOME

RUN wget "$TOMCAT_TGZ_URL" --no-check-certificate && tar -xvf apache-tomcat-$TOMCAT_VERSION.tar.gz && rm *.tar.gz

EXPOSE 8080

#COPY ../fidouaf/build/libs/fidouaf.war webapps

CMD ["catalina.sh", "run"]
