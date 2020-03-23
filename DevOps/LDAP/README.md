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

很多objectClass都会提供额外的字段,比如上面的telephoneNumber字段就是person这个objectClass提供的。([objectClass列表参考](https://www.zytrax.com/books/ldap/ape/#objectclasses ))上面示例的person可选参数如下。

Name | 可选参数
---|---
person | userPassword $ telephoneNumber $ seeAlso $ description 

ldap的数据组织关系，一般都是由大到小的树形结构。比如互联网组织大概会是：根节点为国家，国家下为域名，域名下为组织/组织单元，再往下为用户。企业是：根节点为域名，域名下面为部门，部门下面为用户。

定义一个用户或数据实际就是给分配objectClass，并给objectClass提供的字段赋值即可。比如我们现在定义一个有密码的用户，可以通过分配simpleSecureityObject，然后给userPassord赋值就可以

```
dn: userid=dyp,dc=example,dc=org
objectclass: simpleSecurityObject
userid: dyp
userPassword:123456
```

登录的时候就可以使用`userid=dyp,dc=example,dc=org`做为用户名登录

## 相关命令

当然openldap实现提供了一堆命令来做数据操作。先列出来看看有哪些

**ldap开头的**

ldapadd      ldapdelete   ldapmodify   ldappasswd   ldapurl      
ldapcompare  ldapexop     ldapmodrdn   ldapsearch   ldapwhoami   

**slap开头的**

slapacl     slapauth    slapd       slapindex   slapschema  
slapadd     slapcat     slapdn      slappasswd  slaptest 


## Java连接LDAP

实际上Java直接就支持连接，只需要提供管理员的账户密码，知道ldap所在的机器ip和port即可。如果按照docker的默认安装管理员账号是`cn=admin,dc=example,dc=org`密码是`admin`,查询的遍历起点BASEDN是`dc=example,dc=org`

以下是常用的类：

```java
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
```

简单的Ldap数据连接、写入、查询见示例[代码](source/firstldap)