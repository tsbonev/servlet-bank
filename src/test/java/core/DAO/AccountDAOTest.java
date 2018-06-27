package core.DAO;

import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class AccountDAOTest {


    AccountDAOImpl dao = new AccountDAOImpl();

    @Ignore
    @Test
    public void createTable(){

        dao.createAccountTable();



    }

}
