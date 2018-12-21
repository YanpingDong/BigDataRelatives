# BigDataRelatives

3、集群升级维护时手动进入安全模式吗，命令如下

hadoop dfsadmin -safemode enter


那么如何退出安全模式呢？

使用命令

hadoop dfsadmin -safemode leave

~/mybin/hadoop-2.9.2/bin/hadoop jar /home/tra/mybin/hadoop-2.9.2/share/hadoop/yarn/hadoop-yarn-applications-distributedshell-2.9.2.jar org.apache.hadoop.yarn.applications.distributedshell.Client -jar /home/tra/mybin/hadoop-2.9.2/share/hadoop/yarn/hadoop-yarn-applications-distributedshell-2.9.2.jar -shell_command 'ls'


tra@tra:~/mybin/hadoop-2.9.2$ ~/mybin/hadoop-2.9.2/bin/hadoop jar /home/tra/mybin/hadoop-2.9.2/share/hadoop/yarn/hadoop-yarn-applications-distributedshell-2.9.2.jar org.apache.hadoop.yarn.applications.distributedshell.Client -jar /home/tra/mybin/hadoop-2.9.2/share/hadoop/yarn/hadoop-yarn-applications-distributedshell-2.9.2.jar -shell_command 'ls'
18/12/21 16:34:48 INFO distributedshell.Client: Initializing Client
18/12/21 16:34:48 INFO distributedshell.Client: Running Client
18/12/21 16:34:49 INFO client.RMProxy: Connecting to ResourceManager at /0.0.0.0:8032
18/12/21 16:34:49 INFO distributedshell.Client: Got Cluster metric info from ASM, numNodeManagers=1
18/12/21 16:34:49 INFO distributedshell.Client: Got Cluster node info from ASM
18/12/21 16:34:49 INFO distributedshell.Client: Got node report from ASM for, nodeId=tra:42749, nodeAddress=tra:8042, nodeRackName=/default-rack, nodeNumContainers=0
18/12/21 16:34:49 INFO distributedshell.Client: Queue info, queueName=default, queueCurrentCapacity=0.0, queueMaxCapacity=1.0, queueApplicationCount=0, queueChildQueueCount=0
18/12/21 16:34:49 INFO distributedshell.Client: User ACL Info for Queue, queueName=root, userAcl=SUBMIT_APPLICATIONS
18/12/21 16:34:49 INFO distributedshell.Client: User ACL Info for Queue, queueName=root, userAcl=ADMINISTER_QUEUE
18/12/21 16:34:49 INFO distributedshell.Client: User ACL Info for Queue, queueName=default, userAcl=SUBMIT_APPLICATIONS
18/12/21 16:34:49 INFO distributedshell.Client: User ACL Info for Queue, queueName=default, userAcl=ADMINISTER_QUEUE
18/12/21 16:34:49 INFO distributedshell.Client: Max mem capability of resources in this cluster 8192
18/12/21 16:34:49 INFO distributedshell.Client: Max virtual cores capability of resources in this cluster 4
18/12/21 16:34:49 INFO distributedshell.Client: Copy App Master jar from local filesystem and add to local environment
18/12/21 16:34:50 INFO distributedshell.Client: Set the environment for the application master
18/12/21 16:34:50 INFO distributedshell.Client: Setting up app master command
18/12/21 16:34:50 INFO distributedshell.Client: Completed setting up app master command "{{JAVA_HOME}}/bin/java" -Xmx100m org.apache.hadoop.yarn.applications.distributedshell.ApplicationMaster --container_memory 10 --container_vcores 1 --num_containers 1 --priority 0 1><LOG_DIR>/AppMaster.stdout 2><LOG_DIR>/AppMaster.stderr 
18/12/21 16:34:50 INFO distributedshell.Client: Submitting application to ASM
18/12/21 16:34:50 INFO impl.YarnClientImpl: Submitted application application_1545380789810_0003
18/12/21 16:34:51 INFO distributedshell.Client: Got application report from ASM for, appId=3, clientToAMToken=null, appDiagnostics=AM container is launched, waiting for AM container to Register with RM, appMasterHost=N/A, appQueue=default, appMasterRpcPort=-1, appStartTime=1545381290526, yarnAppState=ACCEPTED, distributedFinalState=UNDEFINED, appTrackingUrl=http://tra:8088/proxy/application_1545380789810_0003/, appUser=tra
18/12/21 16:34:52 INFO distributedshell.Client: Got application report from ASM for, appId=3, clientToAMToken=null, appDiagnostics=AM container is launched, waiting for AM container to Register with RM, appMasterHost=N/A, appQueue=default, appMasterRpcPort=-1, appStartTime=1545381290526, yarnAppState=ACCEPTED, distributedFinalState=UNDEFINED, appTrackingUrl=http://tra:8088/proxy/application_1545380789810_0003/, appUser=tra
18/12/21 16:34:53 INFO distributedshell.Client: Got application report from ASM for, appId=3, clientToAMToken=null, appDiagnostics=, appMasterHost=tra/127.0.1.1, appQueue=default, appMasterRpcPort=-1, appStartTime=1545381290526, yarnAppState=RUNNING, distributedFinalState=UNDEFINED, appTrackingUrl=http://tra:8088/proxy/application_1545380789810_0003/, appUser=tra
18/12/21 16:34:54 INFO distributedshell.Client: Got application report from ASM for, appId=3, clientToAMToken=null, appDiagnostics=, appMasterHost=tra/127.0.1.1, appQueue=default, appMasterRpcPort=-1, appStartTime=1545381290526, yarnAppState=RUNNING, distributedFinalState=UNDEFINED, appTrackingUrl=http://tra:8088/proxy/application_1545380789810_0003/, appUser=tra
18/12/21 16:34:55 INFO distributedshell.Client: Got application report from ASM for, appId=3, clientToAMToken=null, appDiagnostics=, appMasterHost=tra/127.0.1.1, appQueue=default, appMasterRpcPort=-1, appStartTime=1545381290526, yarnAppState=RUNNING, distributedFinalState=UNDEFINED, appTrackingUrl=http://tra:8088/proxy/application_1545380789810_0003/, appUser=tra
18/12/21 16:34:56 INFO distributedshell.Client: Got application report from ASM for, appId=3, clientToAMToken=null, appDiagnostics=, appMasterHost=tra/127.0.1.1, appQueue=default, appMasterRpcPort=-1, appStartTime=1545381290526, yarnAppState=FINISHED, distributedFinalState=SUCCEEDED, appTrackingUrl=http://tra:8088/proxy/application_1545380789810_0003/, appUser=tra
18/12/21 16:34:56 INFO distributedshell.Client: Application has completed successfully. Breaking monitoring loop
18/12/21 16:34:56 INFO distributedshell.Client: Application completed successfully

在http://localhost:8088/cluster也能看到结果


