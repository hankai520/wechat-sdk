
package org.weixin4j.spring.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.weixin4j.MessageDispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 微信平台消息处理端点。
 *
 * @author hankai
 * @since 1.0.0
 */
public class WeixinEndpoint extends AbstractController {

  @Autowired
  private MessageDispatcher dispatcher;

  @Override
  protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response)
      throws Exception {
    if (request.getParameterMap().isEmpty()) {
      response.setStatus(400);
      return null;
    }
    dispatcher.dispatch(request, response);
    return null;
  }

}
