
package org.weixin4j.demo.springmvc.interceptor;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

import javax.servlet.http.HttpSession;

/**
 * TODO 缺少类型描述。
 *
 * @author hankai
 * @version TODO 缺少版本号
 * @since Jun 29, 2020 11:34:36 AM
 */
public class HttpHandshakeInterceptor implements HandshakeInterceptor {

  @Override
  public boolean beforeHandshake(final ServerHttpRequest request, final ServerHttpResponse response,
      final WebSocketHandler wsHandler, final Map<String, Object> attributes) throws Exception {
    if (request instanceof ServletServerHttpRequest) {
      final ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
      final HttpSession session = servletRequest.getServletRequest().getSession();
      // 只是示例如何拦截websocket
      System.out.println(session.getId());
    }
    return true;
  }

  @Override
  public void afterHandshake(final ServerHttpRequest request, final ServerHttpResponse response,
      final WebSocketHandler wsHandler, final Exception exception) {}

}
