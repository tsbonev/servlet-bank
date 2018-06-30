package core.Servlet;

import core.Model.User;
import core.Service.UserService;
import core.Servlet.Helpers.Page;

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

        req.setAttribute("title", "Login");
        Page.getPage("view/user/login.jsp", req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        User user = new User();

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        user.setUsername(username);
        user.setPassword(password);

        boolean isInSystem = service.checkUserPassword(user);

        if(isInSystem){
            req.setAttribute("successMessage", "Successfully logged in!");
            Page.getPage("/home", req, resp);
        }
        else {
            req.setAttribute("errorMessage", "User not registered!");
            doGet(req, resp);
        }


    }
}
