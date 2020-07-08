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
package org.weixin4j.spi;

import org.weixin4j.exception.WeixinException;
import org.weixin4j.model.message.InputMessage;

/**
 * 输入消息处理器
 *
 * @author yangqisheng
 * @since 0.0.6
 */
public interface IMessageHandler {

  /**
   * 处理微信平台消息。
   *
   * @param message 已解析的消息实例
   * @return 处理结果xml
   * @throws WeixinException 微信接口异常
   */
  String invoke(InputMessage message) throws WeixinException;
}
