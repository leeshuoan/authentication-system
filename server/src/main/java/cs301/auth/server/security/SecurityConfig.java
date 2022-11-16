package cs301.auth.server.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import cs301.auth.server.filter.CustomAuthenticationFilter;
import cs301.auth.server.filter.CustomAuthorizationFilter;
import cs301.auth.server.user.UserService;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserService userDetailsService;
    private final PasswordEncoder encoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // TODO Auto-generated method stub
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // TODO Auto-generated method stub
        http.cors();
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(
                authenticationManagerBean());
        customAuthenticationFilter.setFilterProcessesUrl("/api/login");
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers("GET", "/health").permitAll();
        http.authorizeRequests().antMatchers("/api/login/**", "/api/token/refresh/**", "/sso/**", "/verify/**").permitAll();
        http.authorizeRequests().antMatchers("PUT", "/api/users/approve/**").hasAnyAuthority("ROLE_USER","ROLE_SUPER_ADMIN","ROLE_ADMIN");
        http.authorizeRequests().antMatchers("DELETE", "/api/users/delete/**").hasAnyAuthority("ROLE_USER","ROLE_SUPER_ADMIN","ROLE_ADMIN");
        http.authorizeRequests().antMatchers("GET", "/api/users/**").hasAnyAuthority("ROLE_SUPER_ADMIN","ROLE_ADMIN");
        http.authorizeRequests().antMatchers("GET", "/api/profile/**").hasAnyAuthority("ROLE_USER","ROLE_SUPER_ADMIN","ROLE_ADMIN");
        http.authorizeRequests().antMatchers("POST", "/api/users/save/**").hasAnyAuthority("ROLE_ADMIN");
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // @Bean
    // CorsConfigurationSource corsConfigurationSource() {
    //     CorsConfiguration configuration = new CorsConfiguration();
    //     // chrome and firefox didn't liek the wild card * so specify the url of your app
    //     configuration.addAllowedOrigin("http:/localhost:5173");
    //     configuration.addAllowedOrigin("http://127.0.0.1:5173");
    //     configuration.addAllowedOrigin("http:/localhost:1");
    //     configuration.addAllowedOrigin("http://127.0.0.1:1");
    //     // also here the allowed heather should be added not mentiend in the docs either
    //     configuration.addAllowedHeader("*");
    //     configuration.addAllowedMethod("*");
    //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //     source.registerCorsConfiguration("/**", configuration);
    //     return source;
    // }

}
