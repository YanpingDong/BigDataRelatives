# openldap

OpenLDAP是轻型目录访问协议（Lightweight Directory Access Protocol，LDAP）的自由和开源的实现，在其OpenLDAP许可证下发行，并已经被包含在众多流行的Linux发行版中。

## 安装

可以参考[docker下openldap与phpLDAPadmin安装](../Docker/README.md#openldap与phpLDAPadmin安装)

## 基本概念

关键字 |名称| 说明
--|--|---
dn | Distinguished Name | 唯一标识,类比mysql的主键id
dc | Domain Component | 域名,其格式是将完整的域名分成几部分，如域名为example.com变成dc=example,dc=com（一条记录的所属位置）
c  | Country | 国家
o | organization | 组织/公司
ou | Organization Unit | 组织单元,所属组织，一个用户也可有多个ou
sn | Suer Name | 真实名称
cn | Common Name | 常用名,类比用户的呢称（全名）
uid | User Id | 用户ID,登录使用的名称

假设你要树上的一个苹果（一条记录），你怎么告诉园丁它的位置呢？当然首先要说明是哪一棵树（dc，相当于MYSQL的DB），然后是从树根到那个苹果所经过的所有“分叉”（ou），最后就是这个苹果的名字（uid，相当于MySQL表主键id）。

LDIF格式文件: openldap的数据描述格式，类比linux的/etc/passwd文件格式，使用固定的格式来描述包含的数据。

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

定义一个用户或数据实际就是给分配objectClass，并给objectClass提供的字段赋值即可。比如我们现在定义一个有密码的用户，可以通过分配simpleSecureityObject，然后给userPassord赋值就可以。

也就是说在LDAP目录数据库中，所有的条目都必须定义objectClass这个属性。这有点像Java语言里说阐述的“一切皆对象”的理念，每个条目（LDAP Entry）都要定义自己的Object Classes。Object Class可以看作是LDAP Entry的模板，它定义了条目的属性集，包括必有属性（requited attribute）和可选属性（option attribute）。要着重指出的是，在LDAP的Entry中是不能像关系数据库的表那样随意添加属性字段的，一个Entry的属性是由它所继承的所有Object Classes的属性集合决定的，此外可以包括LDAP中规定的“操作属性”（操作属性是一种独立于Object Class而存在的属性，它可以赋给目录中的任意条目）。如果你想添加的属性不在Object Classes定义属性的范畴，也不是LDAP规定的操作属性，那么是不能直接绑定（在LDAP中，给Entry赋予属性的过程称为绑定）到条目上的，你必须自定义一个含有你需要的属性的Object Class，而后将此类型赋给条目。

Object Class是可以被继承的，这使它看上去真的很像Java语言中的POJO对象。继承类的对象实例也必须实现父 类规定的必有属性（requited attribute），同时拥有父类规定的可选属性（option attribute）。继承类可以扩展父类的必有属性和可选属性。比如人员（person）含有姓（sn）、名（cn）、电话(telephoneNumber)、密码(userPassword)等属性，单位职工(organizationalPerson)是人员(person)的继承类，除了上述属性之外还含有职务（title）、邮政编码（postalCode）、通信地址(postalAddress)等属性。

通过对象类可以方便的定义条目类型。每个条目可以直接继承多个对象类，这样就继承了各种属性。如果2个对象类中有相同的属性，则条目继承后只会保留1个属性。下面是一个简单用户的定义示例，继承了top和simpleSecurityObject两个对象：

```
dn: userid=dyp,dc=example,dc=org
objectclass： top
objectclass: simpleSecurityObject
userid: dyp
userPassword:123456
```
*登录的时候就可以使用`userid=dyp,dc=example,dc=org`做为用户名登录*

对象类有三种类型：结构类型（Structural）、抽象类型(Abstract)和辅助类型（Auxiliary）。结构类型是最基本的类型，它规定了对象实体的基本属性，每个条目属于且仅属于一个结构型对象类。抽象类型可以是结构类型或其他抽象类型父类，它将对象属性中共性的部分组织在一起，称为其他类的模板，条目不能直接使用抽象型对象类。辅助类型规定了对象实体的扩展属性。每个条目至少有一个结构性对象类。

对象类本身是可以相互继承的，所以对象类的根类是top抽象型对象类。一般我们可以在/etc/ldap/schema/目录中找到ldap的Schema，如下所示：

```bash
/etc/ldap/schema# ls
collective.ldif    cosine.schema	 java.ldif	openldap.schema
collective.schema  duaconf.ldif		 java.schema	pmi.ldif
corba.ldif	   duaconf.schema	 misc.ldif	pmi.schema
corba.schema	   dyngroup.ldif	 misc.schema	ppolicy.ldif
core.ldif	   dyngroup.schema	 nis.ldif	ppolicy.schema
core.schema	   inetorgperson.ldif	 nis.schema	README
cosine.ldif	   inetorgperson.schema  openldap.ldif
```

我们可以看下inetorgperson.schema的部分片段内容

```bash
## OpenLDAP note: ";binary" transfer should NOT be used as syntax is binary
attributetype ( 2.16.840.1.113730.3.1.216
	NAME 'userPKCS12'
	DESC 'RFC2798: personal identity information, a PKCS #12 PFX'
	SYNTAX 1.3.6.1.4.1.1466.115.121.1.5 )

objectclass	( 2.16.840.1.113730.3.2.2
    NAME 'inetOrgPerson'
	DESC 'RFC2798: Internet Organizational Person'
    SUP organizationalPerson
    STRUCTURAL
	MAY (
		audio $ businessCategory $ carLicense $ departmentNumber $
		displayName $ employeeNumber $ employeeType $ givenName $
		homePhone $ homePostalAddress $ initials $ jpegPhoto $
		labeledURI $ mail $ manager $ mobile $ o $ pager $
		photo $ roomNumber $ secretary $ uid $ userCertificate $
		x500uniqueIdentifier $ preferredLanguage $
		userSMIMECertificate $ userPKCS12 )
	)
```

core.schema部分片段

```bash
objectclass ( 2.5.6.6 NAME 'person'
	DESC 'RFC2256: a person'
	SUP top STRUCTURAL
	MUST ( sn $ cn )
	MAY ( userPassword $ telephoneNumber $ seeAlso $ description ) )

objectclass ( 2.5.6.7 NAME 'organizationalPerson'
	DESC 'RFC2256: an organizational person'
	SUP person STRUCTURAL
	MAY ( title $ x121Address $ registeredAddress $ destinationIndicator $
		preferredDeliveryMethod $ telexNumber $ teletexTerminalIdentifier $
		telephoneNumber $ internationaliSDNNumber $ 
		facsimileTelephoneNumber $ street $ postOfficeBox $ postalCode $
		postalAddress $ physicalDeliveryOfficeName $ ou $ st $ l ) )
```

可以清楚的看到它的父类SUB和可选属性MAY，还有继承来的必要属性MUST(继承自organizationalPerson-->person)。而他的内部属性是定义在自身文件内部，比如userPKCS12。


## LDAP数据组织

如上一节所述，它是一个树型结构，能有效明确的描述一个组织结构特性的相关信息。在这个树型结构上的每个节点，我们称之为“条目（Entry）”，每个条目有自己的唯一可区别的名称（Distinguished Name ，DN）。条目的DN是由条目所在树型结构中的父节点位置（Base DN）和该条目的某个可用来区别身份的属性（称之为RDN如uid , cn）组合而成。对Full DN：“uid=dyp,dc=example,dc=org”而言，其中Base DN：“dc=example,dc=org”，RDN：“uid=dyp”

```
dn：每一个条目都有一个唯一的标识名（distinguished Name ，DN），如 dn："uid=dyp,dc=example,dc=org" 。通过DN的层次型语法结构，可以方便地表示出条目在LDAP树中的位置，通常用于检索。

rdn：一般指dn逗号最左边的部分，如uid=dyp。它与RootDN不同，RootDN通常与RootPW同时出现，特指管理LDAP中信息的最高权限用户。如同文件系统中，带路径的文件名就是DN，文件名就是RDN。

Base DN：LDAP目录树的最顶部就是根，也就是所谓的“Base DN"，如"dc=example,dc=org"。
```

## 相关命令

当然openldap实现提供了一堆命令来做数据操作。先列出来看看有哪些

**ldap开头的**

ldapadd      ldapdelete   ldapmodify   ldappasswd   ldapurl
ldapcompare  ldapexop     ldapmodrdn   ldapsearch   ldapwhoami

ldapsearch指令参数-b 搜索的起始上下文 

- D 绑定搜索的账号Distinguished Name
- h 主机名。地址
- p LDAP服务端口
- l 搜索的最大耗时
- s 从上下文开始的搜索范围，有三个常量base（表示仅当前根对象）/one（当前根对象及下一级）/sub（当前根对象的全部子树）
- W 绑定账号密码
- z 返回结果的最大数量

```
ldapsearch -x -H ldap://localhost -b dc=example,dc=org -D "cn=admin,dc=example,dc=org" -w admin
```

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