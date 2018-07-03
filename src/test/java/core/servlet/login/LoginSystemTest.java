package core.servlet.login;

import core.repository.UserRepository;
import core.model.User;
import core.servlet.helper.LoginSession;
import core.servlet.helper.Page;
import core.servlet.helper.UserCounter;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

@SuppressWarnings("Duplicates")
public class LoginSystemTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @Mock
    private HttpSession session;

    @Mock
    private Page page;

    @Mock
    private Connection conn;

    private LoginServlet loginServlet;
    private RegisterServlet registerServlet;
    private LogoutServlet logoutServlet;



    @Before
    public void setUp() {

        loginServlet = new LoginServlet(page, userRepository){
            @Override
            protected void setConnection(UserRepository repo){
                repo.setConnection(conn);
            }
        };
        registerServlet = new RegisterServlet(page, userRepository){
            @Override
            protected void setConnection(UserRepository repo){
                repo.setConnection(conn);
            }
        };

        logoutServlet = new LogoutServlet(page);

    }

    @After
    public void cleanUp() {

        UserCounter.clearInstance();

    }


    final User realUser = new User("admin", "password");

    @Test
    public void loginWithCorrectAccount() throws IOException {

        context.checking(new Expectations() {{

            oneOf(userRepository).setConnection(conn);

            oneOf(req).getParameter("username");
            will(returnValue(realUser.getUsername()));
            oneOf(req).getParameter("password");
            will(returnValue(realUser.getPassword()));

            oneOf(userRepository).checkPassword(with(any(User.class)));
            will(returnValue(true));

            oneOf(req).getSession().setAttribute("authorized", with(any(LoginSession.class)));
            oneOf(page).redirectTo("/home", resp, req, "successMessage",
                    "Successfully logged in!");

        }});

        loginServlet.doPost(req, resp);

        assertThat(UserCounter.getInstance().getUsersCount(), is(1));

    }

    @Test
    public void userCounterDoesNotGoUpWithSameLogin() throws IOException {

        int count = 5;

        context.checking(new Expectations() {{

            allowing(userRepository).setConnection(conn);

            allowing(req).getParameter("username");
            will(returnValue(realUser.getUsername()));
            allowing(req).getParameter("password");
            will(returnValue(realUser.getPassword()));

            allowing(userRepository).checkPassword(with(any(User.class)));
            will(returnValue(true));

            allowing(req).getSession().setAttribute("authorized", with(any(LoginSession.class)));

            allowing(req).getSession().setAttribute("authorized", with(any(LoginSession.class)));
            allowing(page).redirectTo("/home", resp, req, "successMessage",
                    "Successfully logged in!");

        }});

        for (int i = 0; i < count; i++) {
            loginServlet.doPost(req, resp);
        }

        assertThat(UserCounter.getInstance().getUsersCount(), is(1));

    }

    @Test
    public void userCounterGoesUpWithDifferentLogins() throws IOException {

        String username1 = "admin";
        String username2 = "user";
        String password1 = "admin";
        String password2 = "user";

        context.checking(new Expectations() {{

            oneOf(userRepository).setConnection(conn);

            oneOf(req).getParameter("username");
            will(returnValue(username1));
            oneOf(req).getParameter("password");
            will(returnValue(password1));

            oneOf(userRepository).checkPassword(with(any(User.class)));
            will(returnValue(true));
            oneOf(req).getSession().setAttribute("authorized", with(any(LoginSession.class)));

            oneOf(page).redirectTo("/home", resp, req,
                    "successMessage", "Successfully logged in!");

            oneOf(userRepository).setConnection(conn);

            oneOf(req).getParameter("username");
            will(returnValue(username2));
            oneOf(req).getParameter("password");
            will(returnValue(password2));

            oneOf(userRepository).checkPassword(with(any(User.class)));
            will(returnValue(true));

            oneOf(req).getSession().setAttribute("authorized", with(any(LoginSession.class)));

            oneOf(page).redirectTo("/home", resp, req,
                    "successMessage", "Successfully logged in!");

        }});

        for (int i = 0; i < 2; i++) {
            loginServlet.doPost(req, resp);
        }

        assertThat(UserCounter.getInstance().getUsersCount(), is(2));

    }

    @Test
    public void loginWithEmptyForm() throws IOException {

        context.checking(new Expectations() {{

            oneOf(userRepository).setConnection(conn);

            oneOf(req).getParameter("username");
            will(returnValue(""));
            oneOf(req).getParameter("password");
            will(returnValue(null));

            oneOf(page).redirectTo("/login", resp, req,
                    "errorMessage", "Something went wrong!");

        }});

        loginServlet.doPost(req, resp);

    }

    @Test
    public void loginWithIncorrectAccount() throws IOException {

        context.checking(new Expectations() {{

            oneOf(userRepository).setConnection(conn);

            oneOf(req).getParameter("username");
            will(returnValue(realUser.getUsername()));
            oneOf(req).getParameter("password");
            will(returnValue(realUser.getPassword()));
            oneOf(userRepository).checkPassword(with(any(User.class)));
            will(returnValue(false));

            oneOf(page).redirectTo("/login", resp, req,
                    "errorMessage", "User not registered!");

        }});

        loginServlet.doPost(req, resp);

        assertThat(UserCounter.getInstance().getUsersCount(), is(0));

    }

    @Test
    public void userLogsOut() throws IOException {

        LoginSession loginSession = new LoginSession();

        loginSession.setUsername("admin");
        loginSession.setAuthorized(true);

        context.checking(new Expectations() {{

            oneOf(userRepository).setConnection(conn);

            oneOf(req).getParameter("username");
            will(returnValue(realUser.getUsername()));
            oneOf(req).getParameter("password");
            will(returnValue(realUser.getPassword()));
            oneOf(userRepository).checkPassword(with(any(User.class)));
            will(returnValue(true));
            oneOf(req).getSession().setAttribute("authorized", with(any(LoginSession.class)));
            oneOf(page).redirectTo("/home", resp, req,
                    "successMessage", "Successfully logged in!");

            oneOf(req).getSession();
            will(returnValue(session));
            oneOf(session).getAttribute("authorized");
            will(returnValue(loginSession));


            oneOf(page).redirectTo("/home", resp, req,
                    "infoMessage", "User logged out!");

        }});

        loginServlet.doPost(req, resp);

        assertThat(UserCounter.getInstance().getUsersCount(), is(1));

        logoutServlet.doGet(req, resp);

        assertThat(UserCounter.getInstance().getUsersCount(), is(0));

    }

    @Test
    public void usernameIsTaken() throws ServletException, IOException {

        realUser.setId(1);

        context.checking(new Expectations() {{

            oneOf(userRepository).setConnection(conn);

            oneOf(req).getParameter("username");
            will(returnValue(realUser.getUsername()));
            oneOf(req).getParameter("password");
            will(returnValue(realUser.getPassword()));
            oneOf(userRepository).getByUsername("admin");
            will(returnValue(realUser));

            oneOf(page).redirectTo("/register", resp, req,
                    "errorMessage", "Username taken!");

        }});

        registerServlet.doPost(req, resp);

    }

    @Test
    public void registerUser() throws ServletException, IOException {

        realUser.setId(0);

        context.checking(new Expectations() {{

            oneOf(userRepository).setConnection(conn);

            oneOf(req).getParameter("username");
            will(returnValue(realUser.getUsername()));
            oneOf(req).getParameter("password");
            will(returnValue(realUser.getPassword()));
            oneOf(userRepository).getByUsername("admin");
            will(returnValue(realUser));
            oneOf(userRepository).save(with(any(User.class)));


            oneOf(page).redirectTo("/home", resp, req,
                    "successMessage", "User registered successfully!");

        }});

        registerServlet.doPost(req, resp);

    }

    @Test
    public void registerWithEmptyForm() throws ServletException, IOException {

        context.checking(new Expectations() {{

            oneOf(userRepository).setConnection(conn);

            oneOf(req).getParameter("username");
            will(returnValue(""));
            oneOf(req).getParameter("password");
            will(returnValue(null));

            oneOf(page).redirectTo("/register", resp, req,
                    "errorMessage", "Something went wrong!");

        }});

        registerServlet.doPost(req, resp);

    }

}
