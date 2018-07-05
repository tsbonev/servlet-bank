package core.servlet.login;

import core.servlet.helper.SessionHandler;
import core.servlet.helper.PageHandler;
import core.servlet.helper.UserCounter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    PageHandler page;

    public LogoutServlet(PageHandler page){
        this.page = page;
    }

    /**
     * Clears the authorized attribute of the session.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        HttpSession session = req.getSession();

        SessionHandler loginSession = (SessionHandler) session.getAttribute("authorized");

        String sessionUsername = loginSession.getUsername();

        loginSession.setAuthorized(false);

        UserCounter counter = UserCounter.getInstance();

        if(counter.userIsLoggedIn(sessionUsername)){
            counter.removeUserFromCount(sessionUsername);
        }

        page.redirectTo("/home", resp, req,
        "infoMessage", "User logged out!");
    }
}
