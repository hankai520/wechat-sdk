
package org.weixin4j.demo.springmvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.weixin4j.Weixin;
import org.weixin4j.exception.WeixinException;
import org.weixin4j.model.message.template.TemplateData;
import org.weixin4j.model.qrcode.Qrcode;
import org.weixin4j.model.qrcode.QrcodeType;
import org.weixin4j.model.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO 缺少类型描述。
 *
 * @author hankai
 * @version TODO 缺少版本号
 * @since Jun 29, 2020 10:55:07 AM
 */
@Controller
public class UserController {

  @Autowired
  private Weixin weixin;

  private String qrcode() throws WeixinException {
    final Qrcode code = weixin.qrcode().create(QrcodeType.QR_STR_SCENE, "login", 60 * 5);
    return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + code.getTicket();
  }

  @RequestMapping("/login")
  public ModelAndView showLoginView() throws WeixinException {
    final ModelAndView mav = new ModelAndView("login.html");
    mav.addObject("qrcode_url", qrcode());
    return mav;
  }

  @RequestMapping("/home")
  public ModelAndView showHomePage(@RequestParam("openid") final String openId) throws WeixinException {
    final ModelAndView mav = new ModelAndView("home.html");
    final User user = weixin.user().info(openId);
    mav.addObject("weixinUser", user);
    return mav;
  }

  @RequestMapping("/send_biz_progress")
  public String sendProgress(@RequestParam("openid") final String openId) throws WeixinException {
    final List<TemplateData> data = new ArrayList<>();
    final TemplateData p1 = new TemplateData("bizName", "张三有限公司设立登记", "#FF0000");
    data.add(p1);
    weixin.message().sendTemplateMessage(openId, "0SyMq2AKs9XO0EgCzbh-mdjqYceDdeTuTGLW0FEdYF0", data);
    return "redirect:/home?openid=" + openId;
  }

  @Autowired
  private SimpMessagingTemplate simpMessagingTemplate;

  @MessageMapping("/send.message")
  public void sendPublicMessage(final String msg) {
    System.out.println(msg);
    simpMessagingTemplate.convertAndSend("/topic/login.success", "登录成功");
  }
}
