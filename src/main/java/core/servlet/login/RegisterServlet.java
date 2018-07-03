package core.servlet.login;

import core.model.User;
import core.service.UserService;
import core.servlet.helpers.PageImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static String usernamePattern = "^[\\w]{5,15}$";
    private static String passwordPattern = "^[\\w]{8,20}$";

    UserService service = UserService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("title", "Register");
        PageImpl.getPage("view/user/register.jsp", req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        User user = new User();
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if(!Pattern.matches(usernamePattern, username) || !Pattern.matches(passwordPattern, password)){
            PageImpl.redirectTo("/register", resp, req,
                    "errorMessage", "Something went wrong!");
            return;
        }

        user.setUsername(username);
        user.setPassword(password);

        if(service.getUserByUsername(username).getId() != 0){
            PageImpl.redirectTo("/register", resp, req,
                    "errorMessage", "Username taken!");
        }
        else {
            service.saveUser(user);
            PageImpl.redirectTo("/home", resp, req,
                    "successMessage", "User registered successfully!");
        }

    }
}
