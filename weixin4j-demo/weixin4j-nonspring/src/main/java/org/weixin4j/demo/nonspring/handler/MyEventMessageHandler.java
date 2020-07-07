
package org.weixin4j.demo.nonspring.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weixin4j.demo.nonspring.servlet.UserServlet;
import org.weixin4j.model.message.OutputMessage;
import org.weixin4j.model.message.event.QrsceneScanEventMessage;
import org.weixin4j.model.message.event.SubscribeEventMessage;
import org.weixin4j.spi.support.AbstractEventMessageHandler;

import javax.servlet.http.HttpSession;

/**
 * 自定义的微信事件处理器，根据需要覆盖对应方法实现特定事件的处理，其余事件会被忽略而不报错。
 *
 * @author hankai
 * @since 1.0.0
 */
public class MyEventMessageHandler extends AbstractEventMessageHandler {

  private static final Logger logger = LoggerFactory.getLogger(MyEventMessageHandler.class);

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
      // 3. 通过事件框架发送事件到servlet，使用wesocket推送登录结果到前端；或者利用会话作为中介存储用户认证信息
      final HttpSession session = UserServlet.sessions.get(msg.getTicket());
      if (null != session) {
        session.setAttribute("openid", msg.getFromUserName());
      }
    }
    return null;
  }
}
