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

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    UserService service = UserService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("title", "Register");
        Page.getPage("view/user/register.jsp", req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        User user = new User();
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        user.setUsername(username);
        user.setPassword(password);

        if(service.getUserByUsername(username).getId() != 0){
            req.setAttribute("title", "Register");
            req.setAttribute("errorMessage", "Username taken!");
            this.doGet(req, resp);
        }
        else {
            req.setAttribute("title", "Login");
            service.saveUser(user);
            req.setAttribute("successMessage", "User registered successfully!");
            Page.getPage("/home", req, resp);
        }

    }
}
