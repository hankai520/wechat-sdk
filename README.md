wechat-sdk
==========

### 项目介绍

本项目基于weixin4j（<https://github.com/ansitech/weixin4j>）二次开发（感谢原作者杨启盛作出的贡献），为了便于发布到maven中央库，调整了pom.xml中的groupId。本项目改进如下:

-   整合了weixin4j分散的几个仓库代码

-   简化了不必要的设计模式应用，简化了代码

-   简化了SDK核心公开API，隐藏了部分细节

-   基于微信平台的要求进行了适当优化（如限时处理微信消息和事件

-   整合了示例代码，针对spring和非spring项目给出对接示例代码

 

### 测试说明

以下示例可通过免费申请微信公众平台接口测试账号运行。测试平台地址：<https://mp.weixin.qq.com/debug/cgi-bin/sandbox?t=sandbox/login>

 

在局域网测试并且具备连接互联网条件的，使用免费内网穿透工具ngrok获得互联网域名：

<https://ngrok.com>

 

### 编译

克隆SDK仓库后，在根目录执行 mvn clean
install。此命令会将SDK安装到本地maven仓库，适用于单人开发或简单测试。需要发布到nexus私服的，可以自行配置本SDK的pom.xml文件自行发布到私服。本项目也已经发布到中央仓库，按对接步骤添加依赖可快速开始使用。

 

### 对接

非Spring项目对接，请参见 weixin4j-nonspring 示例工程源码。

 

基于Spring (mvc, boot) 开发的项目接入SDK步骤:

1.  复制 weixin4j-core/src/main/resources/weixin4j.properties
    到项目src/main/resources下

2.  项目pom.xml中添加依赖如下：

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
<dependency>
    <groupId>ren.hankai</groupId>
    <artifactId>weixin4j-spring</artifactId>
    <version>1.0.0</version>
</dependency>
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

1.  创建weixin4j配置类

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
@Configuration
public class Weixin4jConfig extends BaseWeixin4jConfig {
  @Override
  protected String weixinEndpointUrl() {
    // 指定响应微信平台消息的servlet端点 url
    return "/hooks/weixin";
  }
  @Override
  protected String getWeixinConfigFile() {
    // 返回空表示使用类路径中的weixin4j.properties
    return null;
    // 返回路径表示使用自定义的配置文件
    // return "/usr/share/weixin4j/xxx.properties";
  }
  @Override
  protected void configure(final WeixinBuilder builder) {
    // builder.oauth("appId", "appSecret")
    // .tokenLoader(tokenLoader)
    // .ticketLoader(ticketLoader);
  }
  @Bean
  @Override
  public MessageDispatcher messageDispatcher() {
    final MessageDispatcher dispatcher = super.messageDispatcher();
    // 自定义消息处理器，用来处理专一消息
    // dispatcher.addHandler(msgType, handler);
    return dispatcher;
  }
}
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

1.  实现微信消息处理器

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//自定微信普通消息处理器，根据需要覆盖特定消息方法即可，其余消息会被忽略而不报错
@Component
public class MyNormalMessageHandler extends AbstractNormalMessageHandler {
  private static final Logger logger = LoggerFactory.getLogger(MyNormalMessageHandler.class);
  @Override
  public OutputMessage textTypeMsg(final TextInputMessage msg) {
    logger.info("收到消息：" + msg.getContent());
    return new TextOutputMessage("收到了你发的：" + msg.getContent());
  }
}
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

1.  实现微信事件处理器

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//自定义的微信事件处理器，根据需要覆盖对应方法实现特定事件的处理，其余事件会被忽略而不报错。
@Component
public class MyEventMessageHandler extends AbstractEventMessageHandler {
  private static final Logger logger = LoggerFactory.getLogger(MyEventMessageHandler.class);
  // @Autowired
  // private Weixin weixin; //注入微信对接核心对象

  @Override
  public OutputMessage subscribe(final SubscribeEventMessage msg) {
    // msg.getFromUserName() //取得关注者的微信账号openid
    // try {
    // 通过微信接口获取用户资料（头像、昵称、城市、语言等）
    // User weixinUser = weixin.user().info(msg.getFromUserName());
    // } catch (WeixinException ex) {
    // }
    System.out.println("有人关注：openid=" + msg.getFromUserName());
    return null;
  }

  @Override
  public OutputMessage qrsceneScan(final QrsceneScanEventMessage msg) {
    if ("login".equals(msg.getEventKey())) {// 根据二维码中的参数判定当前用户动作为"登录"
      // 1. msg.getCreateTime() 根据创建时间判断登录操作是否过期
      // 2. 根据openid验证登录的微信用户是否是本系统用户
      logger.info("有人扫码登录: openid=" + msg.getFromUserName());
      // 4. 通过websocket推送登录成功或失败的消息
      simpMessagingTemplate.convertAndSend("/topic/login.success", msg.getFromUserName());
    }
    return null;
  }
}
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

 

### 实现微信扫码登录网站

weixin4j-springmvc和weixin4j-nonspring示例工程均实现了微信扫码登录以及登录事件的处理逻辑，可以参考借鉴。工作过程如下：

1.  匿名用户访问网站，跳转至登录页面

2.  调用微信接口生成带参二维码（场景值自定义为"login"）

3.  渲染微信带参二维码到登录页面，将二维码对应ticketid与当前匿名用户关联

4.  登录页面建立websocket等待网站推送登录结果

5.  匿名用户用微信扫码

6.  微信公众平台推送扫码事件到网站服务器

7.  获取事件key，判断是否是步骤2中的场景值 "login"，是则是登录事件

8.  根据事件中的用户openid，调用微信接口获取用户资料，完成隐性注册流程

9.  根据事件ticketid获取用户会话，更新会话信息，标记为已登录

10. 网站后端通过websocket推送消息到浏览器端，告知登录成功

11. 浏览器端跳转至网站首页

 

### 调用微信公众平台接口

​SDK核心API介绍如下：

org.weixin4j.Weixin类：

此类为SDK的入口类，是调用微信接口的总入口，基于weixin4j-spring开发，可直接注入此类型bean。使用示例：

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
...
@Autowired
private Weixin weixin;
...
//创建带参二维码
Qrcode code = weixin.qrcode().create(QrcodeType.QR_STR_SCENE, "login", 60 * 5);
//获取微信用户资料
User weixinUser = weixin.user().info(openid)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

 

org.weixin4j.WeixinBuilder类：

此类为Weixin类实例的构建器，用于干预Weixin实例构建过程，实现自定义部件，少数情况需下需要使用该类，一般对开发者透明。使用示例：

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//支持方法链风格，参见 BaseWeixin4jConfig.java#58
Weixin weixin = builder.oauth(appId, appSecret)
    .tokenLoader(tokenLoader)
    .ticketLoader(ticketLoader)
    .build();
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

 

org.weixin4j.MessageDispatcher类：

此类为微信消息分发器，本身是一个Servlet，扮演微信消息处理端点，响应微信平台发送到开发者服务器的请求。它是对接微信平台的关键，内部维护一个Map登记各类消息的处理器，开发者根据需要注册自定义的处理器。详见
BaseWeixin4jConfig.java\#67

 

org.weixin4j.spi.support.AbstractEventMessageHandler 类：

此抽象类完成微信事件的分拣，进一步将事件分发到合适的接口方法实现中，降低开发者实现
IEventMessageHandler 接口的成本，开发者可以专注于处理需要关心的事件。

 

org.weixin4j.spi.support.AbstractNormalMessageHandler 类：

此抽象类完成微信普通消息的分拣，进一步将不同小类的消息分发到合适的接口方法实现中，降低开发者实现

INormalMessageHandler 接口的成本，开发者可以专注于处理需要关心的消息。
