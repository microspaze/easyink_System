# 替换基础镜像为CentOS
FROM centos:7

# 设置字符集
ENV LC_ALL=zh_CN.utf8 \
    LANG=C.UTF-8 \
    LANGUAGE=zh_CN.utf8 \
    TZ=Asia/Shanghai
	
#创建工作目录
RUN mkdir /app

#定义工作目录
WORKDIR /app

#设置字符集
ENV LANG C.UTF-8

# 添加JAR文件
ARG ADMIN_FOLDER

#拷贝配置文件
COPY $ADMIN_FOLDER/src/main/resources/application.yml /config/application.yml

#拷贝会话存档处理文件so并给予权限
COPY $ADMIN_FOLDER/src/main/resources/lib/libWeWorkFinanceSdk_Java.so /usr/lib/libWeWorkFinanceSdk_Java.so
RUN chmod 777 /usr/lib/libWeWorkFinanceSdk_Java.so

#拷贝代码文件jar
ADD $ADMIN_FOLDER/target/easyink-admin.jar /app.jar

#安装特定版本JDK
RUN mkdir -p /usr/local/jdk && \
    curl -L https://gastaticqn.gatime.cn/OpenJDK8U-jdk_x64_linux_hotspot_8u432b06.tar.gz | \ 
	tar -xz -C /usr/local/jdk/

# 设置JDK环境变量
ENV JAVA_HOME=/usr/local/jdk/jdk8u432-b06 \
    JRE_HOME=$JAVA_HOME/jre \
    CLASSPATH=$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar \
    PATH=$PATH:$JAVA_HOME/bin

# 暴露应用端口（新增的关键行）
EXPOSE 8090

#设置默认的容器启动命令
CMD ["/bin/bash"]

# 启动命令（保持原逻辑）
ENTRYPOINT ["sh","-c","java -server $JAVA_OPTS -jar app.jar"]
