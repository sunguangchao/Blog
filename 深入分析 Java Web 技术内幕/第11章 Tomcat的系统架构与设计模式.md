Tomcat总体设计
==========
[解析Tomcat内部结构和请求过程 ](http://www.cnblogs.com/zhouyuqin/p/5143121.html)

Tomcat的核心有两个部件：Container和Connector，一个Container可以对应多个Connector然后就形成了Service，有了Service就可以提供对外服务。但是Service还要有一个生存环境，这个环境就是Server，所以整个Tomcat的生命周期由Server控制。
