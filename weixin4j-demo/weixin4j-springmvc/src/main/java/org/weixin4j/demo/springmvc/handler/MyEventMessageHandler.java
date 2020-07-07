
package org.weixin4j.demo.springmvc.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.weixin4j.model.message.OutputMessage;
import org.weixin4j.model.message.event.QrsceneScanEventMessage;
import org.weixin4j.model.message.event.SubscribeEventMessage;
import org.weixin4j.spi.support.AbstractEventMessageHandler;

/**
 * 自定义的微信事件处理器，根据需要覆盖对应方法实现特定事件的处理，其余事件会被忽略而不报错。
 *
 * @author hankai
 * @since 1.0.0
 */
@Component
public class MyEventMessageHandler extends AbstractEventMessageHandler {

  private static final Logger logger = LoggerFactory.getLogger(MyEventMessageHandler.class);

  // @Autowired
  // private Weixin weixin; //注入微信对接核心对象

  @Autowired
  private SimpMessagingTemplate simpMessagingTemplate;

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
