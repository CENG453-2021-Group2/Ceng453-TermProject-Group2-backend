package group2.monopoly.auth.controller;

import com.fasterxml.jackson.databind.JsonNode;
import group2.monopoly.MonopolyApplication;
import group2.monopoly.auth.repository.UserPasswordResetRepository;
import group2.monopoly.auth.repository.UserRepository;
import group2.monopoly.mapper.ObjectMapperSingleton;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = {MonopolyApplication.class}
)
@AutoConfigureMockMvc
@WebAppConfiguration
@ContextConfiguration
@ComponentScan(basePackageClasses = {
        group2.monopoly.handlers.RestResponseEntityExceptionHandler.class,
        group2.monopoly.handlers.AuthExceptionHandler.class
})
@ActiveProfiles("test")
public class AuthControllerIntegrationTest {

    @Autowired
    private AuthController authController;


    @Autowired
    UserRepository userRepository;

    @Autowired
    UserPasswordResetRepository resetRepository;

    private MockMvc mvc;

//    @Autowired
//    private WebApplicationContext wac;

    private final String exampleSignup =
            """
                    {
                        "username": "example",
                        "password": "nicepassword123",
                        "confirmPassword": "nicepassword123",
                        "email": "example@example.com"
                    }
                    """;

    private final String exampleLogin =
            """
                    {
                        "username": "example",
                        "password": "nicepassword123"
                    }
                    """;

    private final String exampleLoginInvalidPassword = exampleLogin.replace("123", "1234");

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
        resetRepository.deleteAll();
        resetRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
    }


    private String registerAndLogin() throws Exception {
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(exampleSignup)
        ).andExpect(status().isCreated());

        MvcResult result = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(exampleLogin)
                ).andExpect(status().isOk()
                ).andExpect(jsonPath("$.token").isString())
                .andReturn();

        JsonNode node =
                ObjectMapperSingleton.getMapper().readTree(result.getResponse().getContentAsString());
        return node.get("token").textValue();
    }

    @DirtiesContext
    @Test
    public void Should_Status201_When_RegisteringWithGoodRequest() throws Exception {
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(exampleSignup)
        ).andExpect(status().isCreated()
        ).andExpect(jsonPath("$.username").value("example")
        ).andExpect(jsonPath("$.email").value("example@example.com")
        ).andExpect(jsonPath("$.id").value(1L)
        ).andExpect(jsonPath("$.password").doesNotExist());
    }

    @DirtiesContext
    @Test
    public void Should_Status400_When_RegisteringWithBadRequest() throws Exception {
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(exampleLogin)
        ).andExpect(status().isBadRequest());
    }

    @DirtiesContext
    @Test
    public void Should_Status400_When_RegisteringWithDuplicateUser() throws Exception {
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(exampleSignup)
        ).andExpect(status().isCreated());

        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(exampleSignup)
        ).andExpect(status().isBadRequest()
        ).andExpect(jsonPath("$.message").value("email exists"));
    }

    @DirtiesContext
    @Test
    public void Should_200Login_When_GoodCredentials() throws Exception {
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(exampleSignup)
        ).andExpect(status().isCreated());

        MvcResult result = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(exampleLogin)
                ).andExpect(status().isOk()
                ).andExpect(jsonPath("$.token").isString())
                .andReturn();
    }

    @DirtiesContext
    @Test
    public void Should_401Login_When_BadCredentials() throws Exception {
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(exampleSignup)
        ).andExpect(status().isCreated());

        MvcResult result = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(exampleLoginInvalidPassword)
                ).andExpect(status().isUnauthorized()
                ).andExpect(jsonPath("$.message").value("invalid password"))
                .andReturn();
    }

    @DirtiesContext
    @Test
    public void Should_200InUser_When_GoodCredentials() throws Exception {
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(exampleSignup)
        ).andExpect(status().isCreated());

        MvcResult result = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(exampleLogin)
                ).andExpect(status().isOk()
                ).andExpect(jsonPath("$.token").isString())
                .andReturn();

        JsonNode node =
                ObjectMapperSingleton.getMapper().readTree(result.getResponse().getContentAsString());
        String token = node.get("token").textValue();

        mvc.perform(get("/api/auth/user")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
        ).andExpect(status().isOk()
        ).andExpect(jsonPath("$.active").value(true)
        ).andExpect(jsonPath("$.email").value("example@example.com")
        ).andExpect(jsonPath("$.id").value(1)
        ).andExpect(jsonPath("$.roles[0]").value("USER")
        ).andExpect(jsonPath("$.username").value("example"));
    }

    @DirtiesContext
    @Test
    public void Should_401User_When_NoToken() throws Exception {
        String token = registerAndLogin();

        mvc.perform(get("/api/auth/user")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }


    @DirtiesContext
    @Test
    public void Should_401User_When_BadToken() throws Exception {
        String token = registerAndLogin();

        mvc.perform(get("/api/auth/user")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token + "x")
        ).andExpect(status().isUnauthorized());
    }

    @DirtiesContext
    @Test
    public void Should_200UserSettingsChangeUsername_When_AuthenticatedUserWithValidRequest() throws Exception {
        String token = registerAndLogin();

        mvc.perform(post("/api/auth/user")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"username": "feridun", "password":"nicepassword123"}
                        """)
                .header("Authorization", "Bearer " + token)
        ).andExpect(status().isOk()
        ).andExpect(jsonPath("$.username").value("feridun"));
    }

    @DirtiesContext
    @Test
    public void Should_401UserSettingsChangeUsername_When_AuthenticatedUserWithInvalidRequest() throws Exception {
        String token = registerAndLogin();

        mvc.perform(post("/api/auth/user")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"username": "feridun", "password":"password1234"}
                        """)
                .header("Authorization", "Bearer " + token)
        ).andExpect(status().isUnauthorized()
        ).andExpect(jsonPath("$.message").value("invalid password"));
    }

    @DirtiesContext
    @Test
    public void Should_200UserSettingsChangePasswordname_When_AuthenticatedUserWithValidRequest() throws Exception {
        String token = registerAndLogin();

        mvc.perform(post("/api/auth/user")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"newPassword":"betterpassword123", "confirmNewPassword":"betterpassword123", "password":"nicepassword123"}
                        """)
                .header("Authorization", "Bearer " + token)
        ).andExpect(status().isOk()
        );
    }
}
