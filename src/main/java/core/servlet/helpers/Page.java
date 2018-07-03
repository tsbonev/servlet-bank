package core.servlet.helpers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Page {

    void getPage(String pagePath, HttpServletRequest req, HttpServletResponse resp);
    void redirectTo(String servletPath, HttpServletResponse resp,
                    HttpServletRequest req, String messageType, String messageContent);
}
