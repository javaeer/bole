# ✅ knife4j 新注解

---

## 当前主流：Swagger 3.0 + OpenAPI 3.0 注解（推荐用于 Spring Boot 2.6+ / 3.x）

> 对应依赖：`knife4j-openapi3-jakarta-spring-boot-starter`

### 🔹 类级别注解
| 旧注解 (Swagger 2) | 新注解 (OpenAPI 3.0) | 说明 |
|------------------|--------------------|------|
| `@Api`           | `@Tag`             | 描述 Controller 模块 |

**示例：**
```java
@Tag(name = "01. 用户管理", description = "提供用户增删改查接口")
@RestController
@RequestMapping("/users")
public class UserController { ... }
```

> 💡 建议在 `name` 中加序号（如 `"01. 用户管理"`），Knife4j 会自动按此排序。

---

### 🔹 方法级别注解
| 旧注解 | 新注解 | 说明 |
|-------|--------|------|
| `@ApiOperation` | `@Operation` | 描述接口功能 |
| `@ApiImplicitParam` / `@ApiImplicitParams` | `@Parameter` | 描述单个或多个参数 |
| `@ApiResponses` + `@ApiResponse` | `@ApiResponse`（配合 `@Operation`） | 描述响应状态码 |

**示例：**

```java
@Operation(summary = "获取用户详情", description = "根据ID查询用户信息")
@ApiResponse(responseCode = "200", description = "查询成功")
@ApiResponse(responseCode = "404", description = "用户不存在")
@GetMapping("/{id}")
public User getUserById(
    @Parameter(description = "用户ID", required = true, example = "1001")
    @PathVariable Long id) {
    return userService.findById(id);
}
```

---

### 🔹 实体类/字段注解
| 旧注解 | 新注解 | 说明 |
|-------|--------|------|
| `@ApiModelProperty` | `@Schema` | 描述 POJO 字段或类 |

**示例：**
```java
@Schema(description = "用户信息实体")
public class User {
    @Schema(description = "用户ID", example = "1001", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "用户名", requiredMode = RequiredMode.REQUIRED, example = "张三")
    private String name;
}
```

> ⚠️ 注意：`required = true` 已改为 `requiredMode = RequiredMode.REQUIRED`

---

## 🆕 Knife4j 特有增强注解（仍兼容 Swagger 2 和 OpenAPI 3）

这些注解是 Knife4j 自己扩展的，用于增强文档展示效果：

| 注解 | 作用 | 适用版本 |
|------|------|--------|
| `@ApiOperationSupport(order = 10)` | 控制接口在文档中的排序顺序 | Swagger 2 & OpenAPI 3 |
| `@DynamicParameters` | 动态参数（较少用） | Swagger 2 |
| `@Ignore` | 忽略某个接口（替代 `@Hidden`） | Swagger 2 |

> 🔔 注意：`@ApiOperationSupport` 在 OpenAPI 3 中依然可用，但需确保引入的是 Knife4j 相关 starter。

---

## 📌 总结：如何选择注解？

| 项目技术栈 | 推荐注解体系 | 依赖示例 |
|-----------|-------------|--------|
| Spring Boot ≤ 2.5 + Swagger 2 | 使用 `@Api`, `@ApiOperation`, `@ApiModelProperty` | `knife4j-spring-boot-starter` |
| Spring Boot ≥ 2.6 或 Spring Boot 3 + OpenAPI 3.0 | 使用 `@Tag`, `@Operation`, `@Schema`, `@Parameter` | `knife4j-openapi3-jakarta-spring-boot-starter` |

---

# Maven构建插件使用案例详解

```xml
    <!-- ******************************************************** -->
    <!-- Maven构建配置部分  -->
    <!--此部分定义了项目的构建过程，包括最终输出名称、插件配置和资源处理规则-->
    <!-- ******************************************************** -->
    <build>
        <!--
          最终生成文件的命名规则
          ${project.artifactId}：项目标识符，如"my-app"
          ${project.version}：项目版本号，如"1.0.0"
          示例：my-app-1.0.0.jar
        -->
        <finalName>${project.artifactId}-${project.version}</finalName>

        <!-- Maven插件配置区 -->
        <plugins>
            <!--
              Spring Boot Maven插件
              作用：将项目打包为可执行的Spring Boot应用（fat jar）
              文档：https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/html/
            -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <!--
                      排除依赖项配置
                      指定在打包可执行jar时需要排除的依赖项
                      Lombok是编译时依赖，运行时不需要，因此可以排除以减少jar包大小
                    -->
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                        <exclude>
                            <groupId>org.mapstruct</groupId>
                            <artifactId>mapstruct-processor</artifactId>
                        </exclude>
                    </excludes>
                    <!--
                      指定Spring Boot应用的主启动类
                      该类必须包含main方法，是应用程序的入口点
                    -->
                    <mainClass>cn.net.yunlou.bole.BoleApplication</mainClass>
                    <!--
                      设置为true会生成可执行的jar/war文件
                      在Linux/Unix系统中，可以直接使用 ./app.jar 方式运行
                    -->
                    <executable>true</executable>

                    <!-- 优化：添加JVM启动参数 -->
                    <jvmArguments>
                        -Xmx512m
                        -Xms256m
                        -XX:+UseG1GC
                        -XX:+HeapDumpOnOutOfMemoryError
                        -XX:HeapDumpPath=./heapdump.hprof
                    </jvmArguments>

                </configuration>
                <executions>
                    <!--
                      执行目标配置
                      repackage目标：重新打包生成的jar，使其成为可执行的Spring Boot jar
                    -->
                    <execution>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>

            <!--
              Docker构建插件（Spotify版）
              作用：使用Dockerfile构建Docker镜像并推送到仓库
              注意：此插件已由Spring Boot官方Docker插件取代，但仍可使用
            -->
            <plugin>
                <groupId>com.spotify</groupId>
                <artifactId>dockerfile-maven-plugin</artifactId>
                <version>1.4.13</version>
                <configuration>
                    <!--
                      Docker镜像仓库和名称
                      ${docker.image.prefix}：通常在properties中定义，如"myregistry"
                      示例：myregistry/my-app:1.0.0
                    -->
                    <repository>${docker.image.prefix}/${project.artifactId}</repository>
                    <!-- Docker镜像标签，使用项目版本号 -->
                    <tag>${project.version}</tag>
                    <!--
                      构建参数，传递给Dockerfile
                      JAR_FILE：指定要打包到镜像中的jar文件路径
                    -->
                    <buildArgs>
                        <JAR_FILE>target/${project.build.finalName}.jar</JAR_FILE>
                    </buildArgs>
                </configuration>
            </plugin>



            <!--
              Maven编译插件
              作用：编译项目源代码，配置Java版本和编译参数
              文档：https://maven.apache.org/plugins/maven-compiler-plugin/
            -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>${maven-compiler-plugin.version}</version>
                <configuration>
                    <!-- 源代码兼容版本 -->
                    <source>${java.version}</source>
                    <!-- 目标字节码版本 -->
                    <target>${java.version}</target>
                    <!-- 源代码文件编码 -->
                    <encoding>${project.build.sourceEncoding}</encoding>
                    <!--
                      设置为true，保留方法参数名信息（用于反射获取参数名）
                      对Spring MVC等框架很有用
                    -->
                    <parameters>true</parameters>
                    <!--
                      启用外部进程编译
                      优点：避免内存泄漏，可以设置独立内存参数
                    -->
                    <fork>true</fork>
                    <!-- 编译进程的初始内存大小 -->
                    <meminitial>512m</meminitial>
                    <!-- 编译进程的最大内存大小 -->
                    <maxmem>2048m</maxmem>
                    <!--
                      注解处理器路径配置
                      指定编译时需要使用的注解处理器
                    -->
                    <annotationProcessorPaths>
                        <!-- Lombok注解处理器，用于生成getter/setter等方法 -->
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>${lombok.version}</version>
                        </path>
                        <!-- MapStruct注解处理器，用于生成对象映射代码 -->
                        <path>
                            <groupId>org.mapstruct</groupId>
                            <artifactId>mapstruct-processor</artifactId>
                            <version>${mapstruct.version}</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>

            <!--
              资源处理插件
              作用：处理项目资源文件，如配置文件、属性文件等
              文档：https://maven.apache.org/plugins/maven-resources-plugin/
            -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
                <version>${maven-resources-plugin.version}</version>
                <configuration>
                    <!-- 资源文件编码 -->
                    <encoding>${project.build.sourceEncoding}</encoding>
                </configuration>
            </plugin>

            <!--
              代码格式化插件（Spotless）
              作用：在构建过程中自动格式化代码，保持代码风格一致
              文档：https://github.com/diffplug/spotless/tree/main/plugin-maven
            -->
            <plugin>
                <groupId>com.diffplug.spotless</groupId>
                <artifactId>spotless-maven-plugin</artifactId>
                <version>${spotless-maven-plugin.version}</version>
                <configuration>
                    <!-- Java代码格式化配置 -->
                    <java>
                        <!-- 使用Google Java Format进行代码格式化 -->
                        <googleJavaFormat>
                            <version>1.17.0</version>
                            <!-- 代码风格：AOSP（Android开源项目风格） -->
                            <style>AOSP</style>
                        </googleJavaFormat>
                        <!-- 移除未使用的import语句 -->
                        <removeUnusedImports/>
                        <!-- 修剪行尾空格 -->
                        <trimTrailingWhitespace/>
                        <!-- 文件以换行符结束 -->
                        <endWithNewline/>
                        <!--
                          指定需要格式化的文件范围
                          **/*.java：匹配所有子目录下的Java文件
                        -->
                        <includes>
                            <include>src/main/java/**/*.java</include>
                            <include>src/test/java/**/*.java</include>
                        </includes>
                    </java>
                </configuration>
                <executions>
                    <!--
                      执行配置：在compile阶段执行spotless的check目标
                      check目标：检查代码格式是否符合规范，不符合则构建失败
                    -->
                    <execution>
                        <goals>
                            <goal>check</goal>
                        </goals>
                        <phase>compile</phase>
                    </execution>
                </executions>
            </plugin>

            <!--
              Maven执行插件
              作用：在Maven构建过程中执行Java类
              文档：https://www.mojohaus.org/exec-maven-plugin/
            -->
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>${exec-maven-plugin.version}</version>
                <configuration>
                    <!--
                      指定要执行的Java主类
                      这里配置的是一个代码生成器，用于生成基础代码
                    -->
                    <mainClass>cn.net.yunlou.bole.generator.CodeGenerator</mainClass>
                </configuration>
            </plugin>
        </plugins>

        <!--
          资源文件处理配置
          定义了资源文件的复制和过滤规则
        -->
        <resources>
            <!--
              第一组资源：需要变量替换的资源文件
              filtering=true：启用Maven属性过滤，将${property}替换为实际值
            -->
            <resource>
                <!-- 资源文件所在目录 -->
                <directory>src/main/resources</directory>
                <!-- 启用属性过滤 -->
                <filtering>true</filtering>
                <!--
                  包含的文件类型：需要进行变量替换的配置文件
                  通常包含需要注入Maven属性的文件
                -->
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.yml</include>
                    <include>**/*.yaml</include>
                    <include>**/*.xml</include>
                </includes>
            </resource>

            <!--
              第二组资源：不需要变量替换的静态资源文件
              filtering=false：禁用属性过滤，原样复制
            -->
            <resource>
                <directory>src/main/resources</directory>
                <!-- 禁用属性过滤 -->
                <filtering>false</filtering>
                <!--
                  排除的文件类型：上面已经处理过的配置文件
                  其他文件（如图片、静态HTML等）将原样复制
                -->
                <excludes>
                    <exclude>**/*.properties</exclude>
                    <exclude>**/*.yml</exclude>
                    <exclude>**/*.yaml</exclude>
                    <exclude>**/*.xml</exclude>
                </excludes>
            </resource>
        </resources>
    </build>

```



### 1. **Spring Boot应用打包和运行**

```bash
# 1. 清理并打包项目
# 会执行clean、compile、test、package等阶段
mvn clean package

# 2. 跳过测试打包
mvn clean package -DskipTests

# 3. 运行Spring Boot应用
# 方式一：使用spring-boot插件运行
mvn spring-boot:run

# 方式二：运行打包后的jar
java -jar target/bole-system-1.0.0-SNAPSHOT.jar

# 4. 生产环境运行（设置激活的profile）
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# 5. 查看Spring Boot应用信息
mvn spring-boot:help
```

### 2. **Docker镜像构建和推送**

```bash
# 1. 构建Docker镜像（需要先打包）
mvn clean package dockerfile:build

# 2. 构建并推送镜像到仓库
mvn dockerfile:build dockerfile:push

# 3. 指定镜像标签构建
mvn dockerfile:build -Ddockerfile.tag=v1.2.3

# 4. 清理本地Docker镜像
mvn dockerfile:remove

# 注意：需要先安装Docker并确保Docker服务运行
```

### 3. **代码编译和格式化**

```bash
# 1. 编译项目
mvn compile

# 2. 检查代码格式（spotless插件）
mvn spotless:check

# 3. 自动格式化代码
mvn spotless:apply

# 4. 只格式化特定文件
mvn spotless:apply -DspotlessFiles=src/main/java/com/example/UserService.java

# 5. 查看编译详情
mvn compile -X
```

### 4. **代码生成器使用**

```bash
# 1. 执行代码生成器
# 需要先编写CodeGenerator类
mvn exec:java

# 2. 传递参数给代码生成器
mvn exec:java -Dexec.args="--table user_info"

# 3. 指定不同的主类执行
mvn exec:java -Dexec.mainClass="cn.net.yunlou.bole.generator.EntityGenerator"

# 4. 在指定阶段自动执行代码生成
# 在pom.xml中配置execution到generate-sources阶段
```

### 5. **资源处理和配置文件**

```bash
# 1. 处理资源文件（变量替换）
mvn resources:resources

# 2. 查看资源处理详情
mvn resources:resources -X

# 3. 复制资源文件到指定目录
mvn resources:copy-resources

# 4. 测试配置文件变量替换
# 在application.yml中使用${project.version}
# 打包后会替换为实际版本号
```

### 6. **完整构建流程案例**

```bash
# 案例：完整的CI/CD构建流程脚本
#!/bin/bash

# 1. 设置环境变量
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk
export MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=512m"

# 2. 清理工作空间
mvn clean

# 3. 代码质量检查
echo "=== 代码格式化检查 ==="
mvn spotless:check
if [ $? -ne 0 ]; then
    echo "代码格式检查失败，正在自动格式化..."
    mvn spotless:apply
    git add .
    git commit -m "style: auto format code"
fi

# 4. 运行测试
echo "=== 运行单元测试 ==="
mvn test -DskipITs

# 5. 打包应用
echo "=== 打包应用 ==="
mvn package -DskipTests

# 6. 集成测试
echo "=== 运行集成测试 ==="
mvn verify -Dit.test=*IT

# 7. 构建Docker镜像
echo "=== 构建Docker镜像 ==="
mvn dockerfile:build -Ddockerfile.tag=${BUILD_NUMBER}

# 8. 推送镜像到仓库
echo "=== 推送Docker镜像 ==="
mvn dockerfile:push -Ddockerfile.tag=${BUILD_NUMBER}

# 9. 生成构建报告
echo "=== 生成构建报告 ==="
mvn site:site
```

### 7. **多环境构建配置**

```xml
<!-- 在pom.xml中添加profiles配置 -->
<profiles>
    <profile>
        <id>dev</id>
        <properties>
            <spring.profiles.active>dev</spring.profiles.active>
            <docker.image.prefix>dev-registry</docker.image.prefix>
        </properties>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
    </profile>
    
    <profile>
        <id>test</id>
        <properties>
            <spring.profiles.active>test</spring.profiles.active>
            <docker.image.prefix>test-registry</docker.image.prefix>
        </properties>
    </profile>
    
    <profile>
        <id>prod</id>
        <properties>
            <spring.profiles.active>prod</spring.profiles.active>
            <docker.image.prefix>prod-registry</docker.image.prefix>
        </properties>
    </profile>
</profiles>
```

使用不同profile构建：
```bash
# 开发环境构建（默认）
mvn clean package

# 测试环境构建
mvn clean package -Ptest

# 生产环境构建
mvn clean package -Pprod -DskipTests
```

### 8. **常见问题解决方案**

#### 问题1：Lombok编译错误
```bash
# 解决方案：确保注解处理器配置正确
mvn clean compile

# 如果仍有问题，尝试：
mvn clean install -U
```

#### 问题2：内存不足导致编译失败
```xml
<!-- 在pom.xml中调整内存设置 -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <fork>true</fork>
        <meminitial>1024m</meminitial>
        <maxmem>4096m</maxmem>
        <compilerArgs>
            <arg>-J-XX:MaxPermSize=512m</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

#### 问题3：Docker构建权限问题
```bash
# 解决方案：将用户加入docker组
sudo usermod -aG docker $USER

# 或者使用root权限运行
sudo mvn dockerfile:build
```

### 9. **进阶使用技巧**

#### 并行构建加速
```bash
# 使用多个线程并行构建
mvn clean package -T 4

# 每个CPU核心一个线程
mvn clean package -T 1C
```

#### 构建时间分析
```bash
# 生成构建时间报告
mvn clean package -Dmaven.build.timestamp.enabled=true

# 使用构建分析插件
mvn org.apache.maven.plugins:maven-timeline-plugin:timeline
```

#### 离线构建
```bash
# 在无法访问网络的环境中使用
# 首先在有网络的环境下载依赖
mvn dependency:go-offline

# 然后在离线环境构建
mvn clean package -o
```

### 10. **Git Hook集成示例**

在 `.git/hooks/pre-commit` 中添加：
```bash
#!/bin/bash
echo "Running pre-commit checks..."

# 运行代码格式化检查
mvn spotless:check

if [ $? -ne 0 ]; then
    echo "Code formatting issues found. Please run: mvn spotless:apply"
    exit 1
fi

# 运行编译检查
mvn compile -DskipTests

if [ $? -ne 0 ]; then
    echo "Compilation failed. Please fix compilation errors before committing."
    exit 1
fi

echo "Pre-commit checks passed!"
```

## 三、最佳实践建议

1. **版本管理**：
    - 所有插件版本应在properties中统一管理
    - 使用具体的版本号，避免使用LATEST

2. **构建优化**：
    - 大型项目使用并行构建（-T参数）
    - 合理配置JVM内存参数
    - 使用Maven Wrapper确保构建环境一致

3. **Docker构建**：
    - 使用多阶段构建减小镜像大小
    - 设置合适的.dockerignore文件
    - 定期清理旧镜像

4. **代码质量**：
    - 将spotless:check集成到CI/CD流程
    - 使用SonarQube等工具进行代码分析
    - 设置合理的.gitignore文件

5. **资源管理**：
    - 配置文件分离：dev/test/prod环境独立配置
    - 敏感信息使用环境变量或配置中心
    - 静态资源使用CDN或对象存储

通过以上配置和使用案例，可以建立一个完整的、可维护的Spring Boot项目构建体系，支持从开发、测试到部署的全流程自动化。