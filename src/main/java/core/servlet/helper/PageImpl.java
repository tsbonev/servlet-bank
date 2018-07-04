package core.servlet.helper;

import core.servlet.filter.ConnectionPerRequest;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class PageImpl implements Page {

    public void closeConnection() {
        try {
            ConnectionPerRequest.connection.get().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a jsp page with the forward method and closes the jdbc connection.
     *
     * @param pagePath page to find
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException
     * @throws IOException
     */
    public void getPage(String pagePath, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        RequestDispatcher rd = req.getRequestDispatcher(pagePath);
        rd.forward(req, resp);
        closeConnection();
    }

    /**
     * Redirects to a servlet or jsp page and saves
     * a message type and content into the request session,
     * closes the connection after it has redirected.
     *
     * @param servletPath to redirect to
     * @param resp servlet response
     * @param req servlet request
     * @param messageType type of the message
     * @param messageContent content of the message
     * @throws IOException
     */
    public void redirectTo(String servletPath, HttpServletResponse resp, HttpServletRequest req, String messageType, String messageContent) throws IOException{

        req.getSession().setAttribute(messageType, messageContent);
        resp.sendRedirect(servletPath);
        closeConnection();

    }

}
