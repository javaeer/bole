FROM harbor.yunlou.net.cn/library/eclipse-temurin:17-jre

LABEL authors="MR. WANG"

ENV TZ=Asia/Shanghai

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo '$TZ' > /etc/timezone

COPY target/bole-server-1.0.0.jar bole-server.jar

VOLUME ["logs"]

ENTRYPOINT ["java","-jar","bole-server.jar"]

EXPOSE 8080