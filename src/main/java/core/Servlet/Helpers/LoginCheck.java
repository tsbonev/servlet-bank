package core.Servlet.Helpers;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginCheck {

    public static boolean checkLogged(LoginSession session, HttpServletRequest req, HttpServletResponse resp) throws IOException {

        if(session == null){

            Page.redirectTo("login", resp, req,
                    "infoMessage", "You must be logged in to see this page!");
            return false;
        }

        if(!session.isAuthorized()){
            Page.redirectTo("home", resp, req,
                    "errorMessage", "You do not have access to that page!");
            return false;
        }

        return true;

    }

    public static boolean checkUsername(LoginSession session, String username, HttpServletRequest req, HttpServletResponse resp) throws IOException {

        if(!session.getUsername().equals(username)){
            Page.redirectTo("home", resp, req,
                    "errorMessage", "You do not have access to that page!");
            return false;
        }

        return true;

    }

}
