FROM sirile/minijavabox
ENV JAVA_HOME /usr/lib/jvm/java-8-*/

ADD target/discod-service-resolver.jar /app/
WORKDIR /app

EXPOSE 53
CMD java -jar discod-service-resolver.jar 53 image container