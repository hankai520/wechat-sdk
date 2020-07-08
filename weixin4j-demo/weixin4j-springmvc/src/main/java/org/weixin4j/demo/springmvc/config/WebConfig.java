package org.weixin4j.demo.springmvc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.spring5.ISpringTemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class WebConfig {

  /**
   * HTML 模板解析器。
   *
   * @param applicationContext spring 上下文
   * @return 模板解析器
   */
  @Bean
  public ITemplateResolver htmlTemplateResolver(final ApplicationContext applicationContext) {
    final SpringResourceTemplateResolver templateResolver =
        new SpringResourceTemplateResolver();
    templateResolver.setApplicationContext(applicationContext);
    templateResolver.setPrefix("classpath:/templates/html/");
    templateResolver.setTemplateMode(TemplateMode.HTML);
    templateResolver.setCacheable(false);
    templateResolver.setCheckExistence(true);
    templateResolver.setCharacterEncoding("UTF-8");
    return templateResolver;
  }

  /**
   * 模板引擎。
   *
   * @param htmlTemplateResolver HTML模板解析器
   * @return 模板引擎
   */
  @Bean
  public ISpringTemplateEngine templateEngine(
      @Autowired @Qualifier("htmlTemplateResolver") final ITemplateResolver htmlTemplateResolver) {
    final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    final Set<ITemplateResolver> resolvers = new HashSet<>();
    resolvers.add(htmlTemplateResolver);
    templateEngine.setTemplateResolvers(resolvers);
    templateEngine.setEnableSpringELCompiler(true);
    return templateEngine;
  }

  /**
   * Thymeleaf 视图解析器。
   *
   * @param templateEngine 模板引擎
   * @return 视图解析器
   */
  @Bean
  public ViewResolver viewResolver(final ISpringTemplateEngine templateEngine) {
    final ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
    viewResolver.setTemplateEngine(templateEngine);
    viewResolver.setOrder(1);
    viewResolver.setViewNames(new String[] {"*.html", "*.js", "*.css"});
    viewResolver.setCharacterEncoding("UTF-8");
    return viewResolver;
  }

}
