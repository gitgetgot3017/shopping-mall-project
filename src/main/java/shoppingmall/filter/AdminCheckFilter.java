package shoppingmall.filter;

import shoppingmall.member.constant.Role;
import shoppingmall.member.entity.Member;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static shoppingmall.member.constant.SessionConst.LOGIN_MEMBER;

public class AdminCheckFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        //'GET /items/{itemNum}' 요청은 AdminCheckFilter를 거칠 필요 없음
        //'GET /items/' 요청을 거르기 위한 로직
        if (httpRequest.getMethod().equals("GET") &&
                httpRequest.getRequestURI().contains("/items/") && !httpRequest.getRequestURI().substring(6).equals("/")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        if (session == null || !((Member) session.getAttribute(LOGIN_MEMBER)).getRole().equals(Role.ADMIN)) {
            httpResponse.sendError(401); //UNAUTHORIZED
            return;
        }

        chain.doFilter(request, response);
    }
}
