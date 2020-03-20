# openldap

OpenLDAP是轻型目录访问协议（Lightweight Directory Access Protocol，LDAP）的自由和开源的实现，在其OpenLDAP许可证下发行，并已经被包含在众多流行的Linux发行版中。

## 安装

可以参考[docker下openldap与phpLDAPadmin安装](../Docker/README.md#openldap与phpLDAPadmin安装)

## 基本概念

关键字 |名称| 说明
--|--|---
dn | Distinguished Name | 唯一标识,类比mysql的主键id
dc | Domain Component | 域名,类比命名空间，一个用户可以存在在多个dc中
c  | Country | 国家
o | organization | 组织/公司
ou | Organization Unit | 组织单元,所属组织
sn | Suer Name | 真实名称
cn | Common Name | 常用名,类比用户的呢称（全名）
uid | User Id | 用户ID,登录使用的名称

LDIF格式文件: openldap的数据描述格式，类比linux的/etc/passwd文件格式，使用固定的格式来描述包含的数据

```ldif
# sample.ldif 是用的是默认配置所以dc=example,dc=org不需要改变
dn:uid=1,ou=firstunit,dc=example,dc=org
objectclass:top
objectclass:person
objectclass:uidObject
objectclass:simpleSecurityObject
userPassword:123456
cn:第一个用户
sn:su
uid:1
telephoneNumber：13288888888
```
*当phpLDAPadmin启动后，可以通过import导入ldif格式文件*

很多objectClass都会提供额外的字段,比如上面的telephoneNumber字段就是person这个objectClass提供的。([objectClass列表参考](https://www.zytrax.com/books/ldap/ape/#objectclasses ))

Name | 可选参数
---|---
person | userPassword $ telephoneNumber $ seeAlso $ description 


https://www.lagou.com/lgeduarticle/58051.html