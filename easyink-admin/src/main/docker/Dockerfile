# 替换基础镜像为CentOS
FROM centos:7

# 设置字符集
ENV LC_ALL=zh_CN.utf8 \
    LANG=C.UTF-8 \
    LANGUAGE=zh_CN.utf8 \
    TZ=Asia/Shanghai

# 安装特定版本JDK（替换路径：https://gastaticqn.gatime.cn/OpenJDK8U-jdk_x64_linux_8u342b07.tar.gz）
RUN mkdir -p /usr/local/jdk && \
    curl -L https://github.com/AdoptOpenJDK/openjdk8-upstream-binaries/releases/download/jdk8u342-b07/OpenJDK8U-jdk_x64_linux_8u342b07.tar.gz | \
    tar -xz -C /usr/local/jdk/

# 设置JDK环境变量
ENV JAVA_HOME=/usr/local/jdk/openjdk-8u342-b07 \
    JRE_HOME=$JAVA_HOME/jre \
    CLASSPATH=$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar \
    PATH=$PATH:$JAVA_HOME/bin

# 设置工作目录
WORKDIR /app

# 添加JAR文件
ARG JAR_FILE
ADD $JAR_FILE /app/app.jar

# 添加企微会话同步SDK
ARG FINANCE_SDK_FILE
ADD $FINANCE_SDK_FILE /usr/lib/libWeWorkFinanceSdk_Java.so

# 暴露应用端口（新增的关键行）
EXPOSE 8090

# 设置默认的容器启动命令（新增）
CMD ["/bin/bash"]

# 启动命令（保持原逻辑）
ENTRYPOINT ["sh","-c","java -server $JAVA_OPTS -jar /app.jar"]