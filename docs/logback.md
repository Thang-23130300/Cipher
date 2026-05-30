```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/app.log</file> <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/app.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory> </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>

    <logger name="com.yourpackage.repository" level="DEBUG" />

</configuration>
```

Cấp độ (Level),Khi nào nên dùng?,Ví dụ thực tế
* TRACE,"Mức chi tiết nhất, dùng để dò từng dòng code chạy.","log.trace(""Đã vào vòng lặp i = {}"", i);"
* DEBUG,Lưu thông tin kỹ thuật phục vụ cho việc tìm lỗi khi phát triển.,"log.debug(""Kết quả trả về từ API: {}"", responseBody);"
* INFO,Ghi lại các sự kiện mang tính cột mốc quan trọng của hệ thống.,"log.info(""Ứng dụng đã khởi chạy thành công trên cổng 8080"");"
* WARN,Các tình huống bất thường nhưng hệ thống vẫn tiếp tục chạy được.,"log.warn(""Hết hạn session của user {}, tự động logout"");"
* ERROR,Lỗi nghiêm trọng khiến chức năng hoặc hệ thống thất bại.,"log.error(""Không thể kết nối đến Database!"", exception);"