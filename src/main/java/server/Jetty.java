package server;

import core.repository.*;
import core.servlet.*;
import core.servlet.filter.AuthenticationFilter;
import core.servlet.filter.AuthorizationFilter;
import core.servlet.filter.ConnectionPerRequest;
import core.servlet.helper.Page;
import core.servlet.helper.PageImpl;
import core.servlet.helper.UserCounter;
import core.servlet.login.LoginServlet;
import core.servlet.login.LogoutServlet;
import core.servlet.login.RegisterServlet;
import core.servlet.transaction.AccountServlet;
import core.servlet.transaction.HistoryServlet;
import core.servlet.transaction.TransactionServlet;
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

                Page page = new PageImpl();
                UserRepository userRepo = new UserRepositoryImpl();
                TransactionRepository transactionRepo = new TransactionRepositoryImpl();

                servletContext.addServlet("home", new HomeServlet(page)).addMapping("/", "/home");
                servletContext.addServlet("login", new LoginServlet(page, userRepo)).addMapping("/login");
                servletContext.addServlet("register", new RegisterServlet(page, userRepo, transactionRepo)).addMapping("/register");
                servletContext.addServlet("logout", new LogoutServlet(page)).addMapping("/logout");
                servletContext.addServlet("account", new AccountServlet(page, transactionRepo, userRepo)).addMapping("/account");
                servletContext.addServlet("transaction", new TransactionServlet(page, userRepo, transactionRepo)).addMapping("/transaction");
                servletContext.addServlet("history", new HistoryServlet(page, transactionRepo, userRepo)).addMapping("/history");
                servletContext.addServlet("error", new ErrorHandler(page)).addMapping("/error");


                servletContext.addFilter("loginFilter", new AuthenticationFilter(page))
                        .addMappingForUrlPatterns(null, false, "/*");
                servletContext.addFilter("accountFilter", new AuthorizationFilter(page))
                        .addMappingForUrlPatterns(null, false, "/account");
                servletContext.addFilter("connectionPerRequest", new ConnectionPerRequest())
                        .addMappingForUrlPatterns(null, false, "/*");

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
