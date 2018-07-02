package core.Servlet.Login;

import core.Servlet.Helpers.LoginSession;
import core.Servlet.Helpers.Page;
import core.Servlet.Helpers.UserCounter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {


        HttpSession session = req.getSession();

        LoginSession loginSession = (LoginSession) session.getAttribute("authorized");

        String sessionUsername = loginSession.getUsername();

        loginSession.setAuthorized(false);

        if(UserCounter.getInstance().userIsLoggedIn(sessionUsername)){
            UserCounter.getInstance().removeUserFromCount(sessionUsername);
        }

        UserCounter.getInstance().removeUserFromCount(
                loginSession.getUsername());

        Page.redirectTo("/home", resp, req,
        "infoMessage", "User logged out!");

    }
}