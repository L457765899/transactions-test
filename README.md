# transactions-test
transactions-dubbo项目的使用demo



表

```sql
CREATE TABLE `t1` (
  `id`  int NOT NULL AUTO_INCREMENT ,
  `name`  varchar(64) NULL ,
  `time`  bigint NOT NULL ,
  PRIMARY KEY (`id`)
);

CREATE TABLE `t2` (
	`id`  int NOT NULL AUTO_INCREMENT ,
	`name`  varchar(64) NULL ,
	`time`  bigint NOT NULL ,
	PRIMARY KEY (`id`)
);
```



数据库
demo1-a  只有表t1
demo1-b  只有表t2
demo2-a  只有表t1
demo2-b  只有表t2
demo3-a  只有表t1

项目
demo1  多数据源+ActiveMQ
demo2  多数据源
demo3  单数据源
demo4  单数据源+ActiveMQ
demo5  多数据源+RocketMq
demo6  暂时没东西

测试目的
demo1+demo2                     主要测试：多数据源项目和多数据源项目相互调用是否正常
demo1+demo2+demo3      主要测试：多数据源项目和单数据源项目相互调用是否正常
demo4+demo1                     主要测试：加入ActiveMQ后相互之间调用是否正常
demo5+demo2                     主要测试：加入RocketMq后相互之间调用是否正常