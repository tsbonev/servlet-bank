package core.servlet.login;

import core.model.User;
import core.repository.UserRepository;
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

    UserRepository repo;
    Page page;

    public LoginServlet(Page page, UserRepository repository){
        this.page = page;
        this.repo = repository;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setAttribute("title", "login");
        page.getPage("view/user/login.jsp", req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        User user = new User();

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){

            page.redirectTo("/login", resp, req,
                    "errorMessage", "Something went wrong!");
            return;

        }

        user.setUsername(username);
        user.setPassword(password);

        boolean isInSystem = repo.checkPassword(user);

        if(isInSystem){

            if(!UserCounter.getInstance().userIsLoggedIn(user.getUsername())){
                UserCounter.getInstance().addUserToCount(user.getUsername());
            }

            LoginSession session = new LoginSession(username, true);
            req.getSession().setAttribute("authorized", session);


            page.redirectTo("/home", resp, req,
                    "successMessage", "Successfully logged in!");
        }
        else {
            page.redirectTo("/login", resp, req,
                    "errorMessage", "User not registered!");
        }


    }
}
