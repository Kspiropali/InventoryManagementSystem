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
        registry.addViewController("/user/products").setViewName("products_page");
        registry.addViewController("/kiosk").setViewName("kiosk_login");
        registry.addViewController("/payment_success").setViewName("payment_options");
        registry.addViewController("/kiosk/checkout").setViewName("kiosk_checkout");
        registry.addViewController("/admin/dashboard").setViewName("admin_dashboard");
        registry.addViewController("http://localhost:8080/user/verifyRegistration?***").setViewName("email_confirmation");
        registry.addViewController("/error").setViewName("error");
    }
}
