package core.Servlet;

import core.Model.Account;
import core.Service.UserService;
import core.Servlet.Helpers.LoginCheck;
import core.Servlet.Helpers.LoginSession;
import core.Servlet.Helpers.Page;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/account")
public class AccountServlet extends HttpServlet {

    UserService userService = UserService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String username = req.getParameter("username");

        LoginSession session = (LoginSession) req.getSession().getAttribute("authorized");

        if(!LoginCheck.checkLogged(session, req, resp) || !LoginCheck.checkUsername(session, username, req, resp)){
            return;
        }

        Account account = userService.getUserAccount(username);
        req.setAttribute("balance", String.format("%.2f", account.getAmount()));

        Page.getPage("view/user/account.jsp", req, resp);
    }


}
