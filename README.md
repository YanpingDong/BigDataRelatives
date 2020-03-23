# BigDataRelatives

# [DevOps](DevOps/README.md)

主要涉及集成、部署、监控等。比如Ansible、Jenkins、Docker、K8S、Zabbix等

## [Ansible](DevOps/Ansible/README.md)

简单说明Ansible的主体框架，并了解如何使用Ansible对远程机器进行控制，如何对多台机器进行操作。Python 阵营的配置管理器，只需要控制机器有Python+SSH，并且控制机能ssh到被控制机器就可以方便ansible操作。

## [Docker](DevOps/Docker/README.md)

docker的作用，为什么使用docker等，以及个人在使用过程中其命令使用的理解心得。

docker也简化了各种服务的安装，例如：mysql，jenkins等，只需要下载镜像然后run就好了。

*使用docker在本地搭建各种环境不用再担心破坏系统配置导致莫名的错误，有的时候安装的某个服务，他更改了python导致我的打印机程序出错。现在docker搭建服务，隔离好了就不用担心。而且不用的时候停止就好，如果是在机器上直接搭建mysql不做配置的话需要手动关闭*

## [Zabbix](DevOps/Zabbix/README.md)

什么是zabbix，组成部分与安装

## [LDAP](DevOps/LDAP/README.md)

LDAP（Light Directory Access Portocol），它是基于X.500标准的轻量级目录访问协议。目录是一个为查询、浏览和搜索而优化的数据库，它成树状结构组织数据，类似文件目录一样。可以叫他目录数据库。但和关系数据库不同，它有优异的读性能，但写性能差，并且没有事务处理、回滚等复杂功能，不适于存储修改频繁的数据。所以目录天生是用来查询的。LDAP目录服务是由目录数据库和一套访问协议组成的系统。

从协议衍化上阐述LDAP，它是“目录访问协议DAP——ISO X.500”的衍生，简化了DAP协议，提供了轻量级的基于TCP/IP协议的网络访问，降低了管理维护成本，但保持了强壮且易于扩充的信息框架。LDAP的应用程序可以很轻松的新增、修改、查询和删除目录内容信息。

因为LDAP是开放的Internet标准，支持跨平台的Internet协议，在业界中得到广泛认可的，并且市场上或者开源社区上的大多产品都加入了对LDAP的支持，因此对于这类系统，不需单独定制，只需要通过LDAP做简单的配置就可以与服务器做认证交互。另外LDAP作为一个统一认证的解决方案，重要的优点就在能够快速响应用户的查找需求。

其中OpenLDAP是轻型目录访问协议（Lightweight Directory Access Protocol，LDAP）的自由和开源的实现，在其OpenLDAP许可证下发行，并已经被包含在众多流行的Linux发行版中。

# [HadoopLearning](HadoopLearning/README.md)

Hadoop生态，框架等基本信息，如何编写MR等