package server;

import core.Servlet.*;
import core.Servlet.Filter.AuthenticationFilter;
import core.Servlet.Filter.AuthorizationFilter;
import core.Servlet.Helpers.UserCounter;
import core.Servlet.Login.LoginServlet;
import core.Servlet.Login.LogoutServlet;
import core.Servlet.Login.RegisterServlet;
import core.Servlet.Transaction.HistoryServlet;
import core.Servlet.Transaction.TransactionServlet;
import core.Servlet.User.AccountServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public final class Jetty {
    private final Server server;

    public Jetty(int port) {
        this.server = new Server(port);
    }

    public void start() {
        WebAppContext servletContext = new WebAppContext();
        servletContext.setResourceBase("web/WEB-INF");
        servletContext.setContextPath("/");

        org.eclipse.jetty.webapp.Configuration.ClassList classlist = org.eclipse.jetty.webapp.Configuration.ClassList.setServerDefault(server);
        classlist.addAfter("org.eclipse.jetty.webapp.FragmentConfiguration", "org.eclipse.jetty.plus.webapp.EnvConfiguration", "org.eclipse.jetty.plus.webapp.PlusConfiguration");
        classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration", "org.eclipse.jetty.annotations.AnnotationConfiguration");

        servletContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",".*/[^/]*jstl.*\\.jar$");

        servletContext.addEventListener(new ServletContextListener() {

            public void contextInitialized(ServletContextEvent servletContextEvent) {
                ServletContext servletContext = servletContextEvent.getServletContext();

                servletContext.setAttribute("counter", UserCounter.getInstance());

                servletContext.addServlet("home", new HomeServlet()).addMapping("/", "/home");
                servletContext.addServlet("login", new LoginServlet()).addMapping("/login");
                servletContext.addServlet("register", new RegisterServlet()).addMapping("/register");
                servletContext.addServlet("logout", new LogoutServlet()).addMapping("/logout");
                servletContext.addServlet("account", new AccountServlet()).addMapping("/account");
                servletContext.addServlet("transaction", new TransactionServlet()).addMapping("/transaction");
                servletContext.addServlet("history", new HistoryServlet()).addMapping("/history");
                servletContext.addServlet("error", new ErrorHandler()).addMapping("/error");


                servletContext.addFilter("loginFilter", new AuthenticationFilter())
                        .addMappingForUrlPatterns(null, false, "/*");
                servletContext.addFilter("accountFilter", new AuthorizationFilter())
                        .addMappingForUrlPatterns(null, false, "/account");

            }

            public void contextDestroyed(ServletContextEvent servletContextEvent) {

            }
        });

        ContextHandler staticResourceHandler = new ContextHandler();
        staticResourceHandler.setContextPath("/css");
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase("src/main/resources/static/css");

        staticResourceHandler.setHandler(resourceHandler);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{staticResourceHandler, servletContext});

        server.setHandler(handlers);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
