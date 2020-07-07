
package org.weixin4j.demo.springmvc.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.weixin4j.model.message.OutputMessage;
import org.weixin4j.model.message.normal.TextInputMessage;
import org.weixin4j.model.message.output.TextOutputMessage;
import org.weixin4j.spi.support.AbstractNormalMessageHandler;

/**
 * 自定微信普通消息处理器，根据需要覆盖特定消息方法即可，其余消息会被忽略而不报错。
 *
 * @author hankai
 * @since 1.0.0
 */
@Component
public class MyNormalMessageHandler extends AbstractNormalMessageHandler {

  private static final Logger logger = LoggerFactory.getLogger(MyNormalMessageHandler.class);

  @Override
  public OutputMessage textTypeMsg(final TextInputMessage msg) {
    logger.info("收到消息：" + msg.getContent());
    return new TextOutputMessage("收到了你发的：" + msg.getContent());
  }

}
