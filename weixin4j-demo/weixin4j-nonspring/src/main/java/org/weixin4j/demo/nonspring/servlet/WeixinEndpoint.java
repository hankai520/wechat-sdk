package org.weixin4j.demo.nonspring.servlet;

import org.weixin4j.MessageDispatcher;
import org.weixin4j.Weixin;
import org.weixin4j.WeixinBuilder;
import org.weixin4j.demo.nonspring.handler.MyEventMessageHandler;
import org.weixin4j.demo.nonspring.handler.MyNormalMessageHandler;
import org.weixin4j.model.message.MsgType;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value = "/hooks/weixin", loadOnStartup = 1)
public class WeixinEndpoint extends HttpServlet {

  private static final long serialVersionUID = 1L;

  private static MessageDispatcher dispatcher;

  public static Weixin weixin;

  public WeixinEndpoint() {
    if (null == weixin) {
      // 初始化微信核心对象，用于其他地方主动调用微信接口
      final WeixinBuilder builder = new WeixinBuilder(null); // 传入null用默认的weixin4j.properties
      weixin = builder
          // .ticketLoader(null) //设置自定义的ticket获取实现
          // .tokenLoader(null) //设置自定义的access token获取实现
          .build();
    }
    if (null == dispatcher) {
      dispatcher = new MessageDispatcher();
      final MyEventMessageHandler emh = new MyEventMessageHandler();
      dispatcher.addHandler(MsgType.Event, emh);

      final MyNormalMessageHandler nmh = new MyNormalMessageHandler();
      dispatcher.addHandler(MsgType.Image, nmh);
      dispatcher.addHandler(MsgType.Link, nmh);
      dispatcher.addHandler(MsgType.Location, nmh);
      dispatcher.addHandler(MsgType.Music, nmh);
      dispatcher.addHandler(MsgType.News, nmh);
      dispatcher.addHandler(MsgType.ShortVideo, nmh);
      dispatcher.addHandler(MsgType.Text, nmh);
      dispatcher.addHandler(MsgType.Video, nmh);
      dispatcher.addHandler(MsgType.Voice, nmh);
    }
  }

  @Override
  protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
      throws ServletException, IOException {
    if (request.getParameterMap().isEmpty()) {
      response.setStatus(400);
    } else {
      dispatcher.dispatch(request, response);
    }
  }

  @Override
  protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
      throws ServletException, IOException {
    doGet(request, response);
  }

}
