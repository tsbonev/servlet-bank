package core.servlet.filter;

import core.servlet.helper.LoginSession;
import core.servlet.helper.PageHandler;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/account")
public class AuthorizationFilter implements Filter {

    private FilterConfig filterConfig = null;

    private PageHandler page;

    public AuthorizationFilter(PageHandler page){
        this.page = page;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    /**
     * Authorizes whether the request with a username in
     * the parameters was made by the logged in user
     * and lets the admin through in all cases.
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
        LoginSession session = (LoginSession) req.getSession().getAttribute("authorized");

        if(session.getUsername().equalsIgnoreCase("admin")
                && session.isAuthorized()){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String username = req.getParameter("username");
        if(username == null && session.isAuthorized()){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        if(req.getParameter("username").equalsIgnoreCase(session.getUsername())
        && session.isAuthorized()){
            filterChain.doFilter(servletRequest, servletResponse);
        }
        else {
            page.redirectTo("/home", resp, req,
                    "errorMessage", "You do not have access to this page!");
        }

    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }

}
