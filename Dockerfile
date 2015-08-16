FROM java:8-jre
ENV JAVA_HOME /usr/lib/jvm/java-8-*/

ADD target/discod-services.jar /app/
WORKDIR /app

EXPOSE 53
CMD java -jar discod-services.jar 53 service