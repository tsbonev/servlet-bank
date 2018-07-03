package core.servlet;

import core.servlet.helper.Page;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/error")
public class ErrorHandler extends HttpServlet {

    Page page;

    public ErrorHandler(Page page){
        this.page = page;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        page.redirectTo("/home", resp, req,
                "errorMessage", "Something has happened!");

    }
}
