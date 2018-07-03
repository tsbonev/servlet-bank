package core.servlet.user;

import core.model.Account;
import core.service.UserService;
import core.servlet.helpers.Page;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/account")
public class AccountServlet extends HttpServlet {

    UserService userService = UserService.getInstance();

    Page page;
    public AccountServlet(Page page){
        this.page = page;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String username = req.getParameter("username");

        Account account = userService.getUserAccount(username);

        if(account.getId() == 0){
            page.redirectTo("/home", resp, req,
                    "errorMessage", "No such account was found!");
            return;
        }

        req.setAttribute("balance", account.getAmountFormatted());

        page.getPage("view/user/account.jsp", req, resp);
    }


}
