# 替换基础镜像为CentOS
FROM ccr.ccs.tencentyun.com/easyink/wecomscrm:V1.37.8

# 添加JAR文件
ARG ADMIN_FOLDER

#拷贝代码文件jar
ADD $ADMIN_FOLDER/target/easyink-admin.jar /app.jar