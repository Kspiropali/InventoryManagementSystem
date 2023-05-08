package backend.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@SuppressWarnings("deprecation")
public class SecurePolicy {
    private static final String[] WHITE_LIST_URLS = {
            "/user/register**",
            "/user/verifyRegistration**",
            "/user/resendVerificationToken**",
            "/user/loggedout**",
            "/css/**",
            "/js/**",
            "/error**",
            "/favicon.ico",
            "/payment_success",
            "/documentation",
            "/admin",
            "/kiosk"
    };


    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    private static CustomAuthEntryPoint customAuthEntryPoint;
    public SecurePolicy(CustomAuthEntryPoint customAuthEntryPoint) {
        SecurePolicy.customAuthEntryPoint = customAuthEntryPoint;
    }

    ///USER SECURITY CONFIG////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Configuration
    @Order(1)
    public static class UserSecurityConfig extends WebSecurityConfigurerAdapter {


        @Bean
        protected SessionRegistryImpl sessionRegistry() {
            return new SessionRegistryImpl();
        }

        private final UserDetailsService userService;
        private final PasswordEncoder passwordEncoder;
        //WhiteListed urls from authentication

        public UserSecurityConfig(@Qualifier("userServiceImpl") UserDetailsService userService, PasswordEncoder passwordEncoder) {
            this.userService = userService;
            this.passwordEncoder = passwordEncoder;
        }

        //Security context configurer and session provider configurer
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .logout(logout -> logout
                            .logoutUrl("/user/logout")
                            .invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID")
                    )
                    .antMatcher("/user/***")
                    .authorizeRequests().and()
                    //Cross origin enable
                    .cors().and()
                    //Cross site request forgery disable for testing purposes
                    .csrf().disable()//.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .formLogin().loginPage("/").usernameParameter("email")
                    .and()
                    .authorizeRequests()
                    //Allow all white-listed urls without authentication
                    .antMatchers(WHITE_LIST_URLS)
                    .permitAll()
                    .anyRequest()
                    .authenticated().and()
                    .httpBasic()
                    .authenticationEntryPoint(customAuthEntryPoint)
                    .and().sessionManagement().sessionFixation().migrateSession()
                    .maximumSessions(1).maxSessionsPreventsLogin(true)
                    .sessionRegistry(sessionRegistry());
        }

        //Data access object spring security configuration
        @Override
        protected void configure(AuthenticationManagerBuilder auth) {
            auth.authenticationProvider(daoAuthenticationProvider());
        }

        public DaoAuthenticationProvider daoAuthenticationProvider() {
            DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
            provider.setPasswordEncoder(passwordEncoder);
            provider.setUserDetailsService(userService);
            return provider;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /// ADMINISTRATION SECURITY CONFIGURATION//////////////////////////////////////////////////////////////////////////////////////////
    @Order(2)
    @Configuration
    public static class AdminSecurityConfig extends WebSecurityConfigurerAdapter {
        private final UserDetailsService adminService;
        private final PasswordEncoder passwordEncoder;

        public AdminSecurityConfig(@Qualifier("adminServiceImpl") UserDetailsService adminService, PasswordEncoder passwordEncoder) {
            this.adminService = adminService;
            this.passwordEncoder = passwordEncoder;
        }

        //Security context configurer and session provider configurer
        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.logout(logout -> logout
                            .logoutUrl("/admin/logout")
                            .invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID")
                    )
                    .antMatcher("/admin/**")
                    .authorizeRequests()
                    .and()
                    //Cross origin enable
                    .cors().and()
                    //Cross site request forgery disable for testing purposes
                    .csrf().disable()//.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .formLogin().loginPage("/admin").usernameParameter("email")
                    .and()
                    .authorizeRequests()
                    //Allow all white-listed urls without authentication
                    .antMatchers(WHITE_LIST_URLS).permitAll()
                    .anyRequest().authenticated().and()
                    .httpBasic()
                    .authenticationEntryPoint(customAuthEntryPoint)
                    .and().sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).sessionFixation().migrateSession()
                    .maximumSessions(1).maxSessionsPreventsLogin(true);
        }


        //Data access object spring security configuration
        @Override
        public void configure(AuthenticationManagerBuilder auth) {
            auth.authenticationProvider(daoAuthenticationProvider());
        }

        public DaoAuthenticationProvider daoAuthenticationProvider() {
            DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
            provider.setPasswordEncoder(passwordEncoder);
            provider.setUserDetailsService(adminService);
            return provider;
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ///WEBSOCKET SECURITY CONFIGURATION//////////////////////////////////////////////////////////////////////////////////////////
//    @Order(3)
//    @Configuration
//    public static class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//        private final UserService userService;
//
//        private final PasswordEncoder passwordEncoder;
//
//        public WebSecurityConfig(UserService userService, PasswordEncoder passwordEncoder) {
//            this.userService = userService;
//            this.passwordEncoder = passwordEncoder;
//        }
//
//        @Override
//        protected void configure(final HttpSecurity http) throws Exception {
//            // Websocket auth
//            http
//                    .logout(logout -> logout
//                            .logoutUrl("/websocket/logout")
//                            .logoutSuccessHandler((request, response, authentication) -> {
//                                User userDetails = (User) authentication.getPrincipal();
//                                String username = userDetails.getUsername();
//
//                                System.out.println("The user " + username + " has logged out from the socket.");
//
//                                response.sendRedirect(request.getContextPath());
//                            })
//                            .invalidateHttpSession(true)
//                            .deleteCookies("JSESSIONID")
//                    )
//                    .antMatcher("/ws/***")
//                    //Cross origin enable
//                    .cors().and()
//                    //Cross site request forgery disable for testing purposes
//                    .csrf().disable()//.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                    .formLogin().usernameParameter("email")
//                    .and()
//                    .authorizeRequests()
//                    .anyRequest().authenticated().and()
//                    .httpBasic()
//                    .authenticationEntryPoint(customAuthEntryPoint)
//                    .and().sessionManagement().sessionFixation().migrateSession()
//                    .maximumSessions(1).maxSessionsPreventsLogin(true)
//                    .expiredUrl("/user/loggedout");
//        }
//
//        @Override
//        protected void configure(AuthenticationManagerBuilder auth) {
//            auth.authenticationProvider(daoAuthenticationProvider());
//        }
//
//        public DaoAuthenticationProvider daoAuthenticationProvider() {
//            DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//            provider.setPasswordEncoder(passwordEncoder);
//            provider.setUserDetailsService((UserDetailsService) userService);
//            return provider;
//        }
//    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////KIOSK CONFIGURATION/////////////////////////////////////////////////////////////////////////////////////////////////////
    @Configuration
    @Order(3)
    public static class KioskSecurityConfig extends WebSecurityConfigurerAdapter {


        @Bean
        protected SessionRegistryImpl kioskSessionRegistry() {
            return new SessionRegistryImpl();
        }

        private final UserDetailsService kioskService;
        private final PasswordEncoder passwordEncoder;
        //WhiteListed urls from authentication


        public KioskSecurityConfig(@Qualifier("kioskServiceImpl") UserDetailsService kioskService, PasswordEncoder passwordEncoder) {
            this.kioskService = kioskService;
            this.passwordEncoder = passwordEncoder;
        }

        //Security context configurer and session provider configurer
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .logout(logout -> logout
                            .logoutUrl("/kiosk/logout")
                            .invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID")
                    )
                    .antMatcher("/kiosk/**")
                    .authorizeRequests()
                    .and()
                    //.antMatcher("/home")
                    //Cross origin enable
                    .cors().and()
                    //Cross site request forgery disable for testing purposes
                    .csrf().disable()//.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .formLogin().loginPage("/kiosk").usernameParameter("username").passwordParameter("password")
                    .successForwardUrl("/kiosk/checkout")
                    .and()
                    .authorizeRequests()
                    //Allow all white-listed urls without authentication
                    .antMatchers(WHITE_LIST_URLS)
                    .permitAll()
                    .anyRequest()
                    .authenticated().and()
                    .httpBasic()
                    .authenticationEntryPoint(customAuthEntryPoint)
                    .and().sessionManagement().sessionFixation().migrateSession()
                    .maximumSessions(1).maxSessionsPreventsLogin(true)
                    .sessionRegistry(kioskSessionRegistry());
        }

        //Data access object spring security configuration
        @Override
        protected void configure(AuthenticationManagerBuilder auth) {
            auth.authenticationProvider(daoAuthenticationProvider());
        }

        public DaoAuthenticationProvider daoAuthenticationProvider() {
            DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
            provider.setPasswordEncoder(passwordEncoder);
            provider.setUserDetailsService(kioskService);
            return provider;
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Order(4)
    @Configuration
    public static class AnalyticsSecurityConfig extends WebSecurityConfigurerAdapter {
        private final UserDetailsService analyticsService;
        private final PasswordEncoder passwordEncoder;

        public AnalyticsSecurityConfig(@Qualifier("analyticsServiceImpl") UserDetailsService analyticsService, PasswordEncoder passwordEncoder) {
            this.analyticsService = analyticsService;
            this.passwordEncoder = passwordEncoder;
        }

        //Security context configurer and session provider configurer
        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                    .logout(logout -> logout
                            .logoutUrl("/analytics/logout")
                            .invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID")
                    )
                    .antMatcher("/analytics/**")
                    //Cross origin enable
                    .cors().and()
                    //Cross site request forgery disable for testing purposes
                    .csrf().disable()//.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .formLogin().usernameParameter("email")
                    .and()
                    .authorizeRequests()
                    //Allow all white-listed urls without authentication
                    .antMatchers(WHITE_LIST_URLS).permitAll()
                    .anyRequest().authenticated().and()
                    .httpBasic()
                    .authenticationEntryPoint(customAuthEntryPoint)
                    .and().sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).sessionFixation().migrateSession()
                    .maximumSessions(1).maxSessionsPreventsLogin(true);
        }


        //Data access object spring security configuration
        @Override
        public void configure(AuthenticationManagerBuilder auth) {
            auth.authenticationProvider(daoAuthenticationProvider());
        }

        public DaoAuthenticationProvider daoAuthenticationProvider() {
            DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
            provider.setPasswordEncoder(passwordEncoder);
            provider.setUserDetailsService(analyticsService);
            return provider;
        }
    }

    @Order(5)
    @Configuration
    public static class ChatSecurityConfig extends WebSecurityConfigurerAdapter {
        private final UserDetailsService chatService;
        private final PasswordEncoder passwordEncoder;

        public ChatSecurityConfig(@Qualifier("chatWrapper") UserDetailsService chatService, PasswordEncoder passwordEncoder) {
            this.chatService = chatService;
            this.passwordEncoder = passwordEncoder;
        }

        //Security context configurer and session provider configurer
        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                    .logout(logout -> logout
                            .invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID")
                    )
                    .antMatcher("/ws/**")
                    .antMatcher("/chat/**")
                    .antMatcher("/download/**")
//                    .antMatcher("/analytics/***")
                    //Cross origin enable
                    .cors().and()
                    //Cross site request forgery disable for testing purposes
                    .csrf().disable()//.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .formLogin().usernameParameter("email")
                    .and()
                    .authorizeRequests()
                    //Allow all white-listed urls without authentication
                    .antMatchers(WHITE_LIST_URLS).permitAll()
                    .anyRequest().authenticated().and()
                    .httpBasic()
                    .authenticationEntryPoint(customAuthEntryPoint)
                    .and().sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).sessionFixation().migrateSession()
                    .maximumSessions(1).maxSessionsPreventsLogin(true);
        }


        //Data access object spring security configuration
        @Override
        public void configure(AuthenticationManagerBuilder auth) {
            auth.authenticationProvider(daoAuthenticationProvider());
        }

        public DaoAuthenticationProvider daoAuthenticationProvider() {
            DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
            provider.setPasswordEncoder(passwordEncoder);
            provider.setUserDetailsService(chatService);
            return provider;
        }
    }
}