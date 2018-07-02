package core.Servlet.Filter;

import core.Servlet.Helpers.LoginSession;
import core.Servlet.Helpers.Page;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    private FilterConfig filterConfig = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        LoginSession session = (LoginSession) req.getSession().getAttribute("authorized");

        if(req.getRequestURI().contains("login")
                || req.getRequestURI().contains("register")
                || req.getRequestURI().contains("home")
                || req.getRequestURI().endsWith("/")){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        if(session != null && session.isAuthorized()){
            filterChain.doFilter(servletRequest, servletResponse);
        }
        else {
            Page.redirectTo("/login", resp, req,
                    "infoMessage", "You must be logged in to view this page!");
        }

    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }

}
