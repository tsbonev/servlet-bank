package core.servlet.login;

import core.model.User;
import core.repository.UserRepository;
import core.servlet.filter.ConnectionPerRequest;
import core.servlet.helper.Page;

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

    UserRepository repo;

    Page page;

    protected void setConnection(UserRepository repo){
        repo.setConnection(ConnectionPerRequest.connection.get());
    }

    public RegisterServlet(Page page, UserRepository repository){
        this.page = page;
        this.repo = repository;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("title", "Register");
        page.getPage("view/user/register.jsp", req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        setConnection(repo);

        User user = new User();
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if(!Pattern.matches(usernamePattern, username) || !Pattern.matches(passwordPattern, password)){
            page.redirectTo("/register", resp, req,
                    "errorMessage", "Something went wrong!");
            return;
        }

        user.setUsername(username);
        user.setPassword(password);

        if(repo.getByUsername(username).getId() != 0){
            page.redirectTo("/register", resp, req,
                    "errorMessage", "Username taken!");
        }
        else {
            repo.save(user);
            page.redirectTo("/home", resp, req,
                    "successMessage", "User registered successfully!");
        }

    }
}
