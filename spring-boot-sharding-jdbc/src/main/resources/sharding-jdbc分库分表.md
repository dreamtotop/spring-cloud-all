> 本文在提供完整代码示例，可见 https://github.com/YunaiV/SpringBoot-Labs 的 [lab-18](https://github.com/YunaiV/SpringBoot-Labs/tree/master/lab-18) 目录。
>
> 原创不易，给点个 [Star](https://github.com/YunaiV/SpringBoot-Labs/stargazers) 嘿，一起冲鸭！

# 1. 概述

因为市面上已经非常不错的分库分表的资料，所以艿艿就不在尴尬的瞎哔哔一些内容。推荐阅读两个资料：

- [《Apache ShardingSphere 官方文档》](https://shardingsphere.apache.org/document/current/cn/overview/)

  > ShardingSphere 是目前最好用的数据库中间件之一，很多时候，我们使用它来实现分库分表，或者读写分离。
  >
  > 当然，它不仅仅能够提供上述两个功能，也能提供分布式事务、数据库治理。

- [《为什么几乎所有的开源数据库中间件都是国内公司开源的？并且几乎都停止了更新？》](https://www.zhihu.com/question/352256403)

  > 这个是知乎上的一个讨论，适合我们来吃瓜，看看各路大神对这块的想法。
  >
  > 生命不息，吃瓜不止。

目前，国内使用比较多的分库分表的中间件，主要有：

- [Apache ShardingSphere](https://github.com/apache/incubator-shardingsphere)
- [Mycat](https://github.com/MyCATApache/Mycat-Server)

个人比较推荐使用 ShardingSphere ，主要有几个原因：

- 在京东、当当等大型互联网公司落地使用，并且已经提供的有 100+ 企业的成功案例。

  > 关于 100+ 案例，并不是指的 100+ 公司采用，而是登记给 ShardingSphere 团队的公司数。实际肯定远超这个数字，毕竟大多数团队采用的话，都没去主动登记。

- 社区强大，已经进入 Apache 孵化。并且有京东全职的开发团队，也有总共 88+ [contributors](https://github.com/apache/incubator-shardingsphere/graphs/contributors) 。

- 功能完善，不仅仅提供分库分表、读写分离，也提供分布式事务、数据库治理等功能。

- 代码质量非常高。项目负责人 [张亮](https://github.com/terrymanu) 简直是个代码质量狂魔！

  > 之前学习 Sharding-JDBC 时，尝试写过一套[源码解析](http://www.iocoder.cn/categories/Sharding-JDBC/?self)文章。代码简直易读到爆炸。
  >
  > 亮哥自己也在某次采访中，提到如下内容：以工匠精神去雕琢细节。 开放出去的源代码会在一定的范围内引起共鸣。一个值得研读开源项目，其代码必须经过雕琢，让其规范、一致、优雅、易懂，尽量将细节做到极致。通过代码质量给予使用者信心。
  >
  > 所以呢，非常推荐胖友尝试去阅读下 ShardingSphere 。

可能会有胖友会提到 Mycat ，为什么不推荐使用它？😈 默默不评价。如果在选型中考虑 Mycat 的话，推荐可以看看 [dble](https://github.com/actiontech/dble) 项目。

本文，我们会使用 ShardingSphere 的子项目 Sharding-JDBC 完成分库分表和读写分离的功能，会提供两个示例。如果胖友对 Sharding-JDBC 不是很了解，推荐先去阅读下 [《Apache ShardingSphere 官方文档 —— 概览》](https://shardingsphere.apache.org/document/current/cn/overview/) ，很简短。

# 2. 分库分表

> 示例代码对应仓库：[lab-18-sharding-datasource-01](https://github.com/YunaiV/SpringBoot-Labs/tree/master/lab-18/lab-18-sharding-datasource-01) 。

本小节，我们会使用 Sharding-JDBC 实现分库分表的功能。我们会将 `orders` 订单表，拆分到 **2** 个库，每个库 **4** 个订单表，一共 **8** 个表。库表的情况如下：



```
lab18_orders_0 库
  ├── orders_0
  └── orders_2
  └── orders_4
  └── orders_6
lab18_orders_1 库
  ├── orders_1
  └── orders_3
  └── orders_5
  └── orders_7
```



- 偶数后缀的表，在 `lab18_orders_0` 库下。
- 奇数后缀的表，在 `lab18_orders_1` 库下。

我们使用订单表上的 `user_id` 用户编号，进行分库分表的规则：

- 首先，按照 `index = user_id % 2` 计算，将记录路由到 `lab18_orders_${index}` 库。
- 然后，按照 `index = user_id % 8` 计算，将记录路由到 `orders_${index}` 表。

举个例子：

| 用户编号 | 库               | 表         |
| :------- | :--------------- | :--------- |
| 1        | `lab18_orders_1` | `orders_1` |
| 2        | `lab18_orders_0` | `orders_2` |
| 3        | `lab18_orders_1` | `orders_3` |
| 4        | `lab18_orders_0` | `orders_4` |
| 5        | `lab18_orders_1` | `orders_5` |
| 6        | `lab18_orders_0` | `orders_6` |
| 7        | `lab18_orders_1` | `orders_7` |
| 8        | `lab18_orders_0` | `orders_8` |

考虑到部分表不需要分库分表，例如说 `order_config` 订单配置表，所以我们会配置路由到 `lab18_orders_0` 库下。

具体 `orders` 和 `order_config` 两个表的创建语句，我们在 [TODO](https://www.iocoder.cn/Spring-Boot/sharding-datasource/?vip#) 提供。

因为本文重心在于提供示例。胖友可以碰到不理解的地方，看看如下文档：

- [《ShardingSphere > 概念 & 功能 > 数据分片》](https://shardingsphere.apache.org/document/current/cn/features/sharding/)
- [《ShardingSphere > 用户手册 > Sharding-JDBC > 使用手册 > 数据分片》](https://shardingsphere.apache.org/document/current/cn/manual/sharding-jdbc/usage/sharding/)
- [《ShardingSphere > 用户手册 > Sharding-JDBC > 配置手册》](https://shardingsphere.apache.org/document/current/cn/manual/sharding-jdbc/configuration/)

## 2.1 引入依赖

在 [`pom.xml`](https://github.com/YunaiV/SpringBoot-Labs/blob/master/lab-18/lab-18-sharding-datasource-01/pom.xml) 文件中，引入相关依赖。



```java
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.3.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>lab-18-sharding-datasource-01</artifactId>

    <dependencies>
        <!-- 实现对数据库连接池的自动化配置 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
        <dependency> <!-- 本示例，我们使用 MySQL -->
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.48</version>
        </dependency>

        <!-- 实现对 MyBatis 的自动化配置 -->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.1.1</version>
        </dependency>

        <!-- 实现对 Sharding-JDBC 的自动化配置 -->
        <dependency>
            <groupId>org.apache.shardingsphere</groupId>
            <artifactId>sharding-jdbc-spring-boot-starter</artifactId>
            <version>4.0.0-RC2</version>
        </dependency>

        <!-- 保证 Spring AOP 相关的依赖包 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aspects</artifactId>
        </dependency>

        <!-- 方便等会写单元测试 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

    </dependencies>

</project>
```



具体每个依赖的作用，胖友自己认真看下艿艿添加的所有注释噢。

## 2.2 Application

创建 [`Application.java`](https://github.com/YunaiV/SpringBoot-Labs/blob/master/lab-18/lab-18-sharding-datasource-01/src/main/java/cn/iocoder/springboot/lab18/shardingdatasource/Application.java) 类，代码如下：



```
// Application.java

@SpringBootApplication
@MapperScan(basePackages = "cn.iocoder.springboot.lab18.shardingdatasource.mapper")
public class Application {
}
```



- 添加 `@MapperScan` 注解，[`cn.iocoder.springboot.lab18.shardingdatasource.mapper`](https://github.com/YunaiV/SpringBoot-Labs/blob/master/lab-18/lab-18-sharding-datasource-01/src/main/java/cn/iocoder/springboot/lab18/shardingdatasource/mapper/) 包路径下，就是我们 Mapper 接口所在的包路径。

## 2.3 应用配置文件

在 [`resources`](https://github.com/YunaiV/SpringBoot-Labs/tree/master/lab-18/lab-18-sharding-datasource-01/src/main/resources) 目录下，创建 [`application.yaml`](https://github.com/YunaiV/SpringBoot-Labs/blob/master/lab-18/lab-18-sharding-datasource-01/src/main/resources/application.yaml) 配置文件。配置如下：



```java
spring:
  # ShardingSphere 配置项
  shardingsphere:
    datasource:
      # 所有数据源的名字
      names: ds-orders-0, ds-orders-1
      # 订单 orders 数据源配置 00
      ds-orders-0:
        type: com.zaxxer.hikari.HikariDataSource # 使用 Hikari 数据库连接池
        driver-class-name: com.mysql.jdbc.Driver
        jdbc-url: jdbc:mysql://127.0.0.1:3306/lab18_orders_0?useSSL=false&useUnicode=true&characterEncoding=UTF-8
        username: root
        password:
      # 订单 orders 数据源配置 01
      ds-orders-1:
        type: com.zaxxer.hikari.HikariDataSource # 使用 Hikari 数据库连接池
        driver-class-name: com.mysql.jdbc.Driver
        jdbc-url: jdbc:mysql://127.0.0.1:3306/lab18_orders_1?useSSL=false&useUnicode=true&characterEncoding=UTF-8
        username: root
        password:
    # 分片规则
    sharding:
      tables:
        # orders 表配置
        orders:
          actualDataNodes: ds-orders-0.orders_$->{[0,2,4,6]}, ds-orders-1.orders_$->{[1,3,5,7]} # 映射到 ds-orders-0 和 ds-orders-1 数据源的 orders 表们
          key-generator: # 主键生成策略
            column: id
            type: SNOWFLAKE
          database-strategy:
            inline:
              algorithm-expression: ds-orders-$->{user_id % 2}
              sharding-column: user_id
          table-strategy:
            inline:
              algorithm-expression: orders_$->{user_id % 8}
              sharding-column: user_id
        # order_config 表配置
        order_config:
          actualDataNodes: ds-orders-0.order_config # 仅映射到 ds-orders-0 数据源的 order_config 表
    # 拓展属性配置
    props:
      sql:
        show: true # 打印 SQL

# mybatis 配置内容
mybatis:
  config-location: classpath:mybatis-config.xml # 配置 MyBatis 配置文件路径
  mapper-locations: classpath:mapper/*.xml # 配置 Mapper XML 地址
  type-aliases-package: cn.iocoder.springboot.lab18.shardingdatasource.dataobject # 配置数据库实体包路径
```



- 在 `mybatis` 配置项下，设置 `mybatis-spring-boot-starter` 自动化配置 MyBatis 需要的参数。
- 在 `spring.shardingsphere` 配置项下，设置 `sharding-jdbc-spring-boot-starter` 自动化配置 Sharding-JDBC 需要的参数。比较复杂，我们一个一个来看。

`spring.shardingsphere.datasource` 配置项，我们配置了 `ds-orders-0` 和 `ds-orders-1` 两个数据源，分别对应 `lab18_orders_0` 和 `lab18_orders_1` 两个数据库。

`spring.shardingsphere.sharding` 配置项，我们配置了 `orders` 和 `order_config` [逻辑表](https://shardingsphere.apache.org/document/current/cn/features/sharding/concept/sql/) 。

> **逻辑表** ：水平拆分的数据库（表）的相同逻辑和数据结构表的总称。例：订单数据根据主键尾数拆分为 10 张表，分别是 `t_order_0` 到 `t_order_9` ，他们的逻辑表名为 `t_order` 。
>
> **真实表** ：在分片的数据库中真实存在的物理表。即上个示例中的 `t_order_0` 到 `t_order_9` 。
>
> **数据节点** ：数据分片的最小单元。由数据源名称和数据表组成，例：`ds_0.t_order_0` 。



`orders` 配置项，设置 `orders` 逻辑表，使用分库分表的规则

- `actualDataNodes` ：对应的数据节点，使用的是[行表达式](https://shardingsphere.apache.org/document/current/cn/features/sharding/other-features/inline-expression/) 。这里的意思是，`ds-orders-0.orders_0`, `ds-orders-0.orders_2`, `ds-orders-0.orders_4`, `ds-orders-0.orders_6`, `ds-orders-1.orders_1`, `ds-orders-1.orders_3`, `ds-orders-1.orders_5`, `ds-orders-1.orders_7` 。
- `key-generator` ：主键生成策略。这里采用分布式主键 SNOWFLAKE 方案。更多可以看 [《 ShardingSphere > 概念 & 功能 > 数据分片 > 其他功能 > 分布式主键》](https://shardingsphere.apache.org/document/current/cn/features/sharding/other-features/key-generator/) 文档。
- `database-strategy` ：按照 `index = user_id % 2` 分库，路由到 `ds-orders-${index}` 数据源（库）。
- `table-strategy` ：`index = user_id % 8` 分表，路由到 `orders_${index}` 数据表。

`order_config` 配置项，设置 `order_config` 逻辑表，不使用分库分表。

- `actualDataNodes` ：对应的数据节点，只对应数据源（库）为 `ds-orders-0` 的 `order_config` 表。

`spring.shardingsphere.props` 配置项，设置拓展属性配置。

- `sql.show` ：设置打印 SQL 。因为我们编写的 SQL 会被 Sharding-JDBC 进行处理，实际执行的可能不是我们编写的，通过打印，方便我们观察和理解。

​	

## 2.4 MyBatis 配置文件

在 [`resources`](https://github.com/YunaiV/SpringBoot-Labs/tree/master/lab-18/lab-18-sharding-datasource-01/src/main/resources) 目录下，创建 [`mybatis-config.xml`](https://github.com/YunaiV/SpringBoot-Labs/blob/master/lab-18/lab-18-sharding-datasource-01/src/main/resources/mybatis-config.xml) 配置文件。配置如下：



```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

    <settings>
        <!-- 使用驼峰命名法转换字段。 -->
        <setting name="mapUnderscoreToCamelCase" value="true"/>
    </settings>

    <typeAliases>
        <typeAlias alias="Integer" type="java.lang.Integer"/>
        <typeAlias alias="Long" type="java.lang.Long"/>
        <typeAlias alias="HashMap" type="java.util.HashMap"/>
        <typeAlias alias="LinkedHashMap" type="java.util.LinkedHashMap"/>
        <typeAlias alias="ArrayList" type="java.util.ArrayList"/>
        <typeAlias alias="LinkedList" type="java.util.LinkedList"/>
    </typeAliases>

</configuration>
```



因为在数据库中的表的字段，我们是使用下划线风格，而数据库实体的字段使用驼峰风格，所以通过 `mapUnderscoreToCamelCase = true` 来自动转换。

## 2.5 实体类

在 [`cn.iocoder.springboot.lab18.shardingdatasource.dataobject`](https://github.com/YunaiV/SpringBoot-Labs/tree/master/lab-18/lab-18-sharding-datasource-01/src/main/java/cn/iocoder/springboot/lab18/shardingdatasource/dataobject) 包路径下，创建本小节的实体。

### 2.5.1 OrderDO

创建 [`OrderDO.java`](https://github.com/YunaiV/SpringBoot-Labs/blob/master/lab-18/lab-18-sharding-datasource-01/src/main/java/cn/iocoder/springboot/lab18/shardingdatasource/dataobject/OrderDO.java) 类。代码如下：



```
// OrderDO.java

/**
 * 订单 DO
 */
public class OrderDO {

    /**
     * 订单编号
     */
    private Long id;
    /**
     * 用户编号
     */
    private Integer userId;

    // ... 省略 setting/getting 方法
}
```



在 `lab18_orders_0` 数据库下，创建 `orders_0`、`orders_2`、`orders_4`、`orders_6` 数据表。SQL 如下：



```
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for orders_0
-- ----------------------------
DROP TABLE IF EXISTS `orders_0`;
CREATE TABLE `orders_0` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '订单编号',
  `user_id` int(16) DEFAULT NULL COMMENT '用户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='订单表';

-- ----------------------------
-- Table structure for orders_2
-- ----------------------------
DROP TABLE IF EXISTS `orders_2`;
CREATE TABLE `orders_2` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '订单编号',
  `user_id` int(16) DEFAULT NULL COMMENT '用户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='订单表';

-- ----------------------------
-- Table structure for orders_4
-- ----------------------------
DROP TABLE IF EXISTS `orders_4`;
CREATE TABLE `orders_4` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '订单编号',
  `user_id` int(16) DEFAULT NULL COMMENT '用户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='订单表';

-- ----------------------------
-- Table structure for orders_6
-- ----------------------------
DROP TABLE IF EXISTS `orders_6`;
CREATE TABLE `orders_6` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '订单编号',
  `user_id` int(16) DEFAULT NULL COMMENT '用户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='订单表';

SET FOREIGN_KEY_CHECKS = 1;
```



在 `lab18_orders_1` 数据库下，创建 `orders_1`、`orders_3`、`orders_5`、`orders_7` 数据表。SQL 如下：



```
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for orders_1
-- ----------------------------
DROP TABLE IF EXISTS `orders_1`;
CREATE TABLE `orders_1` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '订单编号',
  `user_id` int(16) DEFAULT NULL COMMENT '用户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=400675304294580226 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='订单表';

-- ----------------------------
-- Table structure for orders_3
-- ----------------------------
DROP TABLE IF EXISTS `orders_3`;
CREATE TABLE `orders_3` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '订单编号',
  `user_id` int(16) DEFAULT NULL COMMENT '用户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='订单表';

-- ----------------------------
-- Table structure for orders_5
-- ----------------------------
DROP TABLE IF EXISTS `orders_5`;
CREATE TABLE `orders_5` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '订单编号',
  `user_id` int(16) DEFAULT NULL COMMENT '用户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='订单表';

-- ----------------------------
-- Table structure for orders_7
-- ----------------------------
DROP TABLE IF EXISTS `orders_7`;
CREATE TABLE `orders_7` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '订单编号',
  `user_id` int(16) DEFAULT NULL COMMENT '用户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='订单表';

SET FOREIGN_KEY_CHECKS = 1;
```



### 2.5.2 OrderConfigDO

创建 [`OrderConfigDO.java`](https://github.com/YunaiV/SpringBoot-Labs/blob/master/lab-18/lab-18-sharding-datasource-01/src/main/java/cn/iocoder/springboot/lab18/shardingdatasource/dataobject/OrderConfigDO.java) 类。代码如下：



```
// OrderConfigDO.java

/**
 * 订单配置 DO
 */
public class OrderConfigDO {

    /**
     * 编号
     */
    private Integer id;
    /**
     * 支付超时时间
     *
     * 单位：分钟
     */
    private Integer payTimeout;

    // ... 省略 setting/getting 方法
}
```



在 `lab18_orders_0` 数据库下，创建 `orders_0` 数据表。SQL 如下：



```
-- ----------------------------
-- Table structure for order_config
-- ----------------------------
DROP TABLE IF EXISTS `order_config`;
CREATE TABLE `order_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `pay_timeout` int(11) DEFAULT NULL COMMENT '支付超时时间;单位：分钟',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='订单配置表';
```



## 2.6 Mapper

在 [`cn.iocoder.springboot.lab18.shardingdatasource.mapper`](https://github.com/YunaiV/SpringBoot-Labs/tree/master/lab-18/lab-18-sharding-datasource-01/src/main/java/cn/iocoder/springboot/lab18/shardingdatasource/mapper) 包路径下，创建相应的 Mapper 接口。

### 2.6.1 OrderMapper

创建 [`OrderMapper.java`](https://github.com/YunaiV/SpringBoot-Labs/blob/master/lab-18/lab-18-sharding-datasource-01/src/main/java/cn/iocoder/springboot/lab18/shardingdatasource/mapper/OrderMapper.java) 类。代码如下：



```
// OrderMapper.java

@Repository
public interface OrderMapper {

    OrderDO selectById(@Param("id") Integer id);

    List<OrderDO> selectListByUserId(@Param("userId") Integer userId);

    void insert(OrderDO order);

}
```



在 [`resources/mapper`](https://github.com/YunaiV/SpringBoot-Labs/tree/master/lab-18/lab-18-sharding-datasource-01/src/main/resources/mapper) 路径下，创建 [`OrderMapper.xml`](https://github.com/YunaiV/SpringBoot-Labs/blob/master/lab-18/lab-18-sharding-datasource-01/src/main/resources/mapper/OrderMapper.xml) 接口。代码如下：



```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="cn.iocoder.springboot.lab18.shardingdatasource.mapper.OrderMapper">

    <sql id="FIELDS">
        id, user_id
    </sql>

    <select id="selectById" parameterType="Integer" resultType="OrderDO">
        SELECT
            <include refid="FIELDS" />
        FROM orders
        WHERE id = #{id}
    </select>

    <select id="selectListByUserId" parameterType="Integer" resultType="OrderDO">
        SELECT
            <include refid="FIELDS" />
        FROM orders
        WHERE user_id = #{userId}
    </select>

    <insert id="insert" parameterType="OrderDO" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO orders (
            user_id
        ) VALUES (
            #{userId}
        )
    </insert>

</mapper>
```



### 2.6.2 OrderConfigMapper

创建 [`OrderConfigMapper.java`](https://github.com/YunaiV/SpringBoot-Labs/blob/master/lab-18/lab-18-sharding-datasource-01/src/main/java/cn/iocoder/springboot/lab18/shardingdatasource/mapper/OrderConfigMapper.java) 类。代码如下：



```
// OrderConfigMapper.java

@Repository
public interface OrderConfigMapper {

    OrderConfigDO selectById(@Param("id") Integer id);

}
```



在 [`resources/mapper`](https://github.com/YunaiV/SpringBoot-Labs/tree/master/lab-18/lab-18-sharding-datasource-01/src/main/resources/mapper) 路径下，创建 [`OrderConfigMapper.xml`](https://github.com/YunaiV/SpringBoot-Labs/blob/master/lab-18/lab-18-sharding-datasource-01/src/main/resources/mapper/OrderConfigMapper.xml) 接口。代码如下：



```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="cn.iocoder.springboot.lab18.shardingdatasource.mapper.OrderConfigMapper">

    <sql id="FIELDS">
        id, pay_timeout
    </sql>

    <select id="selectById" parameterType="Integer" resultType="OrderConfigDO">
        SELECT
            <include refid="FIELDS" />
        FROM order_config
        WHERE id = #{id}
    </select>

</mapper>
```



## 2.7 简单测试

### 2.7.1 OrderConfigMapperTest

创建 [OrderConfigMapperTest](https://github.com/YunaiV/SpringBoot-Labs/blob/master/lab-18/lab-18-sharding-datasource-01/src/test/java/cn/iocoder/springboot/lab18/shardingdatasource/mapper/OrderMapperTest.java) 测试类，我们来测试一下简单的 OrderConfigMapper 的每个操作。代码如下：



```
// OrderConfigMapperTest.java

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class OrderConfigMapperTest {

    @Autowired
    private OrderConfigMapper orderConfigMapper;

    @Test
    public void testSelectById() {
        OrderConfigDO orderConfig = orderConfigMapper.selectById(1);
        System.out.println(orderConfig);
    }

}
```



**`#testSelectByI()` 测试方法**

执行日志如下：



```
// Logic SQL
2019-11-11 20:21:48.845  INFO 32393 --- [           main] ShardingSphere-SQL                       : Logic SQL: SELECT

        id, pay_timeout

        FROM order_config
        WHERE id = ?

// Actual SQL
2019-11-11 20:21:48.845  INFO 32393 --- [           main] ShardingSphere-SQL                       : Actual SQL: ds-orders-0 ::: SELECT

        id, pay_timeout

        FROM order_config
        WHERE id = ? ::: [1]
```



- Logic SQL ：逻辑 SQL 日志，就是我们编写的。
- Actual SQL ：物理 SQL 日志，实际 Sharding-JDBC 向数据库真正发起的日志。
  - 在这里，我们可以看到 `ds-orders-0` ，表名该物理 SQL ，是路由到 `ds-orders-0` 数据源执行。
  - 同时，查询的是 `order_config` 表。
  - 符合我们配置的 `order_config` 逻辑表，不使用分库分表，对应的数据节点仅有 `ds-orders-0.order_config` 。

### 2.7.2 OrderMapperTest

创建 [OrderMapperTest](https://github.com/YunaiV/SpringBoot-Labs/blob/master/lab-18/lab-18-sharding-datasource-01/src/test/java/cn/iocoder/springboot/lab18/shardingdatasource/mapper/OrderMapperTest.java) 测试类，我们来测试一下简单的 OrderMapper 的每个操作。代码如下：



```
// OrderMapperTest.java

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;

    @Test
    public void testSelectById() {
        OrderDO order = orderMapper.selectById(1);
        System.out.println(order);
    }

    @Test
    public void testSelectListByUserId() {
        List<OrderDO> orders = orderMapper.selectListByUserId(1);
        System.out.println(orders.size());
    }

    @Test
    public void testInsert() {
        OrderDO order = new OrderDO();
        order.setUserId(1);
        orderMapper.insert(order);
    }

}
```



**① `#testSelectByI()` 测试方法**

执行日志如下：



```
// Logic SQL
2019-11-11 21:41:15.053  INFO 33184 --- [           main] ShardingSphere-SQL                       : Logic SQL: SELECT

        id, user_id

        FROM orders
        WHERE id = ?

// Actual SQL
2019-11-11 21:41:15.054  INFO 33184 --- [           main] ShardingSphere-SQL                       : Actual SQL: ds-orders-0 ::: SELECT

        id, user_id

        FROM orders_0
        WHERE id = ? ::: [1]
2019-11-11 21:41:15.054  INFO 33184 --- [           main] ShardingSphere-SQL                       : Actual SQL: ds-orders-0 ::: SELECT

        id, user_id

        FROM orders_2
        WHERE id = ? ::: [1]
2019-11-11 21:41:15.054  INFO 33184 --- [           main] ShardingSphere-SQL                       : Actual SQL: ds-orders-0 ::: SELECT

        id, user_id

        FROM orders_4
        WHERE id = ? ::: [1]
2019-11-11 21:41:15.054  INFO 33184 --- [           main] ShardingSphere-SQL                       : Actual SQL: ds-orders-0 ::: SELECT

        id, user_id

        FROM orders_6
        WHERE id = ? ::: [1]
2019-11-11 21:41:15.054  INFO 33184 --- [           main] ShardingSphere-SQL                       : Actual SQL: ds-orders-1 ::: SELECT

        id, user_id

        FROM orders_1
        WHERE id = ? ::: [1]
2019-11-11 21:41:15.054  INFO 33184 --- [           main] ShardingSphere-SQL                       : Actual SQL: ds-orders-1 ::: SELECT

        id, user_id

        FROM orders_3
        WHERE id = ? ::: [1]
2019-11-11 21:41:15.054  INFO 33184 --- [           main] ShardingSphere-SQL                       : Actual SQL: ds-orders-1 ::: SELECT

        id, user_id

        FROM orders_5
        WHERE id = ? ::: [1]
2019-11-11 21:41:15.054  INFO 33184 --- [           main] ShardingSphere-SQL                       : Actual SQL: ds-orders-1 ::: SELECT

        id, user_id

        FROM orders_7
        WHERE id = ? ::: [1]
```



- 明明只有一条 Logic SQL 操作，却发起了 8 条 Actual SQL 操作。这是为什么呢？

- 我们使用 `id = ?` 作为查询条件，因为 Sharding-JDBC 解析不到我们配置的 `user_id` 片键（分库分表字段），作为查询字段，所以只好 [全库表路由](https://shardingsphere.apache.org/document/current/cn/features/sharding/principle/route/#全库表路由) ，查询所有对应的数据节点，也就是配置的所有数据库的数据表。这样，在获得所有查询结果后，通过 [归并引擎](https://shardingsphere.apache.org/document/current/cn/features/sharding/principle/merge/) 合并返回最终结果。

  > 通过将 Actual SQL 在每个数据库的数据表执行，返回的结果都是符合条件的。
  >
  > 这样，和使用 Logic SQL 在逻辑表中执行的结果，实际是一致的。
  >
  > 胖友可以试着想一想噢。如果还是有疑惑，可以给艿艿留言。

- 那么，一次性发起这么多条 Actual SQL 是不是会顺序执行，导致很慢呢？实际上，Sharding-JDBC 有 [执行引擎](https://shardingsphere.apache.org/document/current/cn/features/sharding/principle/execute/) ，会并行执行这多条 Actual SQL 操作。所以呢，最终操作时长，由最慢的 Actual SQL 所决定。

- 虽然说，[执行引擎](https://shardingsphere.apache.org/document/current/cn/features/sharding/principle/execute/) 提供了并行执行 Actual SQL 操作的能力，我们还是推荐尽可能查询的时候，带有片键（分库分表字段）。对 Sharding-JDBC 性能感兴趣的胖友，可以看看 [《Sharding-JDBC 性能测试报告》](https://shardingsphere.apache.org/document/current/cn/manual/sharding-jdbc/stress-test/) 。

**② `#testSelectListByUserId()` 测试方法**

执行日志如下：



```
// Logic SQL
2019-11-11 22:00:16.640  INFO 33407 --- [           main] ShardingSphere-SQL                       : Logic SQL: SELECT

        id, user_id

        FROM orders
        WHERE user_id = ?

// Actual SQL
2019-11-11 22:00:16.640  INFO 33407 --- [           main] ShardingSphere-SQL                       : Actual SQL: ds-orders-1 ::: SELECT

        id, user_id

        FROM orders_1
        WHERE user_id = ? ::: [1]
```

- 一条 Logic SQL 操作，发起了 1 条 Actual SQL 操作。这是为什么呢？
- 我们使用 `user_id = ?` 作为查询条件，因为 Sharding-JDBC 解析到我们配置的 `user_id` 片键（分库分表字段），作为查询字段，所以可以 [标准路由](https://shardingsphere.apache.org/document/current/cn/features/sharding/principle/route/) ，仅查询**一个**数据节点。这种，是 Sharding-JDBC 最为推荐使用的分片方式。
  - 分库：`user_id % 2 = 1 % 2 = 1` ，所以路由到 `ds-orders-1` 数据源。
  - 分表：`user_id % 8 = 1 % 8 = 1` ，所以路由到 `orders_1` 数据表。
  - 两者一结合，只查询 `ds-orders-1.orders_1` 数据节点。

**② `#testInsert()` 测试方法**

执行日志如下：



```
// Logic SQL
2019-11-11 22:05:52.203  INFO 33510 --- [           main] ShardingSphere-SQL                       : Logic SQL: INSERT INTO orders (
            user_id
        ) VALUES (
            ?
        )

// Actual SQL
2019-11-11 22:05:52.203  INFO 33510 --- [           main] ShardingSphere-SQL                       : Actual SQL: ds-orders-1 ::: INSERT INTO orders_1 (
            user_id
        , id) VALUES (?, ?) ::: [1, 400772257330233345]
```



- 不考虑 [广播表](https://shardingsphere.apache.org/document/current/cn/features/sharding/concept/sql/) 的情况下，插入语句必须带有片键（分库分表字段），否则 [执行引擎](https://shardingsphere.apache.org/document/current/cn/features/sharding/principle/execute/) 不知道插入到哪个数据库的哪个数据表中。毕竟，插入操作必然是单库单表。
- 我们会发现，Actual SQL 相比 Logic SQL 来说，增加了主键 `id` 为 `400772257330233345` 。这是为什么呢？我们配置 `orders` 逻辑表，使用 SNOWFLAKE 算法生成分布式主键，而 [改写引擎](https://shardingsphere.apache.org/document/current/cn/features/sharding/principle/rewrite/) 在发现我们的 Logic SQL 并未设置插入的 `id` 主键编号，它会自动生成主键，改写 Logic SQL ，附加 `id` 成 Logic SQL 。

至此，我们已经完成了一个 Sharding-JDBC 的简单的分库分表的示例。艿艿建议的话，如果准备应用到项目之前，通读 [《ShardingSphere 文档》](https://shardingsphere.apache.org/document/current/cn/overview/) 。学习不全面，线上两行泪。

# 3. 读写分离

在 [《芋道 Spring Boot 多数据源（读写分离）入门》](http://www.iocoder.cn/Spring-Boot/dynamic-datasource/?self) 的 [「9. Sharding-JDBC 读写分离」](https://www.iocoder.cn/Spring-Boot/sharding-datasource/?vip#) 小节中，我们已经提供了使用 Sharding-JDBC 实现读写分离的入门示例。

本小节，我们会使用 [MyBatis-Plus](https://mp.baomidou.com/) 替换掉原生 MyBatis ，进一步简化该示例。

- 当然，即使你没看过上述示例，也不影响本小节的阅读与入门。
- 可能胖友没有使用过 MyBatis-Plus ，也请放心，一样不会有影响。

## 3.1 引入依赖

> 示例代码对应仓库：[lab-18-sharding-datasource-02](https://github.com/YunaiV/SpringBoot-Labs/tree/master/lab-18/lab-18-sharding-datasource-02) 。

在 [`pom.xml`](https://github.com/YunaiV/SpringBoot-Labs/blob/master/lab-18/lab-18-sharding-datasource-02/pom.xml) 文件中，引入相关依赖。



```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.3.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>lab-18-sharding-datasource-02</artifactId>

    <dependencies>
        <!-- 实现对数据库连接池的自动化配置 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
        <dependency> <!-- 本示例，我们使用 MySQL -->
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.48</version>
        </dependency>

        <!-- 实现对 Sharding-JDBC 的自动化配置 -->
        <dependency>
            <groupId>org.apache.shardingsphere</groupId>
            <artifactId>sharding-jdbc-spring-boot-starter</artifactId>
            <version>4.0.0-RC2</version>
        </dependency>

        <!-- 实现对 MyBatis Plus 的自动化配置 -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.2.0</version>
        </dependency>

        <!-- 方便等会写单元测试 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

    </dependencies>

</project>
```



具体每个依赖的作用，胖友自己认真看下艿艿添加的所有注释噢。

## 3.2 Application

创建 [`Application.java`](https://github.com/YunaiV/SpringBoot-Labs/blob/master/lab-18/lab-18-sharding-datasource-02/src/main/java/cn/iocoder/springboot/lab18/shardingdatasource/Application.java) 类，配置 `@MapperScan` 注解，扫描对应 Mapper 接口所在的包路径。代码如下：



```
// Application.java

@SpringBootApplication
@MapperScan(basePackages = "cn.iocoder.springboot.lab18.shardingdatasource.mapper")
public class Application {
}
```



- [`cn.iocoder.springboot.lab18.shardingdatasource.mapper`](https://github.com/YunaiV/SpringBoot-Labs/tree/master/lab-18/lab-18-sharding-datasource-02/src/main/java/cn/iocoder/springboot/lab18/shardingdatasource/mapper) 包路径下，就是我们 Mapper 接口所在的包路径。

## 2.3 应用配置文件

在 [`resources`](https://github.com/YunaiV/SpringBoot-Labs/tree/master/lab-18/lab-18-sharding-datasource-02/src/main/resources) 目录下，创建 [`application.yaml`](https://github.com/YunaiV/SpringBoot-Labs/blob/master/lab-18/lab-18-sharding-datasource-02/src/main/resources/application.yaml) 配置文件。配置如下：



```
spring:
  # ShardingSphere 配置项
  shardingsphere:
    # 数据源配置
    datasource:
      # 所有数据源的名字
      names: ds-master, ds-slave-1, ds-slave-2
      # 订单 orders 主库的数据源配置
      ds-master:
        type: com.zaxxer.hikari.HikariDataSource # 使用 Hikari 数据库连接池
        driver-class-name: com.mysql.jdbc.Driver
        jdbc-url: jdbc:mysql://127.0.0.1:3306/test_orders?useSSL=false&useUnicode=true&characterEncoding=UTF-8
        username: root
        password:
      # 订单 orders 从库数据源配置
      ds-slave-1:
        type: com.zaxxer.hikari.HikariDataSource # 使用 Hikari 数据库连接池
        driver-class-name: com.mysql.jdbc.Driver
        jdbc-url: jdbc:mysql://127.0.0.1:3306/test_orders_01?useSSL=false&useUnicode=true&characterEncoding=UTF-8
        username: root
        password:
      # 订单 orders 从库数据源配置
      ds-slave-2:
        type: com.zaxxer.hikari.HikariDataSource # 使用 Hikari 数据库连接池
        driver-class-name: com.mysql.jdbc.Driver
        jdbc-url: jdbc:mysql://127.0.0.1:3306/test_orders_02?useSSL=false&useUnicode=true&characterEncoding=UTF-8
        username: root
        password:
    # 读写分离配置，对应 YamlMasterSlaveRuleConfiguration 配置类
    masterslave:
      name: ms # 名字，任意，需要保证唯一
      master-data-source-name: ds-master # 主库数据源
      slave-data-source-names: ds-slave-1, ds-slave-2 # 从库数据源
    # 拓展属性配置
    props:
      sql:
        show: true # 打印 SQL

# mybatis-plus 配置内容
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true # 虽然默认为 true ，但是还是显示去指定下。
  global-config:
    db-config:
      id-type: none # 虽然 MyBatis Plus 也提供 ID 生成策略，但是还是使用 Sharding-JDBC 的
      logic-delete-value: 1 # 逻辑已删除值(默认为 1)
      logic-not-delete-value: 0 # 逻辑未删除值(默认为 0)
  mapper-locations: classpath*:mapper/*.xml
  type-aliases-package: cn.iocoder.springboot.lab18.shardingdatasource.dataobject
```



**Sharding-JDBC 配置项**

- `spring.shardingsphere.datasource` 配置项下，我们配置了 一个主数据源 `ds-master` 、两个从数据源 `ds-slave-1`、`ds-slave-2` 。
- `spring.shardingsphere.masterslave` 配置项下，配置了读写分离。对于从库来说，Sharding-JDBC 提供了多种负载均衡策略，默认为轮询。
- 因为艿艿本地并未搭建 MySQL 一主多从的环境，所以是通过创建了 `test_orders_01`、`test_orders_02` 库，手动模拟作为 `test_orders` 的从库。

**MyBatis-Plus 配合项**

- `mybatis-plus` 增加了更多配置项，也因此我们无需在配置 [`mybatis-config.xml`](https://github.com/YunaiV/SpringBoot-Labs/blob/master/lab-12-mybatis/lab-12-mybatis-xml/src/main/resources/mybatis-config.xml) 配置文件。
- 更多的 MyBatis-Plus 配置项，可以看看 [MyBatis-Plus 使用配置](https://mybatis.plus/config/#mapperlocations) 。

## 2.4 OrderDO

在 [`cn.iocoder.springboot.lab18.shardingdatasource.dataobject`](https://github.com/YunaiV/SpringBoot-Labs/tree/master/lab-18/lab-18-sharding-datasource-02/src/main/java/cn/iocoder/springboot/lab18/shardingdatasource/dataobject) 包路径下，创建 [OrderDO.java](https://github.com/YunaiV/SpringBoot-Labs/blob/master/lab-18/lab-18-sharding-datasource-02/src/main/java/cn/iocoder/springboot/lab18/shardingdatasource/dataobject/OrderDO.java) 类，订单 DO 。代码如下：



```
// OrderDO.java

@TableName(value = "orders")
public class OrderDO {

    /**
     * 订单编号
     */
    private Long id;
    /**
     * 用户编号
     */
    private Integer userId;

    // ... 省略 setting/getting 方法

}
```



- 增加了 [`@TableName`](https://mybatis.plus/guide/annotation.html#tablename) 注解，设置了 OrderDO 对应的表名是 `orders` 。毕竟，我们要使用 MyBatis-Plus 给咱自动生成 CRUD 操作。

对应的创建表的 SQL 如下：



```
-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单编号',
  `user_id` int(16) DEFAULT NULL COMMENT '用户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='订单表';
```



## 2.5 OrderMapper

在 [`cn.iocoder.springboot.lab18.shardingdatasource.mapper`](https://github.com/YunaiV/SpringBoot-Labs/tree/master/lab-18/lab-18-sharding-datasource-02/src/main/java/cn/iocoder/springboot/lab18/shardingdatasource/mapper) 包路径下，创建 [OrderMapper](https://github.com/YunaiV/SpringBoot-Labs/blob/master/lab-18/lab-18-sharding-datasource-02/src/main/java/cn/iocoder/springboot/lab18/shardingdatasource/mapper/OrderMapper.java) 接口。代码如下：



```
// OrderMapper.java

@Repository
public interface OrderMapper extends BaseMapper<OrderDO> {

}
```



- 继承了 `com.baomidou.mybatisplus.core.mapper.BaseMapper<T>` 接口，这样常规的 CRUD 操作，MyBatis-Plus 就可以替我们自动生成。一般来说，开发 CRUD 业务的时候，最枯燥的就是要写 CRUD 的常用 SQL ，完全跟不上艿艿的思绪哈。

## 2.6 简单测试

创建 [OrderMapperTest](https://github.com/YunaiV/SpringBoot-Labs/blob/master/lab-18/lab-18-sharding-datasource-02/src/test/java/cn/iocoder/springboot/lab18/shardingdatasource/mapper/OrderMapperTest.java) 测试类，我们来测试一下简单的 OrderMapper 的读写操作。代码如下：



```
// OrderMapperTest.java

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;

    @Test
    public void testSelectById() { // 测试从库的负载均衡
        for (int i = 0; i < 2; i++) {
            OrderDO order = orderMapper.selectById(1);
            System.out.println(order);
        }
    }

    @Test
    public void testSelectById02() { // 测试强制访问主库
        try (HintManager hintManager = HintManager.getInstance()) {
            // 设置强制访问主库
            hintManager.setMasterRouteOnly();
            // 执行查询
            OrderDO order = orderMapper.selectById(1);
            System.out.println(order);
        }
    }

    @Test
    public void testInsert() { // 插入
        OrderDO order = new OrderDO();
        order.setUserId(10);
        orderMapper.insert(order);
    }

}
```



**① `#testSelectById()` 测试方法**

执行日志如下：



```
// 第 1 次查询
2019-11-11 23:49:27.414  INFO 35306 --- [           main] ShardingSphere-SQL                       : Rule Type: master-slave
2019-11-11 23:49:27.414  INFO 35306 --- [           main] ShardingSphere-SQL                       : SQL: SELECT id,user_id FROM orders WHERE id=?  ::: DataSources: ds-slave-1

// 第 2 次查询
2019-11-11 23:49:27.454  INFO 35306 --- [           main] ShardingSphere-SQL                       : Rule Type: master-slave
2019-11-11 23:49:27.454  INFO 35306 --- [           main] ShardingSphere-SQL                       : SQL: SELECT id,user_id FROM orders WHERE id=?  ::: DataSources: ds-slave-2
```



- 默认情况下，Sharding-JDBC 使用 [读写分离](https://shardingsphere.apache.org/document/current/cn/features/read-write-split/) 功能时，读取从库。
- 并且，支持从库的负载均衡，默认采用**轮询**的算法。所以，我们可以看到第 1 次查询 `ds-slave-1` 数据源，第 2 次查询 `ds-slave-2` 数据源。

**② `#testSelectById02()` 测试方法**

执行日志如下：



```
2019-11-11 23:56:09.669  INFO 35430 --- [           main] ShardingSphere-SQL                       : Rule Type: master-slave
2019-11-11 23:56:09.669  INFO 35430 --- [           main] ShardingSphere-SQL                       : SQL: SELECT id,user_id FROM orders WHERE id=?  ::: DataSources: ds-master
```



- 测试强制访问主库。在一些业务场景下，对数据延迟敏感，所以只能强制读取主库。此时，可以使用

   

  HintManager

   

  强制访问主库。

  - 不过要注意，在使用完后，需要去清理下 HintManager （HintManager 是基于线程变量，透传给 Sharding-JDBC 的内部实现），避免污染下次请求，一直强制访问主库。
  - Sharding-JDBC 比较贴心，HintManager 实现了 AutoCloseable 接口，可以通过 [Try-with-resources](https://www.cnblogs.com/barrywxx/p/9993005.html) 机制，自动关闭。

**③ `#testInsert()` 测试方法**



```
2019-11-11 23:57:27.046  INFO 35469 --- [           main] ShardingSphere-SQL                       : Rule Type: master-slave
2019-11-11 23:57:27.047  INFO 35469 --- [           main] ShardingSphere-SQL                       : SQL: INSERT INTO orders  ( id,
user_id )  VALUES  ( ?,
? ) ::: DataSources: ds-master
```



- 写入操作时，直接访问主库 `ds-master` 数据源。

## 2.7 详细测试

在 [`cn.iocoder.springboot.lab18.shardingdatasource.service`](https://github.com/YunaiV/SpringBoot-Labs/tree/master/lab-18/lab-18-sharding-datasource-02/src/main/java/cn/iocoder/springboot/lab18/shardingdatasource/service) 包路径下，创建 [`OrderService.java`](https://github.com/YunaiV/SpringBoot-Labs/blob/master/lab-18/lab-18-sharding-datasource-02/src/main/java/cn/iocoder/springboot/lab18/shardingdatasource/service/OrderService.java) 类。代码如下：



```
// OrderService.java

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Transactional
    public void add(OrderDO order) {
        // <1.1> 这里先假模假样的读取一下。读取从库
        OrderDO exists = orderMapper.selectById(1);
        System.out.println(exists);

        // <1.2> 插入订单
        orderMapper.insert(order);

        // <1.3> 这里先假模假样的读取一下。读取主库
        exists = orderMapper.selectById(1);
        System.out.println(exists);
    }

    public OrderDO findById(Integer id) {
        return orderMapper.selectById(id);
    }

}
```



- 我们创建了 [OrderServiceTest](https://github.com/YunaiV/SpringBoot-Labs/blob/master/lab-18/lab-18-sharding-datasource-02/src/test/java/cn/iocoder/springboot/lab18/shardingdatasource/service/OrderServiceTest.java) 测试类，可以测试上面编写的两个方法。

- 在

   

  ```
  #add(OrderDO order)
  ```

   

  方法中，开启事务，插入一条订单记录。

  - `<1.1>` 处，往**从库**发起一次订单查询。在 Sharding-JDBC 的读写分离策略里，默认读取从库。
  - `<1.2>` 处，往**主库**发起一次订单写入。写入，肯定是操作主库的。
  - `<1.3>` 处，往**主库**发起一次订单查询。在 Sharding-JDBC 中，读写分离约定：**同一线程且同一数据库连接内，如有写入操作，以后的读操作均从主库读取，用于保证数据一致性。**

- 在 `#findById(Integer id)` 方法，往**从库**发起一次订单查询。

------

实际场景下，我们会是**分库分表 + 读写分离**共同使用，所以胖友可以参考 [《ShardingSphere > 用户手册 > Sharding-JDBC > 配置手册》](https://shardingsphere.apache.org/document/current/cn/manual/sharding-jdbc/configuration/) 文档，尝试自己实现一个这样的示例。

因为文档提供的是 Properties 的格式，如果胖友想转换成 YAML 格式，可以使用 [ToYaml.com](https://www.toyaml.com/index.html) 工具。

# 666. 彩蛋

在 Apache ShardingSphere 中，目前提供了 Sharding-JDBC 和 Sharding-Proxy 两种方式，未来会有 Sharding-Sidecar 方式。那么，怎么做选择呢？

在 [《Apache ShardingSphere 官方文档 —— 概览》](https://shardingsphere.apache.org/document/current/cn/overview/) 中，其实已经给出了答案。

> Sharding-JDBC 采用无中心化架构，适用于 Java 开发的高性能的轻量级 OLTP 应用。
>
> Sharding-Proxy 提供静态入口以及异构语言的支持，适用于 OLAP 应用以及对分片数据库进行管理和运维的场景。

Sharding-JDBC ，相比 Sharding-Proxy 来说，是基于 client 模式，无需经过 proxy 一层的性能损耗，也不用考虑 proxy 的高可用，所以对于 Java 项目来说，更加被推荐。目前，阿里、京东、美团等公司，都采用 client 模式的分库分表中间件。

当然，Sharding-Proxy 也是有其使用的场景。我们可以搭建一个 Sharding-Proxy 服务，然后使用 Navicat 等 MySQL GUI 工具连接该服务，方便查询数据。

分库分表数据迁移实战:
https://mp.weixin.qq.com/s/ZewY9KyVBsWqB1Fu2hNbSg
https://mp.weixin.qq.com/s/wpzfIxixiAmDXEYuxLoZGQ