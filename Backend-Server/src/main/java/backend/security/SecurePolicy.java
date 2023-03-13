package backend.security;

import backend.kiosk.KioskServiceImpl;
import backend.user.User;
import backend.user.UserService;
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
            "/home",
            "/admin",
            "/payment_success",
            "/documentation",
    };

    public SecurePolicy(CustomAuthEntryPoint customAuthEntryPoint) {
        SecurePolicy.customAuthEntryPoint = customAuthEntryPoint;
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    private static CustomAuthEntryPoint customAuthEntryPoint;

    @Configuration
    @Order(1)
    public static class UserSecurityConfig extends WebSecurityConfigurerAdapter {


        @Bean
        protected SessionRegistryImpl sessionRegistry(){
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
                            .logoutSuccessHandler((request, response, authentication) -> {
                                if(authentication != null){
                                    User userDetails = (User) authentication.getPrincipal();
                                    String username = userDetails.getUsername();

                                    System.out.println("The user " + username + " has logged out.");

                                    response.sendRedirect(request.getContextPath());
                                }

                            })
                            .invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID")
                    )
                    .antMatcher("/user/***")
                    //.antMatcher("/home")
                    //Cross origin enable
                    .cors().and()
                    //Cross site request forgery disable for testing purposes
                    .csrf().disable()//.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .formLogin().usernameParameter("email")
                    .successForwardUrl("/user/login")
                    .failureHandler((request, response, exception) -> {
                        String email = request.getParameter("email");
                        try {
                            String error = exception.getMessage();
                            String userIfExistsEmail = userService.loadUserByUsername(email).getUsername();
                            if (userIfExistsEmail.isEmpty()) {
                                throw new Exception("A failed User login attempt with email: "
                                        + userIfExistsEmail + ". Reason: " + error);
                            }

                            throw new Exception("A failed login attempt with email: "
                                    + userIfExistsEmail + ". Reason: " + error);
                        } catch (Exception e) {
                            //event notifier increase user's failed count + 1
                        }
                    }).and()
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
                    .expiredUrl("/user/loggedout").sessionRegistry(sessionRegistry());
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
            http
                    .logout(logout -> logout
                            .logoutUrl("/admin/logout")
                            .logoutSuccessHandler((request, response, authentication) -> {
                                User userDetails = (User) authentication.getPrincipal();
                                String username = userDetails.getUsername();

                                System.out.println("The Admin " + username + " has logged out.");

                                response.sendRedirect(request.getContextPath());
                            })
                            .invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID")
                    )
                    .antMatcher("/admin/***")
                    //Cross origin enable
                    .cors().and()
                    //Cross site request forgery disable for testing purposes
                    .csrf().disable()//.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .formLogin().loginPage("/login").usernameParameter("email")
                    .successForwardUrl("/admin/login")
                    .failureHandler((request, response, exception) -> {
                        String email = request.getParameter("email");
                        try {
                            String error = exception.getMessage();
                            String adminIfExistsEmail = adminService.loadUserByUsername(email).getUsername();
                            if (adminIfExistsEmail.isEmpty()) {
                                throw new Exception("A failed Admin login attempt with email: "
                                        + adminIfExistsEmail + ". Reason: " + error);
                            }

                            throw new Exception("A failed login attempt with email: "
                                    + adminIfExistsEmail + ". Reason: " + error);
                        } catch (Exception e) {
                            //event notifier increase admin's failed count + 1
                        }
                    }).and()
                    .authorizeRequests()
                    //Allow all white-listed urls without authentication
                    .antMatchers(WHITE_LIST_URLS).permitAll()
                    .anyRequest().authenticated().and()
                    .httpBasic()
                    .authenticationEntryPoint(customAuthEntryPoint)
                    .and().sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).sessionFixation().migrateSession()
                    .maximumSessions(1).maxSessionsPreventsLogin(true)
                    .expiredUrl("/admin/loggedout");
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

    @Order(3)
    @Configuration
    public static class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        private final UserService userService;

        private final PasswordEncoder passwordEncoder;

        public WebSecurityConfig(UserService userService, PasswordEncoder passwordEncoder) {
            this.userService = userService;
            this.passwordEncoder = passwordEncoder;
        }

        @Override
        protected void configure(final HttpSecurity http) throws Exception {
            // Websocket auth
            http
                    .logout(logout -> logout
                            .logoutUrl("/websocket/logout")
                            .logoutSuccessHandler((request, response, authentication) -> {
                                User userDetails = (User) authentication.getPrincipal();
                                String username = userDetails.getUsername();

                                System.out.println("The user " + username + " has logged out from the socket.");

                                response.sendRedirect(request.getContextPath());
                            })
                            .invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID")
                    )
                    .antMatcher("/ws/***")
                    //Cross origin enable
                    .cors().and()
                    //Cross site request forgery disable for testing purposes
                    .csrf().disable()//.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .formLogin().usernameParameter("email")
                    .and()
                    .authorizeRequests()
                    .anyRequest().authenticated().and()
                    .httpBasic()
                    .authenticationEntryPoint(customAuthEntryPoint)
                    .and().sessionManagement().sessionFixation().migrateSession()
                    .maximumSessions(1).maxSessionsPreventsLogin(true)
                    .expiredUrl("/user/loggedout");
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) {
            auth.authenticationProvider(daoAuthenticationProvider());
        }

        public DaoAuthenticationProvider daoAuthenticationProvider() {
            DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
            provider.setPasswordEncoder(passwordEncoder);
            provider.setUserDetailsService((UserDetailsService) userService);
            return provider;
        }
    }


    ////Kiosk configuration
    @Configuration
    @Order(4)
    public static class KioskSecurityConfig extends WebSecurityConfigurerAdapter {


        @Bean
        protected SessionRegistryImpl kioskSessionRegistry(){
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
                            .logoutSuccessHandler((request, response, authentication) -> {
                                if(authentication != null){
                                    User userDetails = (User) authentication.getPrincipal();
                                    String username = userDetails.getUsername();

                                    System.out.println("The kiosk " + username + " has been logged out and thus deactivated.");

                                    response.sendRedirect(request.getContextPath());
                                }

                            })
                            .invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID")
                    )
                    .antMatcher("/kiosk/***")
                    //.antMatcher("/home")
                    //Cross origin enable
                    .cors().and()
                    //Cross site request forgery disable for testing purposes
                    .csrf().disable()//.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .formLogin().usernameParameter("username").passwordParameter("password")
                    .successForwardUrl("/kiosk/login")
                    .failureHandler((request, response, exception) -> {
                        String kioskId = request.getParameter("kioskId");
                        try {
                            String error = exception.getMessage();
                            String userIfExistsEmail = kioskService.loadUserByUsername(kioskId).getPassword();
                            if (userIfExistsEmail.isEmpty()) {
                                throw new Exception("A failed Kiosk login attempt with email: "
                                        + userIfExistsEmail + ". Reason: " + error);
                            }

                            throw new Exception("A failed login attempt with email: "
                                    + userIfExistsEmail + ". Reason: " + error);
                        } catch (Exception e) {
                            //event notifier increase user's failed count + 1
                        }
                    }).and()
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
                    .expiredUrl("/user/loggedout").sessionRegistry(kioskSessionRegistry());
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











}