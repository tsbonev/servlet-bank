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

    public void getPage(String pagePath, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        RequestDispatcher rd = req.getRequestDispatcher(pagePath);
        rd.forward(req, resp);
        closeConnection();
    }

    public void redirectTo(String servletPath, HttpServletResponse resp, HttpServletRequest req, String messageType, String messageContent) throws IOException{

        req.getSession().setAttribute(messageType, messageContent);
        resp.sendRedirect(servletPath);
        closeConnection();

    }

}
