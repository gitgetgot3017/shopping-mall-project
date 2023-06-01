package shoppingmall.filter;

import shoppingmall.member.constant.Role;
import shoppingmall.member.entity.Member;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static shoppingmall.member.constant.SessionConst.LOGIN_MEMBER;

public class HeaderNavFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = ((HttpServletRequest) request);
        String requestURI = httpRequest.getRequestURI();

        //동일한 httpRequest.setAttribute()가 여러 번 실행되는 것을 방지하기 위함
        if (requestURI.contains("/image/") || requestURI.contains("custom.css") || requestURI.contains("bootstrap.css")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            httpRequest.setAttribute("login", true); //로그인 상태
            if (((Member) session.getAttribute(LOGIN_MEMBER)).getRole().equals(Role.ADMIN)) {
                httpRequest.setAttribute("admin", true); //ADMIN 로그인 상태
            }
        }

        chain.doFilter(request, response);
    }
}
