# Docker

Docker 是一个开源的应用容器引擎，基于 Go 语言 并遵从Apache2.0协议开源。

Docker 可以让开发者打包他们的应用以及依赖包到一个轻量级、可移植的容器中，然后发布到任何流行的 Linux 机器上，也可以实现虚拟化。

容器是完全使用沙箱机制，相互之间不会有任何接口（类似 iPhone 的 app）,更重要的是容器性能开销极低。

Docker 从 17.03 版本之后分为 CE（Community Edition: 社区版） 和 EE（Enterprise Edition: 企业版）。

*docker理念一个容器运行一个进程！！！*

## OCI

Open Container Initiative，2015年6月创立，围绕容器格式和运行进制定一个开放的工业化标准。由以下两部分组成

- the Runtime Specification
- the Image Specification   (OCF  open container format是其标准)

the Runtime Specification outlines how to run a "filesystem bundle" that is unpacked on disk

At a high-levl an OCI implementation would download an OCI Image then unpack the image into an OCI Runtime filesystem bundle


## 对比虚拟机与Docker

Docker守护进程可以直接与主操作系统进行通信，为各个Docker容器分配资源；它还可以将容器与主操作系统隔离，并将各个容器互相隔离。虚拟机启动需要数分钟，而Docker容器可以在数毫秒内启动。由于没有臃肿的从操作系统，Docker可以节省大量的磁盘空间以及其他系统资源。

说了这么多Docker的优势，大家也没有必要完全否定虚拟机技术，因为两者有不同的使用场景。虚拟机更擅长于彻底隔离整个运行环境。例如，云服务提供商通常采用虚拟机技术隔离不同的用户。而Docker通常用于隔离不同的应用，例如前端，后端以及数据库。

![pic/vmvsdocker.png](pic/vmvsdocker.png)

从结构上来看，容器和虚拟机还是有很大不同的。左图的虚拟机的Guest层，还有Hypervisor层在Docker上已经被Docker Engine层所取代，在这里我们需要知道，Guest OS 是虚拟机安装的操作系统，是一个完整的系统内核，另外Hypervisor可以理解为硬件虚拟化平台，它在后Host OS以内核驱动的形式存在。虚拟机实现资源的隔离的方式是利用独立的Guest OS，以及利用Hypervisor虚拟化CPU、内存、IO等设备来实现的，对于虚拟机实现资源和环境隔离的方案，Docker显然简单很多。

然后Docker并没有和虚拟机一样利用一个独立的Guest OS执行环境的隔离，它利用的是目前当前Linux内核本身支持的容器方式，实现了资源和环境的隔离，简单来说，Docker就是利用Namespace 实现了系统环境的隔离即一个空间不会跨空间看到其它空间，利用了cgroup（control groups）实现了资源的限制即限制使用CPU,Mem等，利用镜像实例实现跟环境的隔离。

通俗来讲就是多容器从属于同一内核（Host OS）。虚拟机是每一个有自己独立的内核（Guest OS）。

namespace的6种名称空间功能完善内核版本如下图，所以如果想要完整的docker功能需要Linux内核达到3.8。

![pic/namespace.png](pic/namespace.png)


## Doker安装

这里只演示ubuntu下的安装

```bash
wget -qO- https://get.docker.com/ | sh
#以非root用户可以直接运行docker时，需要执行
sudo usermod -aG docker ${USER}
sudo service docker start
```

## 容器的网络模式

1. None --- 容器不能访问外部网络，内部存在回路地址。
2. Container --- 将容器的网络栈合并到一起，可与其他容器共享网络。
3. Host --- 与主机共享网络。
4. Bridge --- 默认网络模式，通过主机和容器的端口映射（iptable转发）来通信。桥接是在主机上，一般叫docker0。
5. 自定义网络 --- 主要是为了解决 docker 跨网络通信能力不足的问题和特殊网络需求问题。主要包括：桥接网络、插件网络和Overlay网络（原生的跨主机多子网模型）。

Docker安装后会创建自带的三种网络，可以通过docker network ls查看，通过docker network inspect查看详细信息。

### 虚拟网桥

Docker启动时，自动在主机上创建虚拟网桥docker0，并随机分配一个本地空闲私有网段的一个地址给docker0接口。
虚拟网桥docker0在内核层连通了其他的物理或虚拟网卡，将所有容器和本地主机都放到同一个网络。
docker0接口的默认配置包含了IP地址、子网掩码等，可以在docker服务启动的时候进行自定义配置。

![brige](pic/vmbrige.png)


[参考](https://blog.csdn.net/anliven/article/details/72888052 )

## 镜像加速

鉴于国内网络问题，后续拉取 Docker 镜像十分缓慢，我们可以需要配置加速器来解决，我使用的是网易的镜像地址：http://hub-mirror.c.163.com。

新版的 Docker 使用 /etc/docker/daemon.json（Linux）

请在该配置文件中加入（没有该文件的话，请先建一个）：

```json
{
  "registry-mirrors": ["http://hub-mirror.c.163.com"]
}
```

也可以在/etc/default/docker文件中配置```DOCKER_OPTS="--registry-mirror=http://mirror-address"``` 如果该目录下没有该文件自行创建一个即可。该文件是docker的启动配置文件

## 镜像生成

制做方式

1. Dockerfile
2. 基于容器制作

### Dockerfile

Dockerfile is nothing but the source code for building Docker images

**概念和命令**

- Dockerfile中所有的shell命令都是基于你所创建所用的基础镜像中所拥有的命令,即FROM命令指定的基础镜像中bin中必需有你所使用的命令
  
- .dockerignore文件: ignore some file that you don't want package into image
  
- Format
  - '#' Comment
  - INSTRUCTION arguments
    - The instruction is not case-sensitive
    - Docker runs instructions in a Dockerfile in order
    - The first instruction must be 'FROM' in order to specify the Base Image from which you are building.(FROM \<repository\>[:\<tag\>] or FROM\<repository\>@\<digest\>)
  
- Environment replacement : Environment variables (declared with the ENV statement) can also be used in certain instructions as variables to eb interpreted by the Dockerfile, Environment variables are notated in the Dockerfile either with $variable_name or ${variable_name}

- MAINTAINER(depreacted): MAINTAINER "dyp \<email@email.com\>".  LABEL replace this!Syntax: LABEL \<key\>=\<value\>  \<key\>=\<value\> ... 

- COPY: 从Docker主机复制文件到创建的新镜像文件
  - Syntax
    - COPY \<src\> \<src\> ... \<dest\>  or  COPY ["\<src\>",\<src\> ,...,"\<dest\>"]
      - \<src\> : 要复制的源文件或目录，支持使用通配符
      - \<dest\> : 目标路径，即正在创建 的image的文件系统路径;建议为 \<dest\> 使用绝对路径，否则COPY指定则以WORKDIR为其起始路径
      - 注意： 在路径中有空白字符时，通常使用第二种格式   
  - 文件复制准则
    -  \<src\> 必须是build上下文中的路径，不能是Dockerfile文件父目录中的文件
    -  如果\<src\> 是目录，则其内部文件或子目录会被递归复制，但\<src\> 目录自身不会被复制
    -  如果指定了多个 \<src\> ，或在\<src\> 中使用了通配符，则\<dest\>必须是一个目录，且必须以/结尾
    -  如果\<dest\>事先不存在，它将会被自动创建，这包括其父目录路径

- ADD: 类似于COPY指令， ADD支持使用TAR文件和URL路径
  - Syntax
    - ADD \<src\> \<src\> ... \<dest\>  or  ADD ["\<src\>",\<src\> ,...,"\<dest\>"]
  - 操作准则
    - 同COPY指令
    - 如果\<src\>为URL且\<dest\>不以/结尾，则\<src\>指定的文件将被下载并直接被创建为\<dest\>;如果\<dest\>以/结尾，则文件名URL指定的文件将被直接下载并保存为\<dest\>/\<filename\>
    - 如果\<src\>是一个本地系统上的tar文件，它将会被展开为一个目录，其行为类似于“tar -x”命令;然而，通过URL获取到的tar文件将不会自动展开
    - 如果\<src\>有多个，或其间接或直接使用了通配符，则\<dest\>必须是一个以/结尾的目录路径;否则其被视作一个普通文件，\<src\>的内容将被直接写入到\<dest\>

- WORKDIR
  - 用于为Dockerfile中所有的RUN、CMD、 ENTRYPOINT、COPY和ADD指定设定工作目录
  - Syntax
    - WORKDIR \<dirpath\>
      - 在Dockerfile文件中，WORKDIR指令可出现多次，其路径也可以为相对路径，不过，其是相对此前一个WORKDIR指令指定的路径
      - WORKDIR可以调用由ENV指定定义的变量（ WORKDIR $STATEPATH ）

- VOLUME
  - 用于在image中创建一个挂载点目录，以挂载Docker host（宿主机）上的卷或其它容器上的卷
  - Syntax
    - VOLUME \<mountpoint\>   or   VOLUME ["\<mountpoint\>"]
    - 挂载到宿主机的位置并没有指定，所以docker会自动绑定主机上的一个目录。可以通过```docker inspec NAME|ID```来查看
    - 通过命令行可以指定宿主机目录：```docker run --name test -it -v /home/xqh/myimage:/data imageName```;这样在容器中对/data目录下的操作，还是在主机上对/home/xqh/myimage的操作，都是完全实时同步的
  - 如果挂载点目录路径下此前有文件存在，docker run命令会在卷挂载完成后将此前的所有文件复制到新挂载的卷中

- EXPOSE
  - 用于为容器打开指定要监听的端口以实现与外部通信,但指定不了宿主机端口，所以是动态绑定到宿主机随意端口，但必需要在启动的时候以docker -P爆露。 所以这里指定的可以理解为默认爆露端口
  - Syntax
    - EXPOSE \<port\> [/\<protocol\>][\<port\>[/\<protocol\>]...]   \<protocol\>用于指定传输层协议，可为tcp或udp二者之一，默认为TCP
    - EXPOSE指令可一次指定多个端口： EXPOSE 1121/udp 11211/tcp
  - 命令行可以通过-p(小写p)可以指定要映射的端口，并且在一个指定的端口上只可以绑定一个容器。支持的格式有：```IP:HostPort:ContainerPort | IP::ContainerPort | HostPort:ContainerPort ```缺省掉的值都是由宿主机随机映射

- ENV
  - 用于镜像定义所需的环境变量，并可被Dockerfile文件中位于其后的其它指令（如ENV、ADD、COPY等）所调用;前面即可以在启动的时候指定运行环境变量，后面的则用来指定创建时所需要的变量，分别处理不同阶段，前面是Image运行阶段的事(docker run)，后面是创建Image阶段的事（docker build）
  - 请用格式为$variable_name  or  ${variable_name}
  - Syntax
    - ENV \<key\> \<value\>]  or  ENV \<key\>=\<value\>
    - 第一种格式中，\<key\> 之后的所有内容均会被视作其 \<value\>的组成部分，因此一次只能设置一个变量
    - 第二种格式可用一次设置多个变量，第个变量为一个\<key\>=\<value\>的键值对，如果\<value\>中包含空格，可以以反斜线（\）进行转义，也可以对\<value\>加引号进行标识;另外，反斜线也可用于续行
    - 定义多个变量时，建议使用第二种方式，以便在同一层中完成所有功能
  
- RUN
  - 用来在docker build过程中运行的程序或命令
  - Syntax
    - RUN \<command\>  or  RUN ["\<executable\>", "\<param1\>", "\<param2\>"]
  - 第一种格式中， \<command\> 通常是一个shell命令，且以“/bin/sh -c”的模式来运行它,后面的CMD，ENTRYPOINT也一样，这意味着此进程在容器中的PID不为1,不能接收到Unix信号，因此，当命名用docker stop \<container\>命令停止容器时，此进程接收不到SIGTERM信号。（能接到信息的进程是进程号为1的进程）
  - 第二中语法格式中的参数是一个**JSON格式的数组**(所以中括号并不是可选项的意思，是必需要存在的)，\<executable\>为要运行的命令，后面的\<paramN\>为传递给命令的选项或参数;然而，此程格式指定的命令不会以“/bin/sh -c”来发起而是直接由系统内核创建或可以理解为用exec来启动的命令，因此常见的shell操作如变量替换以及通配符（？，*等）替换将不会进行;不过，如果要运行的命令依赖于些shell特性的话，可以将其替换为类似以下的式式
    - RUN ["/bin/bash", "-c", "\<executable\>", "\<param1\>", "\<param2\>"]
    - 注意：**JSON格式的数组**中要用双引号
  
- CMD
  - 类似于RUN指命，CMD指令也可以用于运行任何命令或应用程序，不过，二者的运行时间点不同
    - RUN指令运行于镜像文件构建过程中，而CMD指令运行镜像文件启动一个容器时
    - CMD指令的首要目的在于为启动的容器指定默认要运行的程序，且其运行结束后，容器也将终止;不过，CMD指定的命令其可以被docker run的命令行选项所覆盖
    - 在Dockerfile中可以存在多个CMD指令，但仅最后一个会生效
  - Syntax
    - CMD \<command\>  or  CMD ["\<executable\>", "\<param1\>", "\<param2\>"]  or CMD ["\<param1\>", "\<param2\>"]    
    - 前两个语法格式的意义同RUN
    - 第三种则用于为ENTRYPOINT指令提供默认参数

    ```docker
    Dockerfile：CMD /bin/httpd -f -h /data/web/html/
    Docker image instpect imageName：tag输出如下
    "Cmd": [
                    "/bin/sh",
                    "-c",
                    "/bin/httpd -f -h /data/web/html/"
                ]

    Dockerfile：CMD ["/bin/httpd", "-f", "-h", "/data/web/html/"]
    Docker image instpect imageName：tag输出如下
    "Cmd": [
                    "/bin/httpd",
                    "-f",
                    "-h",
                    "/data/web/html/"
                ]
    ```

- ENTRYPOINT
  - 类似CMD指令的功能， 用于为容器指定默认运行程序，从而使得容器像是一个单独可执行程序
  - 与CMD不同的是，由ENTRYPOINT启动的程序不会被docker run命令行指定的参数所覆盖，而且，这些命令行参数会被当作参数传递给ENTRYPOINT指定的程序。可以用这种方式把ENTRYPOINT指定成依赖ENV序设置的环境变量的shell脚本，然后在shell脚本中通过```exec $@```来执行CMD传过来的实际要启动的程序命令。
  - 如果非要改写可以在运行进用```--entrypoint```参数，比如在一个镜像中有/bin/python命令，那么我们想看一下版本但ENTRYPOINT设定不可能是python --version，这个时候我们可以使用```docker container run  --entrypoint "/bin/python"  --name sencom image --version```;从上示例可以发现--version的参数并没有直接跟在python命令后，而是在image名字后面跟着，这就是上面说的“命令行参数会被当作参数传递给ENTRYPOINT指定的程序”即python程序。
  
    
    ```docker
    Dockerfile部分内容如下
    ===================================================
    FROM nginx
    LABEL maintainer="xxx <xxx@xxx.com>"

    NGX_DOC_ROOT=/data/web/html/
    ADD entrypint.sh /bin/

    CMD ["/user/sbin/nginx", "-g", "daemon off;"]

    #实际最后执行的是 /bin/entrypoint.sh /user/sbin/nginx -g daemon off(命令行参数会被当作参数传递给ENTRYPOINT指定的程序);其中/user/sbin/nginx -g daemon off;部分会实当成参数传入entrypoint.sh中。所以才能$@引用到并执行替换当前的进程成为主进程PID为1

    ENTRYPOINT ["/bin/entrypoint.sh"]
    ===================================================
    
    entrypoint.sh部分内容如下：
    ===================================================
    #!/bin.sh

    cat > /etc/my.conf << EOF
    server {
      server_name ${HOSTNAME};
      listen ${IP:-0.0.0.0}:{PORT:-80};
      root ${NGX_DOC_ROOT:-/usr/share/nginx/html};
    } 
    EOF

    exec "$@"
    ===================================================
    ```

  - Syntax
    - ENTRYPOINT  \<command\>   or  ENTRYPOINT ["\<executable\>", "\<param1\>", "\<param2\>"]
  - docker run 命令传入的命令参数会覆盖CMD指令的内容并且附加到ENTRYPOINT命令最后做为其参数使用
  - Dockerfile文件中也可以存在多个ENTRYPOINT指令，但仅有最后一个会生效

    ```
    Dockerfile:ENTRYPOINT ["/bin/httpd", "-f", "-h", "/data/web/html/"]
    Docker image instpect imageName：tag输出如下（注：没有ENTRYPOINT的时候是没有以下输出的）
    "Entrypoint": [
                    "/bin/httpd",
                    "-f",
                    "-h",
                    "/data/web/html/"
                ]
    ```

- USER
  - 用于指定运行image时或运行Dockerfile中任何RUN、CMD或ENTRYPOINT指令指定的程序时的用户名或UID
  - 默认情况下，container的运行身份为root用户
  - Syntax
    - USER \<UID\>|\<UserName\> 
    - 需要注意的是，\<UID\>可以为任意数字，但实践中其必须为容器中/etc/passwd中某用户的有效UID，否则，docker run命令将运行失败

- HEALTHCHECK
  - 告诉Docker如何去测试容器是否工作。比如测试一个web服务是否进入了死循环，是否还能处理新的链接，是否还在工作等
  - Syntax
    - HEALTHCHECK [OPTIONS] CMD 通过在容器内部运行CMD命令来做健康检测
      - OPTIONS选项   
        -  --interval=DURATION(default 30s)
        -  --timeout=DURATION(default 30s)
        -  --start-period=DURATION(default 0s) #主进程启运多后开始检测
        -  --retries=N (default 3)
     - CMD退出状态码
       - 0:success
       - 1:unhealthy
       - 2:reserved -不要使用该退出码
     - For example: HEALTHCHECK --interva=5m --timeout=3s CMD curl -f http://localhost/ || exit 1
    - HEALTHCHECK NONE 关闭所有检测，包括从基镜像继承的

- SHELL
  - 定义默认使用的是哪个shell
  - 默认Linux是["/bin/sh", "-c"], Win是["cmd", "/S", "/C"]
  - SHELL指令必须以JSON数据格式写： SHELL ["executable", "parameters"]
  - 可以出现多次

- STOPSIGNAL
  - 设置系统调用息号值，该值可以使容器退出
  - 该值可以是 syscall table中的位序
  - Syntax： STOPSIGNAL signal

- ARG
  - 定义build-time时使用的变量， builder过程中用--build-arg \<varname\> = \<value\>来使用;而ENV是在running-time进用的
  - 用${varname}来在Dockerfile中引用

- ONBUILD
  - Dockerfile中定义一个触发器,在别人用该镜像做基础镜像构建的时候运行
  - ONBUILD不能自我嵌套，且不会触发FROM和MAINTAINER指令
  - 在ONBUILD中使用ADD或COPY指令应该小心，因为新构建过程的上下文可能会缺失文件导致构建失败
  - Syntax
    - ONBUILD \<INSTRUCTION\>
  
  
**Dockerfile 首字母要大写, 且命令能压缩成一个的尽量做成一个，因为每一个更改命令都成为镜像的一层（分层联合挂载机制）**

**示例**

```Dockerfile
#基于busybox最新的版本进行创建镜像
FROM busybox:latest

#在以该镜像做基础镜像做镜像的时候，会先下载一个nginx到/data/web/nginx1/
ONBUILD ADD http://nginx.org/download/nginx-1.14.2.tar.gz /data/web/nginx1/

ARG author="lear <lear521@163.com>"

MAINTAINER ${author}
# LABLE maintainer="lear <lear521@163.com>"

#文件
COPY index.html /data/web/html/

#目录
COPY youtube_topic/ /data/web/youtube_topic/

#会从网上下载并放到指定目录下
ADD http://nginx.org/download/nginx-1.14.2.tar.gz /data/web/nginx/
RUN cd /data/web/nginx/ && \
    tar -xf nginx-1.14.2.tar.gz 

#docker内部目录
WORKDIR /usr/local/
#这个时候目录是/usr/local/
ADD index.html ./  
 
#将docker下/data/mysql/目录做为内部挂载点
VOLUME /data/mysql/   

EXPOSE 80/tcp

ENV DOC_ROOT /data/web/htmlother/
#如果DOC_ROOT没有值则用/data/web/html2/
COPY index.html ${DOC_ROOT:-/data/web/html2/} 

RUN echo '<h1>Busybox httpd server</h1>' > /data/web/html/index2.html

#CMD ["/bin/httpd", "-f", "-h", "/data/web/html/"]

HEALTHCHECK --interval=5m --start-period=10s --timeout=3s CMD wget -O - -q http://localhost/ || exit 1

ENTRYPOINT ["/bin/httpd", "-f", "-h", "/data/web/html/"]
```

## 基于容器制作

实际是把对一个容器的变更部分变成镜像

docker commit [options] container [repository[:tag]]

```
Options:
  -a, --author string    Author (e.g., "John Hannibal Smith <hannibal@a-team.com>")
  -c, --change list      Apply Dockerfile instruction to the created image
  -m, --message string   Commit message
  -p, --pause            Pause container during commit (default true)
```

*一般会用-p来先暂停容器，避免打包过程种还出现更改。*

## 镜像的导入导出

该方式是为了避免push到仓库。用导出文做共享,方便本地多机简单测试

```
docker save  #导出
docker save  -o myimages.gz dyp/httpd:v0.2

docker load  #导入
docker load -i myimages.gz
```

## Docker命令类

- docker image
- docker network
- docker container
- docker logs [-c -f ]

*Ctrl p+q退出交并使运行起来的容器运行在后台。或者在启动的时候用-d参数*

## 设置Docker资源限制

以下都是docker ran or create所支持的。

### MEM

不设置没限制

| --memory-swap | --memory | 功能  |
| ------------- | -------- | ---- |
|正数 S|正数 M| 容器可用总空间为S，其中ram为M，Swap为（S-M）,若S=M，则无可用Swap|
|0|正数 M|相当未设置Swap|
|unset|正数 M|若主机（Docker Host）启用了swap,则容器的可用swa务
|-1|正数M|若主机（Docker Host）启用了swap,则容器可使用的最大务上的所有swap空间的Swap资源|

--oom-kill-disable : 默认情况下，如果发生OOM错，内核会杀掉容务程。如果想改变这种行为就可以使用该参数。但只有当设置了-m/--memory后，才能设置务。如果-m没有设置，宿主机会的内存使用会超出限制，内核需要去杀掉宿主机系统进程来获务

### CPU

不设置没限制，在Docker >=1.13的版本， 还可以设置实时调度优先级。

```
CFS调度算法负责在linux上维护为任务提供处理器时间方面的平衡。设置值区间为[100,139]或[0,99],后者是实时优先级取值范围
```

--cpu-shares int: 通过比例进行分配，比如A：1024  B：512 ，这AB两个容器以2：1的方式进行分配CPU

--cpus decimal : 固定分配

--cpuset-cpus string: 指定CPU核型，即固定容器所能运行的CPU。第一个CPU值为0.             CPUs in which to allow execution (0-3, 0,1)

```
Usage:200%代表使用2个cpu
```