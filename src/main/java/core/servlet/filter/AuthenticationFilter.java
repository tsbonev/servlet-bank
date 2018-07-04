package core.servlet.filter;

import core.servlet.helper.LoginSessionImpl;
import core.servlet.helper.Page;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    private FilterConfig filterConfig = null;

    Page page;

    public AuthenticationFilter(Page page){
        this.page = page;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    /**
     * Authenticates a logged in user and ignores
     * authentication if the page requested is
     * home, register or login.
     *
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        LoginSessionImpl session = (LoginSessionImpl) req.getSession().getAttribute("authorized");

        if(req.getRequestURI().endsWith("/login")
                || req.getRequestURI().endsWith("/register")
                || req.getRequestURI().endsWith("/home")
                || req.getRequestURI().endsWith("/")){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        if(session != null && session.isAuthorized()){
            filterChain.doFilter(servletRequest, servletResponse);
        }
        else {
            page.redirectTo("/login", resp, req,
                    "infoMessage", "You must be logged in to view this page!");
        }

    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }

}
