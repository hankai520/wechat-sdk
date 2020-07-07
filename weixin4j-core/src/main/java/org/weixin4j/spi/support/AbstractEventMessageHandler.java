
package org.weixin4j.spi.support;

import org.weixin4j.exception.WeixinException;
import org.weixin4j.model.message.EventType;
import org.weixin4j.model.message.InputMessage;
import org.weixin4j.model.message.OutputMessage;
import org.weixin4j.model.message.event.ClickEventMessage;
import org.weixin4j.model.message.event.LocationEventMessage;
import org.weixin4j.model.message.event.LocationSelectEventMessage;
import org.weixin4j.model.message.event.PicPhotoOrAlbumEventMessage;
import org.weixin4j.model.message.event.PicSysPhotoEventMessage;
import org.weixin4j.model.message.event.PicWeixinEventMessage;
import org.weixin4j.model.message.event.QrsceneScanEventMessage;
import org.weixin4j.model.message.event.QrsceneSubscribeEventMessage;
import org.weixin4j.model.message.event.ScanCodePushEventMessage;
import org.weixin4j.model.message.event.ScanCodeWaitMsgEventMessage;
import org.weixin4j.model.message.event.SubscribeEventMessage;
import org.weixin4j.model.message.event.UnSubscribeEventMessage;
import org.weixin4j.model.message.event.ViewEventMessage;
import org.weixin4j.spi.IEventMessageHandler;
import org.weixin4j.spi.IMessageHandler;

/**
 * 抽象的事件消息处理器，开发者可继承此类实现事件细分。内部以空逻辑实现 IEventMessageHandler，降低子类负担，子类
 * 可专注于需要处理的事件。
 *
 * @author hankai
 * @since 1.0.0
 */
public abstract class AbstractEventMessageHandler implements IMessageHandler, IEventMessageHandler {

  @Override
  public String invoke(final InputMessage message) throws WeixinException {
    OutputMessage outputMsg = null;
    final EventType eventType = EventType.fromString(message.getEvent());
    if (EventType.Click == eventType) {
      outputMsg = this.click(message.toClickEventMessage());
    } else if (EventType.View == eventType) {
      outputMsg = this.view(message.toViewEventMessage());
    } else if (EventType.Subscribe == eventType) {
      // 获取事件KEY值，判断是否关注
      final String eventKey = message.getEventKey();
      if ((eventKey != null) && eventKey.startsWith("qrscene_")) {
        // 用户未关注时，进行关注后的事件推送
        outputMsg = this.qrsceneSubscribe(message.toQrsceneSubscribeEventMessage());
      } else {
        // 关注事件
        outputMsg = this.subscribe(message.toSubscribeEventMessage());
      }
    } else if (EventType.Unsubscribe == eventType) {
      outputMsg = this.unSubscribe(message.toUnSubscribeEventMessage());
    } else if (EventType.Scan == eventType) {
      outputMsg = this.qrsceneScan(message.toQrsceneScanEventMessage());
    } else if (EventType.Location == eventType) {
      outputMsg = this.location(message.toLocationEventMessage());
    } else if (EventType.Scancode_Push == eventType) {
      outputMsg = this.scanCodePush(message.toScanCodePushEventMessage());
    } else if (EventType.Scancode_Waitmsg == eventType) {
      outputMsg = this.scanCodeWaitMsg(message.toScanCodeWaitMsgEventMessage());
    } else if (EventType.Pic_Sysphoto == eventType) {
      outputMsg = this.picSysPhoto(message.toPicSysPhotoEventMessage());
    } else if (EventType.Pic_Photo_OR_Album == eventType) {
      outputMsg = this.picPhotoOrAlbum(message.toPicPhotoOrAlbumEventMessage());
    } else if (EventType.Pic_Weixin == eventType) {
      outputMsg = this.picWeixin(message.toPicWeixinEventMessage());
    } else if (EventType.Location_Select == eventType) {
      outputMsg = this.locationSelect(message.toLocationSelectEventMessage());
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
  public OutputMessage subscribe(final SubscribeEventMessage msg) {
    return null;
  }

  @Override
  public OutputMessage unSubscribe(final UnSubscribeEventMessage msg) {
    return null;
  }

  @Override
  public OutputMessage qrsceneSubscribe(final QrsceneSubscribeEventMessage msg) {
    return null;
  }

  @Override
  public OutputMessage qrsceneScan(final QrsceneScanEventMessage msg) {
    return null;
  }

  @Override
  public OutputMessage location(final LocationEventMessage msg) {
    return null;
  }

  @Override
  public OutputMessage click(final ClickEventMessage msg) {
    return null;
  }

  @Override
  public OutputMessage view(final ViewEventMessage msg) {
    return null;
  }

  @Override
  public OutputMessage scanCodePush(final ScanCodePushEventMessage msg) {
    return null;
  }

  @Override
  public OutputMessage scanCodeWaitMsg(final ScanCodeWaitMsgEventMessage msg) {
    return null;
  }

  @Override
  public OutputMessage picSysPhoto(final PicSysPhotoEventMessage msg) {
    return null;
  }

  @Override
  public OutputMessage picPhotoOrAlbum(final PicPhotoOrAlbumEventMessage msg) {
    return null;
  }

  @Override
  public OutputMessage picWeixin(final PicWeixinEventMessage msg) {
    return null;
  }

  @Override
  public OutputMessage locationSelect(final LocationSelectEventMessage msg) {
    return null;
  }

}
