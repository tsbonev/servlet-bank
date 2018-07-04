package core.servlet.filter;

import core.repository.MySQLConnection;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

public class ConnectionPerRequest implements Filter {

    private FilterConfig filterConfig = null;

    public static ThreadLocal<Connection> connection;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    /**
     * Opens a new connection and saves it in a ThreadLocal variable
     * every time a request is sent to a page from
     * account, transaction, login, register or history
     * 
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        connection = new ThreadLocal<>();
        connection.set(MySQLConnection.getConnection());

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        filterChain.doFilter(req, resp);

    }

    @Override
    public void destroy() {
    }
}
