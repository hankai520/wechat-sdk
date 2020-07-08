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
package org.weixin4j.config;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 微信平台调用基础配置
 * <p>配置参数优先级：System.getenv(name) &gt; System.getProperty(key) &gt; weixin4j.properties </p>
 *
 * @author yangqisheng, hankai
 * @since 0.0.1
 */
public final class Configuration {

  private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

  private static Properties defaultProperty = null;

  public static boolean loaded() {
    return (defaultProperty != null) && !defaultProperty.isEmpty();
  }

  public static boolean getBoolean(final String name) {
    final String value = getProperty(name);
    return Boolean.valueOf(value);
  }

  public static int getIntProperty(final String name) {
    final String value = getProperty(name);
    try {
      return Integer.parseInt(value);
    } catch (final NumberFormatException nfe) {
      return -1;
    }
  }

  public static int getIntProperty(final String name, final int fallbackValue) {
    final String value = getProperty(name);
    try {
      return Integer.parseInt(value);
    } catch (final NumberFormatException ignore) {
    }
    return fallbackValue;
  }

  public static String getProperty(final String name) {
    return getProperty(name, null);
  }

  public static String getProperty(final String name, final String fallbackValue) {
    String value = System.getenv(name);
    value = StringUtils.isEmpty(value) ? System.getProperty(name) : value;
    value = StringUtils.isEmpty(value) ? defaultProperty.getProperty(name) : value;
    value = StringUtils.isEmpty(value) ? fallbackValue : value;
    return value;
  }

  /**
   * 载入配置文件。
   *
   * @param inputStream 配置文件输入流，为null则从类路径加载 weixin4j.properties
   */
  public static void load(final InputStream inputStream) {
    defaultProperty = new Properties();
    try {
      if (null == inputStream) {
        defaultProperty.load(Configuration.class.getClassLoader().getResourceAsStream("weixin4j.properties"));
      } else {
        defaultProperty.load(inputStream);
      }
    } catch (final IOException ex) {
      logger.error("载入微信配置文件出错", ex);
    }
  }

  /**
   * 获取开发者第三方用户唯一凭证
   *
   * @return 第三方用户唯一凭证
   */
  public static String oAuthAppId() {
    return getProperty("weixin4j.oauth.appid");
  }

  /**
   * 获取开发者第三方用户唯一凭证密钥
   *
   * @return 第三方用户唯一凭证密钥
   */
  public static String oAuthSecret() {
    return getProperty("weixin4j.oauth.secret");
  }

  /**
   * 获取开发者第三方用户唯一凭证密钥
   *
   * @param secret 默认第三方用户唯一凭证密钥
   * @return 第三方用户唯一凭证密钥
   */
  public static String oAuthSecret(final String secret) {
    return getProperty("weixin4j.oauth.secret", secret);
  }

  /**
   * 获取 连接超时时间
   *
   * @return 连接超时时间
   */
  public static int connectionTimeout() {
    return getIntProperty("weixin4j.http.connectionTimeout");
  }

  /**
   * 获取 连接超时时间
   *
   * @param connectionTimeout 默认连接超时时间
   * @return 连接超时时间
   */
  public static int connectionTimeout(final int connectionTimeout) {
    return getIntProperty("weixin4j.http.connectionTimeout", connectionTimeout);
  }

  /**
   * 获取 请求超时时间
   *
   * @return 请求超时时间
   */
  public static int readTimeout() {
    return getIntProperty("weixin4j.http.readTimeout");
  }

  /**
   * 获取 是否为调试模式
   *
   * @return 是否为调试模式
   */
  public static boolean isDebug() {
    return getBoolean("weixin4j.debug");
  }
}
