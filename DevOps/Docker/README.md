# Docker

Docker 是一个开源的应用容器引擎，基于 Go 语言 并遵从Apache2.0协议开源。

Docker 可以让开发者打包他们的应用以及依赖包到一个轻量级、可移植的容器中，然后发布到任何流行的 Linux 机器上，也可以实现虚拟化。

容器是完全使用沙箱机制，相互之间不会有任何接口（类似 iPhone 的 app）,更重要的是容器性能开销极低。

Docker 从 17.03 版本之后分为 CE（Community Edition: 社区版） 和 EE（Enterprise Edition: 企业版）。

## 对比虚拟机与Docker

Docker守护进程可以直接与主操作系统进行通信，为各个Docker容器分配资源；它还可以将容器与主操作系统隔离，并将各个容器互相隔离。虚拟机启动需要数分钟，而Docker容器可以在数毫秒内启动。由于没有臃肿的从操作系统，Docker可以节省大量的磁盘空间以及其他系统资源。

说了这么多Docker的优势，大家也没有必要完全否定虚拟机技术，因为两者有不同的使用场景。虚拟机更擅长于彻底隔离整个运行环境。例如，云服务提供商通常采用虚拟机技术隔离不同的用户。而Docker通常用于隔离不同的应用，例如前端，后端以及数据库。

![pic/vmvsdocker.png](pic/vmvsdocker.png)

从结构上来看，容器和虚拟机还是有很大不同的。左图的虚拟机的Guest层，还有Hypervisor层在Docker上已经被Docker Engine层所取代，在这里我们需要知道，Guest OS 是虚拟机安装的操作系统，是一个完整的系统内核，另外Hypervisor可以理解为硬件虚拟化平台，它在后Host OS以内核驱动的形式存在。虚拟机实现资源的隔离的方式是利用独立的Guest OS，以及利用Hypervisor虚拟化CPU、内存、IO等设备来实现的，对于虚拟机实现资源和环境隔离的方案，Docker显然简单很多。

然后Docker并没有和虚拟机一样利用一个独立的Guest OS执行环境的隔离，它利用的是目前当前Linux内核本身支持的容器方式，实现了资源和环境的隔离，简单来说，Docker就是利用Namespace 实现了系统环境的隔离即一个空间不会跨空间看到其它空间，利用了cgroup（control groups）实现了资源的限制即限制使用CPU,Mem等，利用镜像实例实现跟环境的隔离。

通俗来讲就是多容器从属于同一内核（Host OS）。虚拟机是每一个有自己独立的内核（Guest OS）。

namespace的功能完善内核版本如下图，所以如果想要完整的docker功能需要Linux内核达到3.8。

![pic/namespace.png](pic/namespace.png)


## OCI

Open Container Initiative，2015年6月创立，围绕容器格式和运行进制定一个开放的工业化标准。由以下两部分组成

- the Runtime Specification
- the Image Specification   (OCF  open container format是其标准)

the Runtime Specification outlines how to run a "filesystem bundle" that is unpacked on disk

At a high-levl an OCI implementation would download an OCI Image then unpack the image into an OCI Runtime filesystem bundle

