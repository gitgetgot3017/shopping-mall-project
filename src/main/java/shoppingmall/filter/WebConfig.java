package shoppingmall.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean regLoginCheckFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new LoginCheckFilter());
        filterFilterRegistrationBean.addUrlPatterns("/members", "/members-logout", "/carts/*", "/orders-form", "/orders/*");
        return filterFilterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean regAdminCheckFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new AdminCheckFilter());
        filterFilterRegistrationBean.addUrlPatterns("/items-regform", "/items/*", "/items-editform/*");
        return filterFilterRegistrationBean;
    }
}
