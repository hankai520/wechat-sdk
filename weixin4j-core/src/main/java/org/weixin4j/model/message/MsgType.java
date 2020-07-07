/*
 * 微信公众平台(JAVA) SDK
 * Copyright (c) 2014, Ansitech Network Technology Co.,Ltd All rights reserved.
 * http://www.weixin4j.org/
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.weixin4j.model.message;

/**
 * 消息类型
 *
 * @author yangqisheng
 * @since 0.0.1
 */
public enum MsgType {

  /**
   * 1 文本消息
   */
  Text("text"),
  /**
   * 2 图片消息
   */
  Image("image"),
  /**
   * 3 语音消息
   */
  Voice("voice"),
  /**
   * 4 视频消息
   */
  Video("video"),
  /**
   * 5 小视频消息
   */
  ShortVideo("shortvideo"),
  /**
   * 6 地理位置消息
   */
  Location("location"),
  /**
   * 7 链接消息
   */
  Link("link"),
  /**
   * 事件消息
   */
  Event("event"),
  /**
   * 音乐消息
   */
  Music("music"),
  /**
   * 图文消息
   */
  News("news");

  private String value = "";

  MsgType(final String value) {
    this.value = value;
  }

  /**
   * 根据字符串值构建枚举。
   * 
   * @param value 字符串值
   * @return 枚举
   */
  public static MsgType fromString(final String value) {
    if (Text.value.equals(value)) {
      return Text;
    } else if (Image.value.equals(value)) {
      return Image;
    } else if (Voice.value.equals(value)) {
      return Voice;
    } else if (Video.value.equals(value)) {
      return Video;
    } else if (ShortVideo.value.equals(value)) {
      return ShortVideo;
    } else if (Location.value.equals(value)) {
      return Location;
    } else if (Link.value.equals(value)) {
      return Link;
    } else if (Event.value.equals(value)) {
      return Event;
    } else if (Music.value.equals(value)) {
      return Music;
    } else if (News.value.equals(value)) {
      return News;
    }
    return null;
  }

  /**
   * @return the msgType
   */
  @Override
  public String toString() {
    return value;
  }
}
