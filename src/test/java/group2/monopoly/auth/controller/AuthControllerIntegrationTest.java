package group2.monopoly.auth.controller;

import group2.monopoly.MonopolyApplication;
import group2.monopoly.auth.repository.UserPasswordResetRepository;
import group2.monopoly.auth.repository.UserRepository;
import group2.monopoly.handlers.AuthExceptionHandler;
import group2.monopoly.handlers.RestResponseEntityExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = MonopolyApplication.class
)
@WebAppConfiguration
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerIntegrationTest {

    @Autowired
    private AuthController authController;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserPasswordResetRepository resetRepository;
    private MockMvc mvc;

    @BeforeEach
    void setup(WebApplicationContext wac) {
        this.mvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new AuthExceptionHandler(),
                        new RestResponseEntityExceptionHandler())
//                .setHan
                .build();
        resetRepository.deleteAll();
        resetRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
    }

    @DirtiesContext
    @Test
    public void Should_Status201_When_RegisteringWithGoodRequest() throws Exception {
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "username",
                            "password": "password123",
                            "confirmPassword": "password123",
                            "email": "example@example.com"
                        }
                        """)
        ).andExpect(status().isCreated()
        ).andExpect(jsonPath("$.username").value("username")
        ).andExpect(jsonPath("$.email").value("example@example.com")
        ).andExpect(jsonPath("$.id").value(1L)
        ).andExpect(jsonPath("$.password").doesNotExist());
    }

    @DirtiesContext
    @Test
    public void Should_Status400_When_RegisteringWithBadRequest() throws Exception {
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "password": "password123",
                            "email": "example@example.com"
                        }
                        """)
        ).andExpect(status().isBadRequest());
    }

    @DirtiesContext
    @Test
    public void Should_Status400_When_RegisteringWithDuplicateUser() throws Exception {
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "example",
                            "password": "nicepassword123",
                            "confirmPassword": "nicepassword123",
                            "email": "example@example.com"
                        }
                        """)
        ).andExpect(status().isCreated());

        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "example",
                            "password": "nicepassword123",
                            "confirmPassword": "nicepassword123",
                            "email": "example2@example.com"
                        }
                        """)
        ).andExpect(status().isBadRequest()
        ).andExpect(jsonPath("$.message").value("username exists"));
    }

    @DirtiesContext
    @Test
    public void givenUserAndGoodCredentials_whenLogin_thenStatus200() throws Exception {
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "example",
                            "password": "nicepassword123",
                            "confirmPassword": "nicepassword123",
                            "email": "example@example.com"
                        }
                        """)
        ).andExpect(status().isCreated());

        MvcResult result = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "example",
                                    "password": "nicepassword123"
                                }
                                """)
                ).andExpect(status().isOk()
                ).andExpect(jsonPath("$.token").isString())
                .andReturn();
    }
}
