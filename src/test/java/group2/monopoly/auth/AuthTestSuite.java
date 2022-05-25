package group2.monopoly.auth;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({"group2.monopoly.auth.controller",
"group2.monopoly.auth.repository",
"group2.monopoly.auth.service"})
public class AuthTestSuite {
}
