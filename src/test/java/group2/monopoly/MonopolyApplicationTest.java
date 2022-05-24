package group2.monopoly;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;


@Suite
@SelectClasses(value=group2.monopoly.auth.AuthTestSuite.class)
class MonopolyApplicationTest {

}