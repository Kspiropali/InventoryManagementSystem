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

        registry.addViewController("/admin").setViewName("admin");
        registry.addViewController("/home").setViewName("products_page");
        registry.addViewController("/kiosk_login").setViewName("kiosk_login");
        registry.addViewController("/payment_success").setViewName("payment_options");
        registry.addViewController("/kiosk_checkout").setViewName("kiosk_checkout");
        registry.addViewController("http://localhost:8080/user/verifyRegistration?***").setViewName("email_confirmation");
    }
}
