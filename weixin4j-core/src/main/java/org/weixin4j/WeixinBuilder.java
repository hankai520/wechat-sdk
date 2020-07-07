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
package org.weixin4j;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weixin4j.config.Configuration;
import org.weixin4j.config.WeixinConfig;
import org.weixin4j.config.WeixinPayConfig;
import org.weixin4j.loader.ITicketLoader;
import org.weixin4j.loader.ITokenLoader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * 微信对象构建器
 *
 * @author yangqisheng
 * @since 0.1.0
 */
public final class WeixinBuilder {

  private static final Logger logger = LoggerFactory.getLogger(WeixinBuilder.class);

  private Weixin weixin;

  private ITokenLoader tokenLoader;

  private ITicketLoader ticketLoader;

  @SuppressWarnings("unused")
  private WeixinBuilder() {}

  public WeixinBuilder(final String configFilePath) {
    try {
      if (StringUtils.isEmpty(configFilePath)) {
        Configuration.load(null);
      } else {
        Configuration.load(new FileInputStream(configFilePath));
      }
    } catch (final FileNotFoundException ex) {
      logger.error("指定的配置文件不存在 " + configFilePath, ex);
    }
  }

  /**
   * 设置oauth2认证信息。
   *
   * @param appId 应用标识
   * @param appSecret 应用密钥
   * @return this
   */
  public WeixinBuilder oauth(final String appId, final String appSecret) {
    this.weixin = new Weixin(appId, appSecret);
    return this;
  }

  /**
   * 设置oauth2认证信息。
   *
   * @param config 微信配置
   * @return this
   */
  public WeixinBuilder oauth(final WeixinConfig config) {
    this.weixin = new Weixin(config);
    return this;
  }

  /**
   * 设置oauth2认证信息。
   *
   * @param config 微信配置
   * @return this
   */
  public WeixinBuilder oauth(final WeixinPayConfig config) {
    this.weixin = new Weixin(config);
    return this;
  }

  /**
   * 设置oauth2认证信息。
   *
   * @param weixinConfig 微信配置
   * @param payConfig 微信支付配置
   * @return this
   */
  public WeixinBuilder oauth(final WeixinConfig weixinConfig, final WeixinPayConfig payConfig) {
    this.weixin = new Weixin(weixinConfig, payConfig);
    return this;
  }

  /**
   * 设置访问令牌加载器。
   *
   * @param tokenLoader 访问令牌加载器
   * @return this
   */
  public WeixinBuilder tokenLoader(final ITokenLoader tokenLoader) {
    if (tokenLoader == null) {
      throw new IllegalStateException("tokenLoader不可为空");
    }
    this.tokenLoader = tokenLoader;
    return this;
  }

  /**
   * 配置ticket加载器
   *
   * @param ticketLoader ticket加载器
   * @return return this
   */
  public WeixinBuilder ticketLoader(final ITicketLoader ticketLoader) {
    if (ticketLoader == null) {
      throw new IllegalStateException("ticketLoader不可为空");
    }
    this.ticketLoader = ticketLoader;
    return this;
  }

  /**
   * 返回最终配置好的Weixin对象
   *
   * @return 微信对象
   */
  public Weixin build() {
    if (weixin == null) {
      weixin = new Weixin();
    }
    if (tokenLoader != null) {
      weixin.setTokenLoader(this.tokenLoader);
    }
    if (this.ticketLoader != null) {
      weixin.setTicketLoader(this.ticketLoader);
    }
    return weixin;
  }
}
