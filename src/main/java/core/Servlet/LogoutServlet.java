package core.Servlet;

import core.Servlet.Helpers.LoginSession;
import core.Servlet.Helpers.Page;
import core.Servlet.Helpers.UserCounter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        String sessionUsername = ((LoginSession)req.getSession().getAttribute("authorized"))
                .getUsername();

        ((LoginSession) req.getSession().getAttribute("authorized")).setAuthorized(false);

        if(UserCounter.getInstance().userIsLoggedIn(sessionUsername)){
            UserCounter.getInstance().removeUserFromCount(sessionUsername);
        }

        ((UserCounter)getServletContext().getAttribute("counter")).removeUserFromCount(
                ((LoginSession)req.getSession().getAttribute("authorized")).getUsername());
        req.setAttribute("infoMessage", "User logged out!");
        Page.getPage("/home", req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}
