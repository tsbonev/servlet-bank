package core.servlet.helpers;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PageImpl {


    public static void getPage(String pagePath, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        RequestDispatcher rd = req.getRequestDispatcher(pagePath);
        rd.forward(req, resp);
    }

    public static void redirectTo(String servletPath, HttpServletResponse resp, HttpServletRequest req, String messageType, String messageContent) throws IOException {

        req.getSession().setAttribute(messageType, messageContent);
        resp.sendRedirect(servletPath);

    }

}
