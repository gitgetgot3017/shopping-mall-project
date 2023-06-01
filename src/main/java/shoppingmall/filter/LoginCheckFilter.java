package shoppingmall.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginCheckFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        //'POST /members' 요청은 LoginCheckFilter를 거칠 필요 없음
        if (httpRequest.getRequestURI().equals("/members") && httpRequest.getMethod().equals("POST")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        if (session == null) {
            httpResponse.sendRedirect("members-loginform");
            return;
        }

        chain.doFilter(request, response);
    }
}
