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
import org.weixin4j.component.AbstractComponent;
import org.weixin4j.component.BaseComponent;
import org.weixin4j.component.FileComponent;
import org.weixin4j.component.GroupsComponent;
import org.weixin4j.component.JsSdkComponent;
import org.weixin4j.component.MaterialComponent;
import org.weixin4j.component.MediaComponent;
import org.weixin4j.component.MenuComponent;
import org.weixin4j.component.MessageComponent;
import org.weixin4j.component.PayComponent;
import org.weixin4j.component.QrcodeComponent;
import org.weixin4j.component.RedpackComponent;
import org.weixin4j.component.SnsComponent;
import org.weixin4j.component.TagsComponent;
import org.weixin4j.component.UserComponent;
import org.weixin4j.config.Configuration;
import org.weixin4j.config.WeixinConfig;
import org.weixin4j.config.WeixinPayConfig;
import org.weixin4j.exception.WeixinException;
import org.weixin4j.loader.ITicketLoader;
import org.weixin4j.loader.ITokenLoader;
import org.weixin4j.loader.impl.DefaultTicketLoader;
import org.weixin4j.loader.impl.DefaultTokenLoader;
import org.weixin4j.model.base.Token;
import org.weixin4j.model.js.Ticket;
import org.weixin4j.model.js.TicketType;
import org.weixin4j.util.WeixinSupport;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信平台基础支持对象
 *
 * @author yangqisheng
 * @since 0.0.1
 */
public class Weixin extends WeixinSupport implements java.io.Serializable {

  private static final long serialVersionUID = 1L;
  /**
   * 同步锁
   */
  private final static byte[] LOCK = new byte[0];
  /**
   * 公众号开发者ID
   */
  private final String appId;
  /**
   * 公众号开发者密钥
   */
  private final String secret;
  /**
   * 公众号配置
   *
   * @since 0.1.3
   */
  private final WeixinConfig weixinConfig;
  /**
   * 微信支付配置
   *
   * @since 0.1.3
   */
  private final WeixinPayConfig weixinPayConfig;
  /**
   * AccessToken加载器
   */
  protected ITokenLoader tokenLoader = new DefaultTokenLoader();
  /**
   * Ticket加载器
   */
  protected ITicketLoader ticketLoader = new DefaultTicketLoader();
  /**
   * 新增组件
   */
  private final Map<String, AbstractComponent> components = new HashMap<>();

  /**
   * 单公众号，并且只支持一个公众号方式
   */
  Weixin() {
    this(Configuration.oAuthAppId(), Configuration.oAuthSecret());
  }

  /**
   * 多公众号，同一个环境中使用方式
   *
   * @param appId 公众号开发者AppId
   * @param secret 公众号开发者秘钥
   */
  Weixin(final String appId, final String secret) {
    this.appId = appId;
    this.secret = secret;
    weixinConfig = new WeixinConfig();
    weixinConfig.setAppid(appId);
    weixinConfig.setSecret(secret);
    weixinConfig.setOriginalid(Configuration.getProperty("weixin4j.oauth.originalid"));
    weixinConfig.setEncodingtype(Configuration.getIntProperty("weixin4j.oauth.encodingtype"));
    weixinConfig.setEncodingaeskey(Configuration.getProperty("weixin4j.oauth.encodingaeskey"));
    weixinConfig.setOauthUrl(Configuration.getProperty("weixin4j.oauth.url"));
    weixinConfig.setApiDomain(Configuration.getProperty("weixin4j.api.domain"));
    weixinPayConfig = new WeixinPayConfig();
    weixinPayConfig.setAppId(appId);
    weixinPayConfig.setPartnerId(Configuration.getProperty("weixin4j.pay.partner.id"));
    weixinPayConfig.setPartnerKey(Configuration.getProperty("weixin4j.pay.partner.key"));
    weixinPayConfig.setNotifyUrl(Configuration.getProperty("weixin4j.pay.notify_url"));
    weixinPayConfig.setMchId(
        Configuration.getProperty("weixin4j.pay.mch.id", Configuration.getProperty("weixin4j.pay.partner.id")));
    weixinPayConfig.setMchKey(
        Configuration.getProperty("weixin4j.pay.mch.key", Configuration.getProperty("weixin4j.pay.partner.key")));
    weixinPayConfig.setCertPath(Configuration.getProperty("weixin4j.http.cert.path"));
    weixinPayConfig.setCertSecret(Configuration.getProperty("weixin4j.http.cert.secret"));
    if (StringUtils.isEmpty(this.weixinPayConfig.getAppId())) {
      this.weixinPayConfig.setAppId(weixinConfig.getAppid());
    }
    if (StringUtils.isEmpty(this.weixinPayConfig.getMchId())) {
      this.weixinPayConfig.setMchId(weixinPayConfig.getPartnerId());
    }
    if (StringUtils.isEmpty(this.weixinPayConfig.getMchKey())) {
      this.weixinPayConfig.setMchKey(weixinPayConfig.getPartnerKey());
    }
    // 兼容证书密钥未设置
    if (StringUtils.isEmpty(this.weixinPayConfig.getCertSecret())) {
      weixinPayConfig.setCertSecret(weixinPayConfig.getMchId());
    }
  }

  /**
   * 外部配置注入方式，更灵活
   *
   * @param weixinConfig 微信公众号配置
   * @since 0.1.3
   */
  Weixin(final WeixinConfig weixinConfig) {
    this(weixinConfig, null);
  }

  /**
   * 外部配置注入方式（带微信支付），更灵活
   *
   * @param weixinPayConfig 微信支付配置
   * @since 0.1.3
   */
  Weixin(final WeixinPayConfig weixinPayConfig) {
    this.appId = Configuration.oAuthAppId();
    this.secret = Configuration.oAuthSecret();
    weixinConfig = new WeixinConfig();
    weixinConfig.setAppid(Configuration.oAuthAppId());
    weixinConfig.setSecret(Configuration.oAuthSecret());
    weixinConfig.setOriginalid(Configuration.getProperty("weixin4j.oauth.originalid"));
    weixinConfig.setEncodingtype(Configuration.getIntProperty("weixin4j.oauth.encodingtype"));
    weixinConfig.setEncodingaeskey(Configuration.getProperty("weixin4j.oauth.encodingaeskey"));
    weixinConfig.setOauthUrl(Configuration.getProperty("weixin4j.oauth.url"));
    weixinConfig.setApiDomain(Configuration.getProperty("weixin4j.api.domain"));
    this.weixinPayConfig = weixinPayConfig;
    // 兼容0.1.6以前的版本
    if (this.weixinPayConfig != null) {
      if (StringUtils.isEmpty(this.weixinPayConfig.getAppId())) {
        this.weixinPayConfig.setAppId(weixinConfig.getAppid());
      }
      if (StringUtils.isEmpty(this.weixinPayConfig.getMchId())) {
        this.weixinPayConfig.setMchId(weixinPayConfig.getPartnerId());
      }
      if (StringUtils.isEmpty(this.weixinPayConfig.getMchKey())) {
        this.weixinPayConfig.setMchKey(weixinPayConfig.getPartnerKey());
      }
      // 兼容证书密钥未设置
      if (StringUtils.isEmpty(this.weixinPayConfig.getCertSecret())) {
        weixinPayConfig.setCertSecret(weixinPayConfig.getMchId());
      }
    }
  }

  /**
   * 外部配置注入方式（带微信支付），更灵活
   *
   * @param weixinConfig 微信公众号配置
   * @param weixinPayConfig 微信支付配置
   * @since 0.1.3
   */
  Weixin(final WeixinConfig weixinConfig, final WeixinPayConfig weixinPayConfig) {
    this.appId = weixinConfig.getAppid();
    this.secret = weixinConfig.getSecret();
    this.weixinConfig = weixinConfig;
    this.weixinPayConfig = weixinPayConfig;
    // 兼容0.1.6以前的版本
    if (this.weixinPayConfig != null) {
      if (StringUtils.isEmpty(this.weixinPayConfig.getAppId())) {
        this.weixinPayConfig.setAppId(weixinConfig.getAppid());
      }
      if (StringUtils.isEmpty(this.weixinPayConfig.getMchId())) {
        this.weixinPayConfig.setMchId(weixinPayConfig.getPartnerId());
      }
      if (StringUtils.isEmpty(this.weixinPayConfig.getMchKey())) {
        this.weixinPayConfig.setMchKey(weixinPayConfig.getPartnerKey());
      }
      // 兼容证书密钥未设置
      if (StringUtils.isEmpty(this.weixinPayConfig.getCertSecret())) {
        weixinPayConfig.setCertSecret(weixinPayConfig.getMchId());
      }
    }
  }

  public String getAppId() {
    return appId;
  }

  public String getSecret() {
    return secret;
  }

  /**
   * 设置 tokenLoader 字段的值。
   *
   * @param tokenLoader tokenLoader 字段的值
   */
  public void setTokenLoader(final ITokenLoader tokenLoader) {
    this.tokenLoader = tokenLoader;
  }

  /**
   * 设置 ticketLoader 字段的值。
   *
   * @param ticketLoader ticketLoader 字段的值
   */
  public void setTicketLoader(final ITicketLoader ticketLoader) {
    this.ticketLoader = ticketLoader;
  }

  /**
   * 获取Token对象
   *
   * @return Token对象
   * @throws org.weixin4j.exception.WeixinException 微信操作异常
   * @since 0.1.0
   */
  public Token getToken() throws WeixinException {
    Token token = tokenLoader.get();
    if (token == null) {
      synchronized (LOCK) {
        token = tokenLoader.get();
        if (token == null) {
          token = base().token();
          tokenLoader.refresh(token);
        }
      }
    }
    return token;
  }

  /**
   * 获取jsapi开发ticket
   *
   * @return jsapi_ticket
   * @throws org.weixin4j.exception.WeixinException 微信操作异常
   */
  public Ticket getJsApiTicket() throws WeixinException {
    Ticket ticket = ticketLoader.get(TicketType.JSAPI);
    if (ticket == null) {
      synchronized (LOCK) {
        ticket = ticketLoader.get(TicketType.JSAPI);
        if (ticket == null) {
          ticket = js().getJsApiTicket();
          ticketLoader.refresh(ticket);
        }
      }
    }
    return ticket;
  }

  public BaseComponent base() {
    final String key = BaseComponent.class.getName();
    if (components.containsKey(key)) {
      return (BaseComponent) components.get(key);
    }
    final BaseComponent component = new BaseComponent(this);
    components.put(key, component);
    return component;
  }

  public JsSdkComponent js() {
    final String key = JsSdkComponent.class.getName();
    if (components.containsKey(key)) {
      return (JsSdkComponent) components.get(key);
    }
    final JsSdkComponent component = new JsSdkComponent(this);
    components.put(key, component);
    return component;
  }

  public UserComponent user() {
    final String key = UserComponent.class.getName();
    if (components.containsKey(key)) {
      return (UserComponent) components.get(key);
    }
    final UserComponent component = new UserComponent(this);
    components.put(key, component);
    return component;
  }

  public SnsComponent sns() {
    final String key = SnsComponent.class.getName();
    if (components.containsKey(key)) {
      return (SnsComponent) components.get(key);
    }
    final SnsComponent component = new SnsComponent(this);
    components.put(key, component);
    return component;
  }

  public SnsComponent sns(final String authorize_url) {
    final String key = SnsComponent.class.getName();
    if (components.containsKey(key)) {
      return (SnsComponent) components.get(key);
    }
    final SnsComponent component = new SnsComponent(this, authorize_url);
    components.put(key, component);
    return component;
  }

  public TagsComponent tags() {
    final String key = TagsComponent.class.getName();
    if (components.containsKey(key)) {
      return (TagsComponent) components.get(key);
    }
    final TagsComponent component = new TagsComponent(this);
    components.put(key, component);
    return component;
  }

  public GroupsComponent groups() {
    final String key = GroupsComponent.class.getName();
    if (components.containsKey(key)) {
      return (GroupsComponent) components.get(key);
    }
    final GroupsComponent component = new GroupsComponent(this);
    components.put(key, component);
    return component;
  }

  public PayComponent pay() {
    final String key = PayComponent.class.getName();
    if (components.containsKey(key)) {
      return (PayComponent) components.get(key);
    }
    final PayComponent component = new PayComponent(this);
    components.put(key, component);
    return component;
  }

  public RedpackComponent redpack() {
    final String key = RedpackComponent.class.getName();
    if (components.containsKey(key)) {
      return (RedpackComponent) components.get(key);
    }
    final RedpackComponent component = new RedpackComponent(this);
    components.put(key, component);
    return component;
  }

  public MessageComponent message() {
    final String key = MessageComponent.class.getName();
    if (components.containsKey(key)) {
      return (MessageComponent) components.get(key);
    }
    final MessageComponent component = new MessageComponent(this);
    components.put(key, component);
    return component;
  }

  public MenuComponent menu() {
    final String key = MenuComponent.class.getName();
    if (components.containsKey(key)) {
      return (MenuComponent) components.get(key);
    }
    final MenuComponent component = new MenuComponent(this);
    components.put(key, component);
    return component;
  }

  @Deprecated
  public MediaComponent media() {
    final String key = MediaComponent.class.getName();
    if (components.containsKey(key)) {
      return (MediaComponent) components.get(key);
    }
    final MediaComponent component = new MediaComponent(this);
    components.put(key, component);
    return component;
  }

  @Deprecated
  public FileComponent file() {
    final String key = FileComponent.class.getName();
    if (components.containsKey(key)) {
      return (FileComponent) components.get(key);
    }
    final FileComponent component = new FileComponent(this);
    components.put(key, component);
    return component;
  }

  public MaterialComponent material() {
    final String key = MaterialComponent.class.getName();
    if (components.containsKey(key)) {
      return (MaterialComponent) components.get(key);
    }
    final MaterialComponent component = new MaterialComponent(this);
    components.put(key, component);
    return component;
  }

  public QrcodeComponent qrcode() {
    final String key = QrcodeComponent.class.getName();
    if (components.containsKey(key)) {
      return (QrcodeComponent) components.get(key);
    }
    final QrcodeComponent component = new QrcodeComponent(this);
    components.put(key, component);
    return component;
  }

  /**
   * 获取微信配置对象
   *
   * @return 微信配置对象
   * @since 0.1.3
   */
  public WeixinConfig getWeixinConfig() {
    return weixinConfig;
  }

  /**
   * 获取微信支付配置对象
   *
   * @return 微信支付配置对象
   * @since 0.1.3
   */
  public WeixinPayConfig getWeixinPayConfig() {
    return weixinPayConfig;
  }
}
