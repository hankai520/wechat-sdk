
package org.weixin4j.demo.springmvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.weixin4j.MessageDispatcher;
import org.weixin4j.WeixinBuilder;
import org.weixin4j.spring.config.BaseWeixin4jConfig;

/**
 * weixin4j配置类。
 *
 * @author hankai
 * @since 1.0.0
 */
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
