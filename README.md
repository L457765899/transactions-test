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

数据库</br>
demo1-a  只有表t1</br>
demo1-b  只有表t2</br>
demo2-a  只有表t1</br>
demo2-b  只有表t2</br>
demo3-a  只有表t1</br>

项目</br>
demo1  多数据源+ActiveMQ</br>
demo2  多数据源</br>
demo3  单数据源</br>
demo4  单数据源+ActiveMQ</br>
demo5  多数据源+RocketMq</br>
demo6  暂时没东西</br>

事物的使用</br>
多数据源项目：只需要在事物发起者和事物参与者的方法上加@Transactional注解</br>
单数据源项目：只需要在事物发起者的方法上加@Transactional、@XA注解，事物参与者的方法上加@Transactional注解</br>

<font color=red>注意：单数据源项目的事物发起者比多数据源的事物发起者多了一个@XA注解，原因是单数据源默认使用的是1段提交，
加@XA注解的目的是让单数据源的项目使用2段提交，事物参与者单数据源项目和多数据源项目一样加一个@Transactional注解就够了</font>

测试目的</br>
demo1+demo2            主要测试：多数据源项目和多数据源项目相互调用是否正常</br>
demo1+demo2+demo3      主要测试：多数据源项目和单数据源项目相互调用是否正常</br>
demo4+demo1            主要测试：加入ActiveMQ后相互之间调用是否正常</br>
demo5+demo2            主要测试：加入RocketMq后相互之间调用是否正常</br>




