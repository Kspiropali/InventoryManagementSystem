package backend.webcontroller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebController implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        System.out.println("--------------Web pages set successfully----------");
        //mapping pages in /templates to endpoints
        registry.addViewController("/chat").setViewName("live_chat_page");
        registry.addViewController("/http://localhost:8080/user/verifyRegistration?***").setViewName("email_confirmed_page");
        registry.addViewController("/error").setViewName("error");
        registry.addViewController("/settings").setViewName("account_settings_page");
    }
}
