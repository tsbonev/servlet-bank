package core.servlet.helper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public interface Page {

    void closeConnection();
    void getPage(String pagePath, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;
    void redirectTo(String servletPath, HttpServletResponse resp,
                    HttpServletRequest req, String messageType, String messageContent) throws IOException;
}
