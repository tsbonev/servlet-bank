package core.servlet;

import core.servlet.helper.PageHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/error")
public class ErrorHandler extends HttpServlet {

    PageHandler page;

    public ErrorHandler(PageHandler page){
        this.page = page;
    }

    /**
     * Redirects to the home page with an error message.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        page.redirectTo("/home", resp, req,
                "errorMessage", "Something has happened!");

    }
}
