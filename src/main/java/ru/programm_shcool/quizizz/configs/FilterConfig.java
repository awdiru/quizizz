package ru.programm_shcool.quizizz.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.programm_shcool.quizizz.filter.AuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final AuthenticationFilter authenticationFilter;

    @Bean
    public FilterRegistrationBean<AuthenticationFilter> authenticationFilterRegistration() {
        FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(authenticationFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1); // Порядок выполнения
        return registrationBean;
    }
}
