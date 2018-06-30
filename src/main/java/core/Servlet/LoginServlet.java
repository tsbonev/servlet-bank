package core.Servlet;

import core.Model.User;
import core.Service.UserService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    UserService service = UserService.getInstance();

    protected void getPage(String pagePath, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        RequestDispatcher rd = req.getRequestDispatcher(pagePath);
        rd.forward(req, resp);

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setAttribute("title", "Login");
        getPage("view/user/login.jsp", req, resp);

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
            getPage("view/index.jsp", req, resp);
        }
        else {
            req.setAttribute("errorMessage", "User not registered!");
            getPage("view/user/login.jsp", req, resp);
        }


    }
}
