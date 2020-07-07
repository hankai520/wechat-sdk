
package org.weixin4j.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.weixin4j.MessageDispatcher;
import org.weixin4j.Weixin;
import org.weixin4j.WeixinBuilder;
import org.weixin4j.model.message.MsgType;
import org.weixin4j.spi.support.AbstractEventMessageHandler;
import org.weixin4j.spi.support.AbstractNormalMessageHandler;
import org.weixin4j.spring.endpoint.WeixinEndpoint;

import java.util.Properties;

/**
 * 微信配置类。
 *
 * @author hankai
 * @since 1.0.0
 */
public abstract class BaseWeixin4jConfig {

  @Autowired
  private AbstractEventMessageHandler eventMessageHandler;

  @Autowired
  private AbstractNormalMessageHandler normalMessageHandler;

  /**
   * 获取配置文件路径。子类覆盖此方法可返回指定配置文件，返回null表示使用默认的类路径下的weixin4j.properties。
   *
   * @return 配置文件路径
   */
  protected String getWeixinConfigFile() {
    // null 表示使用 weixin4j.properties
    return null;
  }

  /**
   * 自定义微信对接细节配置。
   *
   * @param builder
   */
  protected void configure(final WeixinBuilder builder) {
    // do nothing by default
  }

  /**
   * 设置响应微信平台请求的端点URL。
   *
   * @return url
   */
  protected abstract String weixinEndpointUrl();

  @Bean
  public Weixin weixin() {
    final String path = getWeixinConfigFile();
    final WeixinBuilder builder = new WeixinBuilder(path);
    configure(builder);
    final Weixin weixin = builder.build();
    return weixin;
  }

  @Bean
  public MessageDispatcher messageDispatcher() {
    final MessageDispatcher md = new MessageDispatcher();
    md.addHandler(MsgType.Event, eventMessageHandler);

    md.addHandler(MsgType.Image, normalMessageHandler);
    md.addHandler(MsgType.Link, normalMessageHandler);
    md.addHandler(MsgType.Location, normalMessageHandler);
    md.addHandler(MsgType.Music, normalMessageHandler);
    md.addHandler(MsgType.News, normalMessageHandler);
    md.addHandler(MsgType.ShortVideo, normalMessageHandler);
    md.addHandler(MsgType.Text, normalMessageHandler);
    md.addHandler(MsgType.Video, normalMessageHandler);
    md.addHandler(MsgType.Voice, normalMessageHandler);
    return md;
  }

  @Bean
  public WeixinEndpoint weixinEndpoint() {
    return new WeixinEndpoint();
  }

  @Bean
  public SimpleUrlHandlerMapping weixinEndpointMapping(final WeixinEndpoint endpoint) {
    final SimpleUrlHandlerMapping urlMapping = new SimpleUrlHandlerMapping();
    urlMapping.setOrder(0);// 必须设置优先级高于静态资源，否则会出现404
    final Properties mappings = new Properties();
    mappings.put(weixinEndpointUrl(), endpoint);
    urlMapping.setMappings(mappings);
    return urlMapping;
  }
}
