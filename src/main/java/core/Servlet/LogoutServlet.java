package core.Servlet;

import core.Servlet.Helpers.LoginSession;
import core.Servlet.Helpers.Page;
import core.Servlet.Helpers.UserCounter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        HttpSession session = req.getSession();

        LoginSession loginSession = (LoginSession) session.getAttribute("authorized");

        String sessionUsername = loginSession.getUsername();

        loginSession.setAuthorized(false);

        if(UserCounter.getInstance().userIsLoggedIn(sessionUsername)){
            UserCounter.getInstance().removeUserFromCount(sessionUsername);
        }

        UserCounter.getInstance().removeUserFromCount(
                loginSession.getUsername());

        req.setAttribute("infoMessage", "User logged out!");
        Page.getPage("/home", req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}
