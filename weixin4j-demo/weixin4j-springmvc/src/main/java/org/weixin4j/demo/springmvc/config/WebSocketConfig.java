
package org.weixin4j.demo.springmvc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.weixin4j.demo.springmvc.interceptor.HttpHandshakeInterceptor;

/**
 * TODO 缺少类型描述。
 *
 * @author hankai
 * @version TODO 缺少版本号
 * @since Jun 29, 2020 11:27:41 AM
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void registerStompEndpoints(final StompEndpointRegistry registry) {
    registry.addEndpoint("/sock")
        .addInterceptors(new HttpHandshakeInterceptor())
        .withSockJS();
  }

  @Override
  public void configureMessageBroker(final MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic/");
    registry.setApplicationDestinationPrefixes("/app");
  }

}
