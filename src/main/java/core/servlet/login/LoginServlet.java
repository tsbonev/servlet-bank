package core.servlet.login;

import core.model.User;
import core.service.UserService;
import core.servlet.helpers.LoginSession;
import core.servlet.helpers.Page;
import core.servlet.helpers.UserCounter;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    UserService service = UserService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setAttribute("title", "login");
        Page.getPage("view/user/login.jsp", req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        User user = new User();

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){

            Page.redirectTo("/login", resp, req,
                    "errorMessage", "Something went wrong!");
            return;

        }

        user.setUsername(username);
        user.setPassword(password);

        boolean isInSystem = service.checkUserPassword(user);

        if(isInSystem){

            if(!UserCounter.getInstance().userIsLoggedIn(user.getUsername())){
                UserCounter.getInstance().addUserToCount(user.getUsername());
            }

            LoginSession session = new LoginSession(username, true);
            req.getSession().setAttribute("authorized", session);


            Page.redirectTo("/home", resp, req,
                    "successMessage", "Successfully logged in!");
        }
        else {
            Page.redirectTo("/login", resp, req,
                    "errorMessage", "User not registered!");
        }


    }
}
