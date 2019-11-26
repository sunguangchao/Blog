RefreshConfigFromDBToZK
======
将数据库中的配置更新到zk上，每隔30分钟执行一次。

Job.getSysUuid() = JobSystem.getUuid()

1. 获取`List<Job>`所有job信息
2. 获取`List<JobSystem>`所有jobSystem信息系统表信息
3. 循环遍历Job，将Job信息转化为JobSettings(包括分片数，cron表达式，最小运行时间，最大运行时间，是否启用失效转移等)
4. 获取作业配置API对象，参数：jobSystem.getNamespace()，然后获取作业设置，如果获取的作业设置与前面转化的JobSettings不一致，需要更新作业设置。
5. 注意JobSettings对象equals方法的重写(hashCode方法是不是也要重写？)

注意：这里没有这个job也是可以的，主要是为了补偿，万一持久化zk信息的时候失败了，从数据库里面读取配置可以进行补偿

作业如何下线：offlineJob
========
入参：jobUUID
1. jobUUID找对应的job信息
2. job.getSysUuid找JobSystem（jobSystem.getNamespace()）
3. jobStatus改为2-已下下线，并记录操作日志
4. 禁用操作作业api对象
5. 更新作业设置为已下线
   1. `/${job_name}/servers/${ip}`内容设置为DISABLE
   2. 只有jobName参数的时候，将该job下`/${job_name}/servers`所有ip填充为DISABLE
   3. 只有serverIP参数的时候，遍历所有job的`/${job_name}/servers`找到和该serverIP对应的ip，value填充为DISABLE


```java
jobAPIService.getJobOperatorAPI(jobSystem.getNamespace()).disable(job.getJobId(), null);
job.setJobStatus(JobStatusEnum.offline.key);     jobAPIService.getJobSettingsAPI(jobSystem.getNamespace()).updateJobSettings(transferJobToJobSettings(job));
```

触发job
======
入参：jobUUID
1. jobUUID找对应的job信息
2. job.getSysUuid找JobSystem（jobSystem.getNamespace()）
3. 获取分片当前状态(Job,JobSystem.getNameSpace()) 
   1. Job信息中获取总分片数，获取每个分片的运行状态(结果以map返回)
   2. `/${name_space}/${job_name}/sharding/${item}/running`
4. 如果该job已经处于运行状态，返回不能触发
5. 如果没有处于运行状态，先删除平滑停止节点
   1. `/${name_space}/${job_name}/servers/{ip}/smoothstop`
6. 根据jobId触发job
   1. 持久化节点：`/${name_space}/${job_name}/instances/TRIGGER`

```java
        jobAPIService.getJobOperatorAPI(jobSystem.getNamespace()).removeSmoothStop(job.getJobId());
jobAPIService.getJobOperatorAPI(jobSystem.getNamespace()).trigger(job.getJobId());
```



JobEventBus

```java
    /**
     * 分布式作业执行事件监听执行.
     *
     * @param jobExecuteEvent 分布式作业执行事件
     */
    @Subscribe
    @AllowConcurrentEvents
    void listen(JobExecuteEvent jobExecuteEvent);
```


基本作业的命名空间解析器
=======
```java
	//AbstractJobBeanDefinitionParser.java
    @Override
    protected AbstractBeanDefinition parseInternal(final Element element, final ParserContext parserContext) {
        log.info("dhjobs--parseInternal " + element.getAttribute(ID_ATTRIBUTE));
        //创建一个新的BeanDefinitionBuilder:
      	//beanClass="com.plg.dhjobs.job.lite.spring.api.SpringJobScheduler"
        BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(SpringJobScheduler.class);
        //Set the init method for this definition.
        //只是设置，并没有执行，JobScheduler的init()方法
        factory.setInitMethodName("init");
        //TODO 抽象子类
        jobSettingsAPI = JobAPIFactory.createJobSettingsAPI(
                PropertiesUtil.getStringValue("zookeeper.subNamespace"),
                PropertiesUtil.getStringValue("zookeeper.address"),
                "dhjobs",
                "");
		//获取jobID(本地)
        String jobID = element.getAttribute(ID_ATTRIBUTE);
        //根据jobID到zk上获取在后台增加的配置
        JobSettings jobSettings = jobSettingsAPI.loadJobSettings(element.getAttribute("id"));
        if (jobSettings == null) {
            throw new RuntimeException(String.format("can not found jobid:%s in dhjobs system", jobID));
        }
        log.info("dhjobs--parseInternal 2222");        
        //Add a reference to a named bean as a constructor arg.
        //其实就是SpringJobScheduler构造函数中的第一个参数:ElasticJob,下面的类就是实现了ElasticJob接口
        //jobClass="com.plg.lawjudge.provider.task.PayStatSyncJob"
        factory.addConstructorArgValue(BeanDefinitionBuilder.rootBeanDefinition(jobSettings.getJobClass()).getBeanDefinition());
        //周到一个regCenter注解的对象
        factory.addConstructorArgReference("regCenter");
        //LiteJobConfiguration的BeanDefinition
        factory.addConstructorArgValue(createLiteJobConfiguration(parserContext, jobSettings));
        //JobEventRdbConfiguration的BeanDefinition
        BeanDefinition jobEventConfig = createJobEventConfig();
        if (null != jobEventConfig) {
            factory.addConstructorArgValue(jobEventConfig);
        }
        factory.addConstructorArgValue(DataSourceUtil.getDruidDataSource());
        factory.addConstructorArgValue(createJobListeners(jobSettings));
        log.info("dhjobs--parseInternal 3333");
        return factory.getBeanDefinition();
    }
```

主要使用了Spring动态注册Bean

1. 创建作业配置API对象
2. 读取job.xml中的配置，根据jobId获取JobSettings，如果获取不到，抛异常
3. 将配置放入新建的BeanDefinitionBuilder
4. 调用SpringJobScheduler#init方法，因为SpringJobScheduler继承了JobScheduler



```java
//JobScheduler.java 
/**
     * 初始化作业.
     */
    public void init() {
        log.info("dhjobs--JobScheduler init 111,jobName:"+liteJobConfig.getJobName());
        //1.持久化/jobName/config 信息到zk
        LiteJobConfiguration liteJobConfigFromRegCenter = liteJobConfig;
        //2.在jobreg上设置作业分片数
        JobRegistry.getInstance().setCurrentShardingTotalCount(liteJobConfigFromRegCenter.getJobName(), liteJobConfigFromRegCenter.getTypeConfig().getCoreConfig().getShardingTotalCount());
        log.info("dhjobs--JobScheduler init 222,jobName:" + liteJobConfig.getJobName());
        //3.初始化quartz的实例和配置
        JobScheduleController jobScheduleController = new JobScheduleController(
                createScheduler(), createJobDetail(), liteJobConfigFromRegCenter.getJobName());
        log.info("dhjobs--JobScheduler init 333,jobName:"+liteJobConfig.getJobName());
        JobRegistry.getInstance().registerJob(liteJobConfigFromRegCenter.getJobName(), jobScheduleController, regCenter);
        //4.注册启动信息，ElasticJob的任务服务器的启动流程就在这里定义
        log.info("dhjobs--JobScheduler init 444,jobName:"+liteJobConfig.getJobName());
        schedulerFacade.registerStartUpInfo(liteJobConfigFromRegCenter.getTypeConfig().getCoreConfig().getJobStatus() == 1 ? true : false);

        log.info("dhjobs--JobScheduler init 555,jobName:" + liteJobConfig.getJobName());
        jobScheduleController.scheduleJob(liteJobConfigFromRegCenter.getTypeConfig().getCoreConfig().getCron());
        log.info("dhjobs--JobScheduler init 666,jobName:"+liteJobConfig.getJobName());
    }

```






createJobSettingsAPI
```java
/**
 * 创建注册中心.
 *
 * @param connectString 注册中心连接字符串
 * @param namespace 注册中心命名空间
 * @param digest 注册中心凭证
 * @return 注册中心对象
 */
public static CoordinatorRegistryCenter createCoordinatorRegistryCenter(final String connectString, final String namespace, String digest) {
    Hasher hasher =  Hashing.md5().newHasher().putString(connectString, Charsets.UTF_8).putString(namespace, Charsets.UTF_8);
    if (StringUtils.isNotEmpty(digest)) {
        hasher.putString(digest, Charsets.UTF_8);
    }
    HashCode hashCode = hasher.hash();
    CoordinatorRegistryCenter result = REG_CENTER_REGISTRY.get(hashCode);
    if (null != result) {
        return result;
    }
    ZookeeperConfiguration zkConfig = new ZookeeperConfiguration(connectString, namespace);
    if (StringUtils.isNotEmpty(digest)) {
        zkConfig.setDigest(digest);
    }
    result = new ZookeeperRegistryCenter(zkConfig);
    result.init();
    REG_CENTER_REGISTRY.put(hashCode, result);
    return result;
}
```