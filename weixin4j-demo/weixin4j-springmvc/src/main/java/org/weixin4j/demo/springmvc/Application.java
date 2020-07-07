package org.weixin4j.demo.springmvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {

  private static ApplicationContext appContext;

  public static ApplicationContext getContext() {
    return appContext;
  }

  public static void main(final String[] args) {
    appContext = SpringApplication.run(Application.class, args);
  }
}
