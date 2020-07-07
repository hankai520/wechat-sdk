
package org.weixin4j.spi.support;

import org.weixin4j.exception.WeixinException;
import org.weixin4j.model.message.InputMessage;
import org.weixin4j.model.message.MsgType;
import org.weixin4j.model.message.OutputMessage;
import org.weixin4j.model.message.normal.ImageInputMessage;
import org.weixin4j.model.message.normal.LinkInputMessage;
import org.weixin4j.model.message.normal.LocationInputMessage;
import org.weixin4j.model.message.normal.ShortVideoInputMessage;
import org.weixin4j.model.message.normal.TextInputMessage;
import org.weixin4j.model.message.normal.VideoInputMessage;
import org.weixin4j.model.message.normal.VoiceInputMessage;
import org.weixin4j.spi.IMessageHandler;
import org.weixin4j.spi.INormalMessageHandler;

/**
 * 抽象的普通消息处理器，内部以空逻辑实现INormalMessageHandler，这样子类可以专注于部分复杂消息的处理。
 * 具体子类处理哪些类型的消息，由 MessageDispatcher 的装配逻辑控制。
 *
 * @author hankai
 * @since 1.0.0
 */
public abstract class AbstractNormalMessageHandler implements IMessageHandler, INormalMessageHandler {

  @Override
  public String invoke(final InputMessage message) throws WeixinException {
    OutputMessage outputMsg = null;
    final MsgType msgType = MsgType.fromString(message.getMsgType());
    if (MsgType.Text == msgType) {
      outputMsg = this.textTypeMsg(message.toTextInputMessage());
    } else if (MsgType.Image == msgType) {
      outputMsg = this.imageTypeMsg(message.toImageInputMessage());
    } else if (MsgType.Voice == msgType) {
      outputMsg = this.voiceTypeMsg(message.toVoiceInputMessage());
    } else if (MsgType.Video == msgType) {
      outputMsg = this.videoTypeMsg(message.toVideoInputMessage());
    } else if (MsgType.ShortVideo == msgType) {
      outputMsg = this.shortvideoTypeMsg(message.toShortVideoInputMessage());
    } else if (MsgType.Location == msgType) {
      outputMsg = this.locationTypeMsg(message.toLocationInputMessage());
    } else if (MsgType.Link == msgType) {
      outputMsg = this.linkTypeMsg(message.toLinkInputMessage());
    } else if (MsgType.Music == msgType) {
      // TODO: 处理音乐消息
    } else if (MsgType.News == msgType) {
      // TODO: 处理图文消息
    }
    if (null != outputMsg) {
      outputMsg.setCreateTime(System.currentTimeMillis());
      outputMsg.setToUserName(message.getFromUserName());
      outputMsg.setFromUserName(message.getToUserName());
      return outputMsg.toXML();
    }
    return null;
  }

  @Override
  public OutputMessage textTypeMsg(final TextInputMessage msg) {
    return null;
  }

  @Override
  public OutputMessage imageTypeMsg(final ImageInputMessage msg) {
    return null;
  }

  @Override
  public OutputMessage voiceTypeMsg(final VoiceInputMessage msg) {
    return null;
  }

  @Override
  public OutputMessage videoTypeMsg(final VideoInputMessage msg) {
    return null;
  }

  @Override
  public OutputMessage shortvideoTypeMsg(final ShortVideoInputMessage msg) {
    return null;
  }

  @Override
  public OutputMessage locationTypeMsg(final LocationInputMessage msg) {
    return null;
  }

  @Override
  public OutputMessage linkTypeMsg(final LinkInputMessage msg) {
    return null;
  }

}
