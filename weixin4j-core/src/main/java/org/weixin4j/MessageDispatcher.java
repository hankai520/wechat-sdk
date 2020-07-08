
package org.weixin4j;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weixin4j.exception.WeixinException;
import org.weixin4j.model.message.InputMessage;
import org.weixin4j.model.message.MsgType;
import org.weixin4j.spi.IMessageHandler;
import org.weixin4j.util.TokenUtil;
import org.weixin4j.util.XStreamFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * 微信 HTTP 消息分发器，是处理微信平台所有请求的入口，可在Servlet或者Filter中调用，用来分发消息。
 * 内部分发时通过ExecutorService实现消息处理的调用，以便支持超时取消，满足微信平台的时间要求。
 *
 * @author hankai
 * @since 1.0.0
 */
public class MessageDispatcher {

  private static final Logger logger = LoggerFactory.getLogger(MessageDispatcher.class);

  // 处理不同消息类型的消息处理器
  private final Map<MsgType, IMessageHandler> handlers = new HashMap<>(10);

  private ExecutorService executor;

  // 微信官方解释5秒内需要有响应，考虑网络传输，可酌情限定
  private int messageHandlingTimeout = 3;

  /*
   * 任务并行性，根据程序最大线程数和消息量来分配。分配原则：
   * 1. messageHandlingTimeout内最大消息量 >= parallelism < 应用最大可用线程数
   * 2. parallelism % CPU数量 = 0
   */
  private int parallelism = 8;

  /**
   * 读取微信平台消息。
   *
   * @param inputStream 输入流
   * @return 消息实例
   * @throws WeixinException
   */
  private InputMessage readMessage(final InputStream inputStream) throws WeixinException {
    try {
      final String inputXml = XStreamFactory.inputStream2String(inputStream);
      final JAXBContext context = JAXBContext.newInstance(InputMessage.class);
      final Unmarshaller unmarshaller = context.createUnmarshaller();
      final InputMessage inputMsg = (InputMessage) unmarshaller.unmarshal(new StringReader(inputXml));
      logger.debug(String.format("收到微信平台消息(%s)：\n %s \n", inputMsg.getMsgType(), inputXml));
      return inputMsg;
    } catch (final IOException ex) {
      throw new WeixinException("读取微信平台消息出错", ex);
    } catch (final JAXBException ex) {
      throw new WeixinException("解析微信平台消息出错", ex);
    }
  }

  /**
   * 限时处理消息。
   *
   * @param task 消息处理逻辑实现
   * @return 消息处理结果
   * @throws WeixinException
   */
  private String invokeHandler(final Callable<String> task) throws WeixinException {
    String result = null;
    if (null == executor) {
      // 微信消息的消费与时间顺序无关，且对响应性要求较高，因此选择 work-stealing，尽可能利用
      // 线程资源。实际线程资源可以动态增减，因此更适用于消息量不稳定的情况。
      executor = Executors.newWorkStealingPool(parallelism);
    }
    final Future<String> future = executor.submit(task);
    try {
      result = future.get(messageHandlingTimeout, TimeUnit.SECONDS);
    } catch (final TimeoutException ex) {
      throw new WeixinException("消息处理超时", ex);
    } catch (final InterruptedException ex) {
      // 消息处理被打断，通常是是线程管理问题或者资源问题
      throw new WeixinException("消息处理过程被打断", ex);
    } catch (final ExecutionException ignore) {
      // 消息处理逻辑执行失败，通常是业务错误，由对应开发者自行处理
    } finally {
      // 如果既没有取消、也没有完成，则人工取消
      if (!future.isCancelled() && !future.isDone()) {
        future.cancel(true);
      }
    }
    if (StringUtils.isEmpty(result)) {
      result = "success";// 避免微信试图重发请求
    }
    return result;
  }

  /**
   * 添加消息处理器，同类处理器，只有最后添加的生效。
   *
   * @param msgType 消息类型
   * @param handler 处理器
   */
  public void addHandler(final MsgType msgType, final IMessageHandler handler) {
    if ((null != msgType) && (null != handler)) {
      handlers.put(msgType, handler);
    }
  }

  /**
   * 移除消息处理器。
   *
   * @param msgType 消息类型
   */
  public void removeHandler(final MsgType msgType) {
    handlers.remove(msgType);
  }

  /**
   * 获取 messageHandlingTimeout 字段的值。
   *
   * @return messageHandlingTimeout 字段值
   */
  public int getMessageHandlingTimeout() {
    return messageHandlingTimeout;
  }

  /**
   * 设置 messageHandlingTimeout 字段的值。
   *
   * @param messageHandlingTimeout messageHandlingTimeout 字段的值
   */
  public void setMessageHandlingTimeout(final int messageHandlingTimeout) {
    this.messageHandlingTimeout = messageHandlingTimeout;
  }

  /**
   * 获取 parallelism 字段的值。
   *
   * @return parallelism 字段值
   */
  public int getParallelism() {
    return parallelism;
  }

  /**
   * 设置 parallelism 字段的值。
   *
   * @param parallelism parallelism 字段的值
   */
  public void setParallelism(final int parallelism) {
    this.parallelism = parallelism;
  }

  /**
   * 解析并派发消息到合适的消息处理。
   *
   * @param req HTTP请求
   * @param res HTTP响应
   * @throws IOException HTTP I/O 异常
   */
  public void dispatch(final ServletRequest req, final ServletResponse res) throws IOException {
    final HttpServletRequest request = (HttpServletRequest) req;
    final HttpServletResponse response = (HttpServletResponse) res;
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/xml");
    logger.debug(request.getMethod() + ": " + request.getServletPath());
    final String signature = request.getParameter("signature");// 微信加密签名
    final String timestamp = request.getParameter("timestamp");// 时间戳
    final String nonce = request.getParameter("nonce"); // 随机数
    final String token = TokenUtil.get();
    if (!TokenUtil.checkSignature(token, signature, timestamp, nonce)) {
      // 验签不过，消息可能是伪造的，记录日志供追溯
      logger.warn(String.format("来自 %s 的潜在攻击，位于 %s", request.getRemoteAddr(), request.getServletPath()));
      response.setStatus(400);
      response.getWriter().write("签名错误.");
      return;
    }
    if (request.getMethod().equalsIgnoreCase("get")) {
      // 验证消息的确来自微信服务器：https://developers.weixin.qq.com/doc/offiaccount/Basic_Information/Access_Overview.html
      final String echostr = request.getParameter("echostr");
      logger.debug("收到微信验证服务器的消息，回显字符串为：" + echostr);
      response.getWriter().write(echostr);
    } else {
      // 消息管理：https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Receiving_standard_messages.html
      try {
        final InputMessage message = readMessage(request.getInputStream());
        final MsgType msgType = MsgType.fromString(message.getMsgType());
        if (null == msgType) {
          throw new WeixinException("无法识别的微信消息类型：" + message.getMsgType());
        }
        final IMessageHandler handler = handlers.get(msgType);
        if (null == handler) {
          logger.warn("没有找到 " + message.getMsgType() + " 类型的消息处理器");
        } else {
          final String xml = invokeHandler(new Callable<String>() {

            @Override
            public String call() throws Exception {
              return handler.invoke(message);
            }
          });
          response.getWriter().write(xml);
        }
      } catch (final WeixinException ex) {
        logger.error("处理微信消息失败", ex);
        response.setStatus(500);
      }
    }
  }

}
