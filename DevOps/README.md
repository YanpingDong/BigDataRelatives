# DevOps

CI: 持续集成 
- code--build--test

CD（Delivery）:持续交付
- 监控提交代码
- 打包发送到产品库
  
CD(Deployment)：持续部署
- 监控入库产品
- 将产品库中打包的按要求部署上线
  
# zabbix



# [ansible](Ansible/README.md)

Ansible 是一个模型驱动的配置管理器，支持多节点发布、远程任务执行。默认使用 SSH 进行远程连接。无需在被管理节点上安装附加软件，可使用各种编程语言进行扩展。

1. Python 阵营的配置管理器
2. 不用帮每台机器 (instance) 预安装 agent ，只要有 SSH 和 Python 就可以闯天下！ssh能连接到目标机器就可用ansible操作！
3. 4 大主流的配置管理器(Puppet, SaltStack, Chef, Ansible) 中， Ansible 是最容易上手，且马上就可以用的工具。
