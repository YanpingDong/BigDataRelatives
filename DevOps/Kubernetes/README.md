# Docker存在的问题

1. 无法有效集群
2. 没有有效容灾、自愈机制
3. 没有预设编排模板\容器间依赖管理、无法快速、大规模容器调度
4. 没有统一配置管理中心工具
5. 没有容器生命周期管理工具

所以才引入了编排工具，compose、swarm or k8s。

# 编排工具

1. docker compose(单机编排)，docker swarm(增加多机能力)，docker machine(把docker host初始化并加入docker swarm的管理之下)
2. mesos(资源管理器), marathon（将docker host资源抽像成mesos可管理资源）
3. kubernetes

# 什么是Kubernetes

Kubernetes（k8s）是自动化容器操作的开源平台，这些操作包括部署，调度和节点集群间扩展。如果你曾经用过Docker容器技术部署容器，那么可以将Docker看成Kubernetes内部使用的低级别组件。**Kubernetes不仅仅支持Docker，还支持Rocket（这是另一种容器技术）**。

Kubernetes特性：

- 自动化容器的部署和复制
- 随时扩展或收缩容器规模
- 将容器组织成组，并且提供容器间的负载均衡
- 很容易地升级应用程序容器的新版本
- 密钥和配置管理
- 存储(docker volume)编排
- 批量处理执行
- 提供容器弹性，如果容器失效就替换它，等等...

下图展示了一个完整的K8s所拥有的元素，以及元素之间的关系。

![AllElementInK8s](pic/AllElementInK8s.png)

**图元素解析**

1. 每个应用的运行都要依赖一个环境，对于一个 PHP 应用来说，这个环境包括了一个 webserver，一个可读的文件系统和 PHP 的 engine。（对应于图上web server的方框）
2. 容器为应用提供了隔离的环境，在这个环境里应用就能运行起来（对应于图container方框，比如docker可以是其容器）。但是这些相互隔离的容器需要管理，也需要跟外面的世界沟通。**共享的文件系统，网络，调度，负载均衡和资源分配**都是挑战。
3. pod代表着一个运行着的工作单元（对应于图pod方框）。通常，每个pod中只有一个容器，但有些情况下，如果几个容器是紧耦合的，这几个容器就会运行在同一个pod中。Kubernetes 承担了 pod与外界环境通信的工作。并且同一个Pod里的容器共享同一个网络命名空间，可以使用localhost互相通信;而且同一个Pod里的容器可以共享volume。Pod的里运行的多个容器，其中一个叫主容器，而其它的是辅助的，比如运行一个Nginx服务的Docker，但又需要收集Log这个时候就可以再启一个Log收集的服务Docker,前一个就叫主容器。
4. Replication controller 提供了一种管理任意数量 pod 的方式。一个 replication controller 包含了一个 pod 模板，这个模板可以被不限次数地复制。通过 replication controller，Kubernetes 可以管理 pod 的生命周期，包括扩/缩容，滚动部署和监控等功能。确保任意时间都有指定数量的Pod“副本”在运行。（对应于图replication controller方框）
   
   ```
    当创建Replication Controller时，需要指定两个东西：
    1. Pod模板：用来创建Pod副本的模板。
    2. Label：Replication Controller需要监控的Pod的标签。
   ```

5. service 可以和 Kubernetes 环境中其它部分（包括其它 pod 和 replication controller）进行通信，告诉它们你的应用提供什么服务。Pod可以增减，但是 service 的 IP 地址和端口号是不变的。而且其它应用可以通过 Kubernetes 的服务发现找到你的 service。并且service是定义一系列Pod以及访问这些Pod的策略的一层抽象。Service通过Label找到Pod组。（对应service方框）
   ```
   假定有2个后台Pod，并且定义后台Service的名称为‘backend-service’，lable选择器为（tier=backend, app=myapp）。backend-service 的Service会完成如下两件重要的事情：
   1. 会为Service创建一个本地集群的DNS入口，因此前端Pod只需要DNS查找主机名为 ‘backend-service’，就能够解析出前端应用程序可用的IP地址。
   2. 现在前端已经得到了后台服务的IP地址，但是它应该访问2个后台Pod的哪一个呢？Service在这2个后台Pod之间提供透明的负载均衡，会将请求分发给其中的任意一个。通过每个Node上运行的代理（kube-proxy）完成。

   有一个特别类型的Kubernetes Service，称为'LoadBalancer'，作为外部负载均衡器使用，在一定数量的Pod之间均衡流量。比如，对于负载均衡Web流量很有用。
   ```
6. Volume 代表了一块容器可以访问和存储信息的空间，对于应用来说，volume 是一个本地的文件系统。实际上，除了本地存储，Ceph、Gluster、Elastic Block Storage 和很多其它后端存储都可以作为 volume。（对应于volume方框）
7. Namespace 是 Kubernetes 内的分组机制。Service，pod，replication controller 和 volume 可以很容易地和 namespace 配合工作，但是 namespace 为集群中的组件间提供了一定程度的隔离。（对应于namespace方框）

另一个重要概念label：Kubernetes的Label是attach到Pod的一对键/值对，用来传递用户定义的属性。来区分事物，还可以根据 label 来查询。label 是开放式的：可以根据角色，稳定性或其它重要的特性来指定。比如，你可能创建了一个"tier"和“app”标签，通过Label（tier=frontend, app=myapp）来标记前端Pod容器，使用Label（tier=backend, app=myapp）标记后台Pod。

# Kubernetes框架

一个K8S系统，通常称为一个K8S集群（Cluster）。这个集群主要包括两个部分：一个Master节点（主节点）和 一群Node节点（计算节点）

下图是整体结构，是一个主从式集群，Master结点可冗余配置，负责管理和控制。Node节点是工作负载节点，里面是具体的容器。

![k8swholepicture.jpg](pic/k8swholepicture.jpg)

**Master节点**

![k8smaster.jpg](pic/k8smaster.jpg)

Master节点包括API Server、Scheduler、Controller manager、etcd。

API Server(kube-apiserver)：是整个系统的对外接口,集群的统一入口 ，各组件协调者，以RESTful API提供接口服务，所有对象资源的增删改查和监听操作都交给APIServer处理后再提交给Etcd存储;供客户端和其它组件调用，相当于“营业厅”。

Scheduler：负责对集群内部的资源进行调度，相当于“调度室”。也就是根据调度算法为新创建的Pod选择一个Node节点，可以任意部署,可以部署在同一个节点上,也可以部署在不同的节点上。

Controller manager(kube-controller-manager)：负责管理控制器，相当于“大总管”。也就是处理集群中常规后台任务，一个资源对应一个控制器，而ControllerManager就是负责管理这些控制器的。

etcd： 分布式键值存储系统。用于保存集群状态数据，比如Pod、Service等对象信息。

**Node节点**

![k8snode.jpg](pic/k8snode.jpg)

Node节点包括Docker、kubelet、kube-proxy、Fluentd、kube-dns（可选），还有就是Pod。

- Docker: 容器引擎，运行容器，但k8s支持的容器并不只限于Docker。比如rocket也可以
- Kubelet: kubelet是Master在Node节点上的Agent，管理本机运行容器的生命周期，主要负责监视（即Master Scheduler）指派到它所在Node上的Pod，包括创建、修改、监控、删除等。
- Kube-proxy: 在Node节点上实现Pod网络代理，维护网络规则和四层负载均衡工作。
- Fluentd: 主要负责日志收集、存储与查询。

```
Pod是Kubernetes最基本的操作单元。一个Pod代表着集群中运行的一个进程，它内部封装了一个或多个紧密相关的容器。除了Pod之外，K8S还有一个Service的概念，一个Service可以看作一组提供相同服务的Pod的对外访问接口。
```

[参考引用](https://blog.csdn.net/wenjianfeng/article/details/90130895)

# Kubeadm

kubeadm 能帮助您建立一个小型的符合最佳实践的Kubernetes 集群。通过使用 kubeadm, 您的集群会符合 Kubernetes 合规性测试的要求. Kubeadm 也支持其他的集群生命周期操作，比如升级、降级和管理启动引导令牌。

因为您可以在不同类型的机器（比如笔记本、服务器和树莓派等）上安装 kubeadm，因此它非常适合与 Terraform 或 Ansible 这类自动化管理系统集成。

kubeadm 的简单便捷为大家带来了广泛的用户案例：
- 新用户可以从 kubeadm 开始来试用 Kubernetes。
- 熟悉 Kubernetes 的用户可以使用 kubeadm 快速搭建集群并测试他们的应用。
- 大型的项目可以将 kubeadm 和其他的安装工具一起形成一个比较复杂的系统。

kubeadm 的设计初衷是为新用户提供一种便捷的方式来首次试用 Kubernetes， 同时也方便老用户搭建集群测试他们的应用。 此外 kubeadm 也可以跟其它生态系统与/或安装工具集成到一起，提供更强大的功能。

默认情况下K8S使用CRI（Container Runtime Interface）接口来与用户选择的容器运行时通讯。如果用户没有指定运行时，Kubeadm会自动尝试扫描检测安装在本机的常见的容器运行时（container runtime）。实际是通过扫描Unix主机socket。如下所示几种常见的Runtime和对应的socket parth

Runtime |	Path to Unix domain socket
---|---
Docker | /var/run/docker.sock
containerd | /run/containerd/containerd.sock
CRI-O	| /var/run/crio/crio.sock

如果Docker和containerd都被检测到，docker的有限级更高。因为在Docker 18.09版本后containerd会和Docker一起被安装。如果是其他组合被检测到，kubeadm会退出并报错。（比如containerd和CRIO同时被检测到）

## PKI证书

Kubernets需要PKI证书来做身份验证，但如果你是使用kubeadm来安装的kubernetes，会自动安装该证书。当然你也可以使用你自己私有的证书。

使用Kubeadm安装K8s，证书会存放在/etc/kubernetes/pki目录中

## kubeadm创建集群

Master节点需要：安装kubelet，kubeadm，kubectl，Dokcer

Node节点需要：安装kubelet，kubeadm，kubectl，Dokcer，并加入master节点

**环境设置**

```bash
#以下操作都是在Ubuntu 16.04下
Step1：关闭swap
使用命令‘swapoff -a’ 禁用swap

Step2：关闭防火墙和SElinux
systemctl stop firewalld
输出：Failed to stop firewalld.service: Unit firewalld.service not loaded.
执行：systemctl disable firewalld
输出：Failed to execute operation: No such file or directory
执行：setenforce 0
输出：The program 'setenforce' is currently not installed. You can install it by typing:apt install selinux-utils
执行：apt install selinux-utils
写入
$ vim /etc/selinux/config
写入如下内容，
SELINUX=disabled

Step3:安装并启动docker-ce版本
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh
#以非root用户可以直接运行docker时，需要执行
sudo usermod -aG docker ${USER}
#Remember that you will have to log out and back in for this to take effect!
#提示说需要log out就好，但我在ubuntu下发现需要reboot。docker组是安装docker引擎时候创建的
sudo service docker start
```

**安装kubeadm,kubelet,kubectl**

这三个包需要安装在所有的机器上。

- kubeadm: 用来引导创建cluster的命令工具

- kubelet: 在所有集群机器上，用来启动pods和containers.

- kubectl: 用来访问集群的命令行

kubeadm不会安装和管理kubelet和kubectl，所以你需要保证这两个包的版本能匹配Kubeadm安装的K8S控制面板。使用国内镜像站

```bash
sudo apt-get update && sudo apt-get install -y apt-transport-https curl

sudo curl -s https://mirrors.aliyun.com/kubernetes/apt/doc/apt-key.gpg | sudo apt-key add -

sudo tee /etc/apt/sources.list.d/kubernetes.list << EOF
deb https://mirrors.aliyun.com/kubernetes/apt kubernetes-xenial main
EOF

sudo apt-get update

#查看版本
apt-cache madison kubeadm
#查看过版本之后就可以指定版本安装
$ sudo apt-get install -y kubelet=1.14.0-00 kubeadm=1.14.0-00 kubectl=1.14.0-00
$ sudo apt-mark hold kubelet=1.14.0-00 kubeadm=1.14.0-00 kubectl=1.14.0-00
```

*kubeadm config print init-defaults 来查看kubeadm的启动配置，更多命令命使用kubeadm help或者Use "kubeadm [command] --help" for more information about a command.*

```
Note:
注意: 如果您的机器已经安装了 kubeadm, 请运行 apt-get update && apt-get upgrade 或者 yum update 来升级至最新版本的 kubeadm.

升级过程中，kubelet 会每隔几秒钟重启并陷入了不断循环等待 kubeadm 发布指令的状态。 这个死循环的过程是正常的，当升级并初始化完成您的主节点之后，kubelet 才会正常运行。

Kubernetes 发现版本的通常只维护支持九个月，在维护周期内，如果发现有比较重大的 bug 或者安全问题的话， 可能会发布一个补丁版本。同时也适用于 kubeadm。
```

## 启动Shell自动补全

kubectl为bash和zsh提供自动补全机制，但在这之前要先确认bash-completion在本机是有的，可以通过`type _init_completion`查看，如果有，然后在Linux下只需要使用`kubectl completion bash`命令就可以。

### 安装bash-completion

如果没有安装可以用`apt-get install bash-completion or yum install bash-completion`命令安装。

通过命令安装成功后，会有`/usr/share/bash-completion/bash_completion`文件生成，然后尝试重启你的shell窗口，执行`type _init_completion`命令，如果成功一切OK。如果没有成功，將`source /usr/share/bash-completion/bash_completion`添加到`～/.bashrc`文件中。在重启Shell窗口就好了。

### master节点部署

执行下面命令就可以在你的master机器上启动K8S相关服务

Step1:生成init启动配置文件

```
$ kubeadm config print init-defaults > kubeadm-config.yaml
$ more kubeadm-config.yaml 
apiVersion: kubeadm.k8s.io/v1beta2
bootstrapTokens:
- groups:
  - system:bootstrappers:kubeadm:default-node-token
  token: abcdef.0123456789abcdef
  ttl: 24h0m0s
  usages:
  - signing
  - authentication
kind: InitConfiguration
localAPIEndpoint:
  advertiseAddress: 1.2.3.4  ##宿主机IP地址
  bindPort: 6443
nodeRegistration:
  criSocket: /var/run/dockershim.sock
  name: learleepc   ##当前节点在k8s集群中名称
  taints:
  - effect: NoSchedule
    key: node-role.kubernetes.io/master
---
apiServer:
  timeoutForControlPlane: 4m0s
apiVersion: kubeadm.k8s.io/v1beta2
certificatesDir: /etc/kubernetes/pki
clusterName: kubernetes
controlPlaneEndpoint: "你的负载均衡器IP地址:你的负载均衡器端口"
controllerManager: {}
dns:
  type: CoreDNS
etcd:
  local:
    dataDir: /var/lib/etcd
imageRepository: registry.cn-hangzhou.aliyuncs.com/google_containers    ##使用阿里的镜像地址，否则无法拉取镜像
kind: ClusterConfiguration
kubernetesVersion: v1.18.0
networking:
  dnsDomain: cluster.local
  serviceSubnet: 10.96.0.0/12 
scheduler: {}

```

Step2:拉取镜像，并启动
```
$ kubeadm  config images pull  --config kubeadm-config.yaml  

$ kubeadm init --config=kubeadm-config.yaml --upload-certs | tee kubeadm-init.log
```

简单方式，可以直接使用下面的命令启动maseter,跳过上面两步

```
kubeadm init \
--apiserver-advertise-address=your_master_host_id \
--image-repository registry.aliyuncs.com/google_containers \
--kubernetes-version v1.15.0 \
--control-plane-endpoint "LOAD_BALANCER_DNS:LOAD_BALANCER_PORT"
```
由于默认拉取镜像地址k8s.gcr.io国内无法访问，这里指定阿里云镜像仓库地址。

启动成功后会输出如下信息,并且告知了如何添加master和node节点：

```
Your Kubernetes control-plane has initialized successfully!

To start using your cluster, you need to run the following as a regular user:

  mkdir -p $HOME/.kube
  sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
  sudo chown $(id -u):$(id -g) $HOME/.kube/config

You should now deploy a pod network to the cluster.
Run "kubectl apply -f [podnetwork].yaml" with one of the options listed at:
  https://kubernetes.io/docs/concepts/cluster-administration/addons/

You can now join any number of the control-plane node running the following command on each as root:
# master节点用以下命令加入集群：
  kubeadm join your_master_ip:port --token abcdef.0123456789abcdef \
    --discovery-token-ca-cert-hash sha256:37041e2b8e0de7b17fdbf73f1c79714f2bddde2d6e96af2953c8b026d15000d8 \
    --control-plane --certificate-key 8d3f96830a1218b704cb2c24520186828ac6fe1d738dfb11199dcdb9a10579f8

Please note that the certificate-key gives access to cluster sensitive data, keep it secret!
As a safeguard, uploaded-certs will be deleted in two hours; If necessary, you can use
"kubeadm init phase upload-certs --upload-certs" to reload certs afterward.

Then you can join any number of worker nodes by running the following on each as root:

# 工作节点用以下命令加入集群
kubeadm join your_master_ip:port --token abcdef.0123456789abcdef \
    --discovery-token-ca-cert-hash sha256:37041e2b8e0de7b17fdbf73f1c79714f2bddde2d6e96af2953c8b026d15000d8 
```

### node节点部署

使用master节点启动信息中的提示命令就可以启动了！

当然如果需要配置文件启动可以使用如下命令生成默认配置文件

```
$ kubeadm config print join-defaults
apiVersion: kubeadm.k8s.io/v1beta2
caCertPath: /etc/kubernetes/pki/ca.crt
discovery:
  bootstrapToken:
    apiServerEndpoint: kube-apiserver:6443
    token: abcdef.0123456789abcdef
    unsafeSkipCAVerification: true
  timeout: 5m0s
  tlsBootstrapToken: abcdef.0123456789abcdef
kind: JoinConfiguration
nodeRegistration:
  criSocket: /var/run/dockershim.sock
  name: learleepc
  taints: null

```
## single-node kubernetres集群

通过安装Minikube，可以通过虚拟机实现单节点K8S集群（单节点或通过虚拟化技术实现的单机集群）。但我们需要先使用`grep -E --color 'vmx|svm' /proc/cpuinfo`命令查看机器是否支持虚拟化，如果支持命令是非空输出。

安装KVM或VirtualBox管理程序。当然Minikube同样支持`--driver=none`选项，让K8S组件运行在宿主机上而不是VM中。使用`--driver=none`选项，需要Docker和Linux环境就可以。当然这里需要的使用.deb安装Docker，而不是snap版本的！！！

*none选项会有安全和数据丢失问题*

可选的driver如下（除了none都需要事先安装好）：

-    docker 
-    virtualbox
-    podman  (EXPERIMENTAL)
-    vmwarefusion
-    kvm2 
-    hyperkit
-    hyperv Note that the IP below is dynamic and can change. It can be retrieved with minikube ip.
-    vmware (VMware unified driver)
-    parallels 
-    none (Runs the Kubernetes components on the host and not in a virtual machine. You need to be running Linux and to have Docker installed.)并且你需要是root用户才能启动。要不就用sudo。

**minikube安装**

通过上面了解，我们梳理下minikube安装前置条件：
- 需要一个Driver（当然可以使node）,这一条算是非强制。
- 需安装docker，可以参考[Docker文档](../Docker/README.md)
- 需安装kubectl
- `grep -E --color 'vmx|svm' /proc/cpuinfo`命令需要有输出，即支持虚拟化

官方安装方式`curl -Lo minikube https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64 && chmod +x minikube && sudo mv minikube /usr/local/bin/`，但这种网络一直很不稳定。


**启动本地集群**

安装好minikube后启动如下：

```
$ sudo minikube start --driver=none
😄  Ubuntu 16.04 上的 minikube v1.12.1
✨  根据用户配置使用 none 驱动程序
👍  Starting control plane node minikube in cluster minikube
🤹  Running on localhost (CPUs=4, Memory=15980MB, Disk=922201MB) ...
ℹ️  OS release is Ubuntu 16.04.6 LTS
🐳  正在 Docker 19.03.12 中准备 Kubernetes v1.18.3…
    > kubeadm.sha256: 65 B / 65 B [--------------------------] 100.00% ? p/s 0s
    > kubelet.sha256: 65 B / 65 B [--------------------------] 100.00% ? p/s 0s
    > kubectl.sha256: 65 B / 65 B [--------------------------] 100.00% ? p/s 0s
    > kubeadm: 37.97 MiB / 37.97 MiB [---------------] 100.00% 1.21 MiB p/s 31s
    > kubectl: 41.99 MiB / 41.99 MiB [---------------] 100.00% 1.35 MiB p/s 31s
    > kubelet: 108.04 MiB / 108.04 MiB [-----------] 100.00% 1.43 MiB p/s 1m16s
🤹  开始配置本地主机环境...

❗  The 'none' driver is designed for experts who need to integrate with an existing VM
💡  Most users should use the newer 'docker' driver instead, which does not require root!
📘  For more information, see: https://minikube.sigs.k8s.io/docs/reference/drivers/none/

❗  kubectl 和 minikube 配置将存储在 /home/learlee 中
❗  如需以您自己的用户身份使用 kubectl 或 minikube 命令，您可能需要重新定位该命令。例如，如需覆盖您的自定义设置，请运行：

    ▪ sudo mv /home/learlee/.kube /home/learlee/.minikube $HOME
    ▪ sudo chown -R $USER $HOME/.kube $HOME/.minikube

💡  此操作还可通过设置环境变量 CHANGE_MINIKUBE_NONE_USER=true 自动完成
🔎  Verifying Kubernetes components...
🌟  Enabled addons: default-storageclass, storage-provisioner
🏄  完成！kubectl 已经配置至 "minikube"
```

从提示信息可以看出，官方推荐用docker代替none做启动driver参数,所以推荐使用`minikube start  --driver=docker --registry-mirror=https://registry.docker-cn.com`

```
$ minikube start --driver=docker --registry-mirror=https://registry.docker-cn.com
😄  Ubuntu 16.04 上的 minikube v1.12.1
✨  根据用户配置使用 docker 驱动程序
👍  Starting control plane node minikube in cluster minikube
🔥  Creating docker container (CPUs=2, Memory=3900MB) ...
🐳  正在 Docker 19.03.2 中准备 Kubernetes v1.18.3…
🔎  Verifying Kubernetes components...
🌟  Enabled addons: default-storageclass, storage-provisioner
🏄  完成！kubectl 已经配置至 "minikube"

```

**查看本地集群状态**

完成后可以通过如下命令查看状态

```
$ sudo minikube status
minikube
type: Control Plane
host: Running
kubelet: Running
apiserver: Running
kubeconfig: Configured

```

或者通过如下命令查看启动的集群

```
$ sudo kubectl cluster-info
Kubernetes master is running at https://192.168.1.107:8443
KubeDNS is running at https://192.168.1.107:8443/api/v1/namespaces/kube-system/services/kube-dns:dns/proxy

```

启动成功后就可以通过kubectl和你的k8s本地集群进行交互了。就像是一个完整的多物理机集群。例如，启动服务器：`kubectl create deployment hello-minikube --image=k8s.gcr.io/echoserver:1.4`

将服务公开为NodePort(8080端口是镜像对外公布的端口）：
`kubectl expose deployment hello-minikube --type=NodePort --port=8080`

minikube使您可以在浏览器中轻松打开此公开的端点：

```
$ minikube service hello-minikube1

|-----------|-----------------|-------------|-------------------------|
| NAMESPACE |      NAME       | TARGET PORT |           URL           |
|-----------|-----------------|-------------|-------------------------|
| default   | hello-minikube1 |        8080 | http://172.17.0.4:31466 |
|-----------|-----------------|-------------|-------------------------|
* 正通过默认浏览器打开服务 default/hello-minikube1...
````

一切顺利的话可以在浏览器看到如下界面：

![](pic/hellominikubeDemo.png)

如过一切顺利还好，但大概率是会有错，通过后面的dashboard可以看到没办法拉取镜像，错误如下：

```
Failed to pull image "k8s.gcr.io/echoserver:1.4": rpc error: code = Unknown desc = Error response from daemon: Get https://k8s.gcr.io/v2/: net/http: request canceled while waiting for connection (Client.Timeout exceeded while awaiting headers)
```

如果报错可以尝试，修改镜像的：`kubectl create deployment hello-minikube--image=mirrorgooglecontainers/echoserver:1.4
deployment.apps/hello-minikube1 created`然后在执行后面打开端口和暴露服务的命令


**启动dashboard**

```
$ minikube dashboard
🔌  正在开启 dashboard ...
🤔  正在验证 dashboard 运行情况 ...
🚀  Launching proxy ...
🤔  正在验证 proxy 运行状况 ...
🎉  Opening http://127.0.0.1:39163/api/v1/namespaces/kubernetes-dashboard/services/http:kubernetes-dashboard:/proxy/ in your default browser...
```

![](pic/MinikubDashboard.png)


**停止minikube**

```bash
$ sudo minikube stop
✋  Stopping "minikube" in none ...
🛑  Node "minikube" stopped.

#然后再看状态
$ sudo kubectl cluster-info

To further debug and diagnose cluster problems, use 'kubectl cluster-info dump'.
The connection to the server localhost:8080 was refused - did you specify the right host or port?
$ sudo minikube status
minikube
type: Control Plane
host: Stopped
kubelet: Stopped
apiserver: Stopped
kubeconfig: Stopped
```

**删除minikube创建的集群**

```bash
$ sudo minikube delete
🔄  正在使用 kubeadm 卸载 Kubernetes v1.18.3…
🔥  正在删除 none 中的“minikube”…
💀  Removed all traces of the "minikube" cluster.

#再看状态
$ sudo kubectl cluster-info

To further debug and diagnose cluster problems, use 'kubectl cluster-info dump'.
The connection to the server localhost:8080 was refused - did you specify the right host or port?
$ sudo minikube status
🤷  There is no local cluster named "minikube"
👉  To fix this, run: "minikube start"

```

### 访问本地集群

前面也说过，通过minikube创建的集群可以当成真集群使用，所以自然是可以通过kubctl命令行工具来访问和控制，前面示例也展示过了。事实是在minikube start的时候创建好的ninikube的沟通上下文配置，官方解释如下。

````
The minikube start command creates a kubectl context called "minikube". This context contains the configuration to communicate with your Minikube cluster.

Minikube sets this context to default automatically, but if you need to switch back to it in the future, run:

kubectl config use-context minikube

Or pass the context on each command like this:

kubectl get pods --context=minikube
````

我们现在可以通过`kubectl config view`来查看minikube创建的集群的基本信息。

```bash
$ kubectl config view
apiVersion: v1
clusters:
- cluster:
    certificate-authority-data: DATA+OMITTED
    server: https://10.120.142.58:6443
  name: kubernetes
- cluster:
    certificate-authority: /home/learlee/.minikube/ca.crt
    server: https://172.17.0.4:8443
  name: minikube
contexts:
- context:
    cluster: kubernetes
    user: kubernetes-admin
  name: kubernetes-admin@kubernetes
- context:
    cluster: minikube
    user: minikube
  name: minikube
current-context: minikube
kind: Config
preferences: {}
users:
- name: kubernetes-admin
  user:
    client-certificate-data: REDACTED
    client-key-data: REDACTED
- name: minikube
  user:
    client-certificate: /home/learlee/.minikube/profiles/minikube/client.crt
    client-key: /home/learlee/.minikube/profiles/minikube/client.key
```

如果你想通过curl和wget命令访问API SERVER，可以使用以下几种方式开放API SERVER的REST API

1. 使用kubectl让API SERVER运行在代理模式下,这中方式是官方推荐的！
   
   ```shell
   $ kubectl proxy --port=8080 &
   $ Starting to serve on 127.0.0.1:8080
   #验证
   $ curl http://localhost:8080/api/
    {
      "kind": "APIVersions",
      "versions": [
        "v1"
      ],
      "serverAddressByClientCIDRs": [
        {
          "clientCIDR": "0.0.0.0/0",
          "serverAddress": "172.17.0.4:8443"
        }
      ]
    }
   ```
2. 通过鉴权Token访问API Server

   ```shell
   #获取server name 和 IP
    $ kubectl config view -o jsonpath='{"Cluster name\tSrver\n"}{range .clusters[*]}{.name}{"\t"}{.cluster.server}{"\n"}{end}'
    Cluster name	Server
    minikube	https://172.17.0.4:8443

   #创建token
    $ kubectl get secrets -o jsonpath="{.items[?(@.metadata.annotations['kubernetes\.io/service-account\.name']=='default')].data.token}"|base64 --decode
    eyJhbGciOiJSUzI1NiIsImtpZCI6IlFUMnpvLTkxWUhQLVcxdm1veGU0Rl9PNFFoVzMzVG5sV0Rha0VLN1hXX3cifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImRlZmF1bHQtdG9rZW4tNDg2dmMiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiZGVmYXVsdCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6Ijg4MWNjMWE5LTAzYTctNDc1Ni05ZDRlLThhOWZmZGUzZDBjNyIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OmRlZmF1bHQifQ.O1Ku_GkiQPWZUc78ReyjVFNp5mrYdHYx-oyHchmZHYkzrcXjGyV_HorMtCSVngh0b149aNx_CtPt-Zk8C6Hp92MojiMVHkbsnNg7CiU8Nf_kVHfwJ_r9fYk4kOigl7_YDxTFENpnZCeq4HuO6uGjqyKKhdOSx2_ZvJce5riXVMX9RRywg4CaBmWoM9dky9hT47aFa6q8_KFIYZRfKtSLnCGveyMCCfG1wC2K2EGE3kcLD1f7EvxcAxGqWxlnoOuhxOMR8cwDRRWKLmFhNHjR89GxzLrde4-X-Eniza-C4EtCg5zhWKn3gW30wW2ZxyXi1Wr-N6FdjnrMl2itc9KDMw
    
    #使用token访问API Server
    (base)  learlee@learleePC:~$ curl -X GET https://172.17.0.4:8443/api --header "Authorization: Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IlFUMnpvLTkxWUhQLVcxdm1veGU0Rl9PNFFoVzMzVG5sV0Rha0VLN1hXX3cifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImRlZmF1bHQtdG9rZW4tNDg2dmMiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiZGVmYXVsdCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6Ijg4MWNjMWE5LTAzYTctNDc1Ni05ZDRlLThhOWZmZGUzZDBjNyIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OmRlZmF1bHQifQ.O1Ku_GkiQPWZUc78ReyjVFNp5mrYdHYx-oyHchmZHYkzrcXjGyV_HorMtCSVngh0b149aNx_CtPt-Zk8C6Hp92MojiMVHkbsnNg7CiU8Nf_kVHfwJ_r9fYk4kOigl7_YDxTFENpnZCeq4HuO6uGjqyKKhdOSx2_ZvJce5riXVMX9RRywg4CaBmWoM9dky9hT47aFa6q8_KFIYZRfKtSLnCGveyMCCfG1wC2K2EGE3kcLD1f7EvxcAxGqWxlnoOuhxOMR8cwDRRWKLmFhNHjR89GxzLrde4-X-Eniza-C4EtCg5zhWKn3gW30wW2ZxyXi1Wr-N6FdjnrMl2itc9KDMw" --insecure
    {
      "kind": "APIVersions",
      "versions": [
        "v1"
      ],
      "serverAddressByClientCIDRs": [
        {
          "clientCIDR": "0.0.0.0/0",
          "serverAddress": "172.17.0.4:8443"
        }
      ]
    }
   ```

   上面我是把过程拆分了，下面是官方标准流程

   ```
  # Check all possible clusters, as your .KUBECONFIG may have multiple contexts:
  kubectl config view -o jsonpath='{"Cluster name\tServer\n"}{range .clusters[*]}{.name}{"\t"}{.cluster.server}{"\n"}{end}'

  # Select name of cluster you want to interact with from above output:
  export CLUSTER_NAME="some_server_name"

  # Point to the API server referring the cluster name
  APISERVER=$(kubectl config view -o jsonpath="{.clusters[?(@.name==\"$CLUSTER_NAME\")].cluster.server}")

  # Gets the token value
  TOKEN=$(kubectl get secrets -o jsonpath="{.items[?(@.metadata.annotations['kubernetes\.io/service-account\.name']=='default')].data.token}"|base64 --decode)

  # Explore the API with TOKEN
  curl -X GET $APISERVER/api --header "Authorization: Bearer $TOKEN" --insecure
   ```

3. 通过各个软件开发包访问，支持的开发语言：Java python js go
   
   ```python
    # 先pip install kubernetes安装库
    from kubernetes import client, config

    config.load_kube_config()

    v1=client.CoreV1Api()
    print("Listing pods with their IPs:")
    ret = v1.list_pod_for_all_namespaces(watch=False)
    for i in ret.items:
        print("%s\t%s\t%s" % (i.status.pod_ip, i.metadata.namespace, i.metadata.name))
    #输出如下
    /home/learlee/PycharmProjects/MyTest/.venv/bin/python /home/learlee/PycharmProjects/MyTest/KubeAccessTest.py
    Listing pods with their IPs:
    172.18.0.5	default	hello-minikube
    172.18.0.7	default	hello-minikube-5655c9d946-qrptw
    172.18.0.9	default	hello-minikube1-7cdf48f69f-hbd4l
    172.18.0.6	default	mymvc
    172.18.0.8	default	mytest-5d65d5ff4b-4b4j2
    172.18.0.2	kube-system	coredns-546565776c-nl74z
    172.17.0.4	kube-system	etcd-minikube
    172.17.0.4	kube-system	kube-apiserver-minikube
    172.17.0.4	kube-system	kube-controller-manager-minikube
    172.17.0.4	kube-system	kube-proxy-648ws
    172.17.0.4	kube-system	kube-scheduler-minikube
    172.17.0.4	kube-system	storage-provisioner
    172.18.0.4	kubernetes-dashboard	dashboard-metrics-scraper-dc6947fbf-fpqfp
    172.18.0.3	kubernetes-dashboard	kubernetes-dashboard-6dbb54fd95-l9md8

    Process finished with exit code 0

   ```


## 集群版本

国内需要通过--image-repository指定国内代理，以下是源代码的解释。

```
startCmd.Flags().String(imageRepository, "", "Alternative image repository to pull docker images from. This can be used when you have limited access to gcr.io. For Chinese mainland users, you may use local gcr.io mirrors such as registry.cn-hangzhou.aliyuncs.com/google_containers")
```
kubeadm init \
  --apiserver-advertise-address=192.168.202.132 \
  --image-repository registry.aliyuncs.com/google_containers \
  --kubernetes-version v1.16.0 \
  --service-cidr=10.1.0.0/16 \
  --pod-network-cidr=10.244.0.0/16


```bash
[master]$ ps -e | grep -i kube
  1688 pts/0    00:05:40 kubelet
  2019 ?        00:06:16 kube-apiserver
  2344 ?        00:00:09 kube-proxy
  7781 ?        00:03:56 kube-controller
  7835 ?        00:00:18 kube-scheduler

[node]$ ps -e | grep -i kube  
  1158 pts/0    00:00:02 kubelet
  1594 ?        00:00:00 kube-proxy

#Master上进行查看
[master]$ kubectl get nodes
NAME    STATUS   ROLES    AGE    VERSION
node1   Ready    master   170m   v1.14.0
node2   Ready    <none>   36s    v1.14.0
```

###
```bash
Example usage:

Create a two-machine cluster with one control-plane node
(which controls the cluster), and one worker node
(where your workloads, like Pods and Deployments run).

┌──────────────────────────────────────────────────────────┐
│ On the first machine:                                    │
├──────────────────────────────────────────────────────────┤
│ control-plane# kubeadm init                              │
└──────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────┐
│ On the second machine:                                   │
├──────────────────────────────────────────────────────────┤
│ worker # kubeadm join <arguments-returned-from-init>      │
└──────────────────────────────────────────────────────────┘

You can then repeat the second step on as many other machines as you like.
```

安装参考(https://blog.csdn.net/qq_14845119/article/details/83349471)
https://www.cnblogs.com/baylorqu/p/10754924.html
https://www.jianshu.com/p/2ba3d8c6678d

https://labs.play-with-k8s.com/


「Fan: Certificates:
mkdir $HOME/certs
cd $HOME/certs
openssl genrsa -out dashboard.key 2048
openssl rsa -in dashboard.key -out dashboard.key
openssl req -sha256 -new -key dashboard.key -out dashboard.csr -subj '/CN=localhost'
openssl x509 -req -sha256 -days 365 -in dashboard.csr -signkey dashboard.key -out dashboard.crt
kubectl -n kube-system create secret generic kubernetes-dashboard-certs --from-file=$HOME/certs

Deploy dashboard:
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/master/src/deploy/recommended/kubernetes-dashboard.yaml

Check if the replica set is fulfuilled:
kubectl -n kube-system get rs

Create a PSP:
kubectl -n kube-system create -f - <<EOF
apiVersion: extensions/v1beta1
kind: PodSecurityPolicy
metadata:
  name: dashboard
spec:
  privileged: false
  seLinux:
    rule: RunAsAny
  supplementalGroups:
    rule: RunAsAny
  runAsUser:
    rule: RunAsAny
  fsGroup:
    rule: RunAsAny
  volumes:
  - '*'
EOF

Create a role to allow use of the PSP:
kubectl -n kube-system create role psp:dashboard --verb=use --resource=podsecuritypolicy --resource-name=dashboard

Bind the role to kubernetes-dashboard service account:
kubectl -n kube-system create rolebinding kubernetes-dashboard-policy --role=psp:dashboard --serviceaccount=kube-system:kubernetes-dashboard
kubectl --as=system:serviceaccount:kube-system:kubernetes-dashboard -n kube-system auth can-i use podsecuritypolicy/dashboard

Expose dashboard service on a NodePort:
Edit the kubernetes-dashboard service and change the following options:
* spec.type from ClusterIP to NodePort
* spec.ports[0].nodePort from 32641 to whatever port you want it to be exposed on
kubectl -n kube-system edit service kubernetes-dashboard
kubectl -n kube-system get services」
