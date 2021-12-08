FROM amazoncorretto:11-alpine-jdk
MAINTAINER jlengrand
COPY build/libs/online-payments-0.0.1-SNAPSHOT.jar online-payments-spring-adyen-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/online-payments-spring-adyen-0.0.1-SNAPSHOT.jar"]
