
package org.weixin4j.demo.nonspring.servlet;

import org.weixin4j.exception.WeixinException;
import org.weixin4j.model.message.template.TemplateData;
import org.weixin4j.model.qrcode.Qrcode;
import org.weixin4j.model.qrcode.QrcodeType;
import org.weixin4j.model.user.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 用户servlet。
 *
 * @author hankai
 * @since 1.0.0
 */
@WebServlet("/user")
public class UserServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  // 模拟会话存储。
  public static final Map<String, HttpSession> sessions = new HashMap<>();

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException {
    resp.setCharacterEncoding("UTF-8");
    final String action = req.getParameter("action");
    if ("login".equals(action)) {
      resp.setContentType("text/html; charset=utf-8");
      try {
        final Qrcode code = WeixinEndpoint.weixin.qrcode().create(QrcodeType.QR_STR_SCENE, "login", 60 * 5);
        final HttpSession session = req.getSession();
        sessions.put(code.getTicket(), session);
        final String url = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + code.getTicket();
        final String html = "<!doctype html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<title>login</title>\n" +
            "<script type=\"text/javascript\" src=\"http://apps.bdimg.com/libs/jquery/1.9.1/jquery.min.js\"></script>\n"
            +
            "<script type=\"text/javascript\">\n" +
            "var loop = setInterval(function() {\n" +
            "    $.ajax({\n" +
            "      url: \"" + req.getServletContext().getContextPath() + "/user?action=login_check\",\n" +
            "      data: {\n" +
            "        qr_ticket: \"" + code.getTicket() + "\"\n" +
            "      },\n" +
            "      success: function( result ) {\n" +
            "        if(result.login) {\n" +
            "            alert(\"登录成功\");\n" +
            "            location.href=\"" + req.getServletContext().getContextPath() + "/user?action=profile\";\n" +
            "        }\n" +
            "        clearInterval(loop);\n" +
            "      }\n" +
            "    }); \n" +
            "}, 5000);\n" +
            "</script>" +
            "</head>\n" +
            "<body>\n" +
            "    <h2>请用微信扫码登录</h2>\n" +
            "    <img alt=\"\" src=\"" + url + "\">\n" +
            " <br/><a href=\"" + "" + "\">个人信息页面</a> " +
            "</body>\n" +
            "</html>\n";
        resp.getWriter().print(html);
      } catch (final WeixinException ex) {
        ex.printStackTrace();
      }
    } else if ("login_check".equals(action)) {
      resp.setContentType("application/json; charset=utf-8");
      final String qrTicket = req.getParameter("qr_ticket");
      final HttpSession session = sessions.get(qrTicket);
      if (null != session) {
        final Object obj = session.getAttribute("openid");
        if ((null != obj) && (obj instanceof String)) {
          resp.getWriter().print("{\"login\":true}");
          resp.setContentType("application/json; charset=utf8");
        }
      }
    } else if ("profile".equals(action)) {
      resp.setContentType("text/html; charset=utf-8");
      try {
        final HttpSession session = req.getSession();
        final Object obj = session.getAttribute("openid");
        String openid = null;
        if ((null != obj) && (obj instanceof String)) {
          openid = (String) obj;
        }
        final User weixinUser = WeixinEndpoint.weixin.user().info(openid);
        final String html = "<!doctype html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<title>home</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <h2>已登录，欢迎光临！</h2>\n" +
            "    <p> " + weixinUser.getNickname() + " </p>\n" +
            "    <img alt=\"\" src=\"" + weixinUser.getHeadimgurl() + "\">\n" +
            "    <p><button><a href=\"" + req.getServletContext().getContextPath()
            + "/user?action=send_msg&openid=" + weixinUser.getOpenid() + "\">发送消息到微信</a></button></p>\n" +
            "</body>\n" +
            "</html>\n";
        resp.getWriter().print(html);
      } catch (final WeixinException ex) {
        throw new RuntimeException(ex);
      }
    } else if ("send_msg".equals(action)) {
      resp.setContentType("text/html; charset=utf-8");
      final HttpSession session = req.getSession();
      final Object obj = session.getAttribute("openid");
      String openid = null;
      if ((null != obj) && (obj instanceof String)) {
        openid = (String) obj;
      }
      final List<TemplateData> data = new ArrayList<>();
      final TemplateData p1 = new TemplateData("bizName", "张三有限公司设立登记", "#FF0000");
      data.add(p1);
      try {
        WeixinEndpoint.weixin.message().sendTemplateMessage(openid, "0SyMq2AKs9XO0EgCzbh-mdjqYceDdeTuTGLW0FEdYF0",
            data);
        resp.getWriter().print("发送成功");
      } catch (final WeixinException ex) {
        throw new RuntimeException(ex);
      }
    }
    resp.getWriter().flush();
  }

  @Override
  protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException {
    doGet(req, resp);
  }

}
