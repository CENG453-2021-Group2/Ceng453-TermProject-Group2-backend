package group2.monopoly.auth;

import group2.monopoly.MonopolyApplication;
import group2.monopoly.player.PlayerRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = WebEnvironment.MOCK,
        classes = MonopolyApplication.class
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;


    @Transactional
    @Test
    public void givenGoodRequest_whenRegister_thenStatus200() throws Exception {
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "username",
                            "password": "password123",
                            "email": "example@example.com"
                        }
                        """)
        ).andExpect(status().isOk()
        ).andExpect(jsonPath("$.success").value(true)
        ).andExpect(jsonPath("$.message").value("signed up"));
    }

    @Transactional
    @Test
    public void givenBadRequestNoUsername_whenRegister_thenStatus400() throws Exception {
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

    @Transactional
    @Test
    public void givenBadRequestNoEmail_whenRegister_thenStatus400() throws Exception {
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "username",
                            "password": "password123",
                        }
                        """)
        ).andExpect(status().isBadRequest());
    }

    @Transactional
    @Test
    public void givenBadRequestNoPassword_whenRegister_thenStatus400() throws Exception {
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "username",
                            "email": "example@example.com"
                        }
                        """)
        ).andExpect(status().isBadRequest());
    }

    @Transactional
    @Test
    public void givenBadRequestWeakPassword_whenRegister_thenStatus400() throws Exception {
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "username",
                            "password": "weakaf",
                            "email": "example@example.com"
                        }
                        """)
        ).andExpect(status().isBadRequest());
    }

    @Transactional
    @Test
    public void givenBadRequestInvalidEmail_whenRegister_thenStatus400() throws Exception {
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "username",
                            "password": "nicepassword123",
                            "email": "bogus"
                        }
                        """)
        ).andExpect(status().isBadRequest());
    }

    @Transactional
    @Test
    public void givenBadRequestInvalidUsername_whenRegister_thenStatus400() throws Exception {
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "unnecessarilylongpasswordthatislikelytoberejected",
                            "password": "nicepassword123",
                            "email": "example@example.com"
                        }
                        """)
        ).andExpect(status().isBadRequest()
        ).andExpect(jsonPath("$.details.username[0]").isNotEmpty());
    }

    @Transactional
    @Test
    public void givenDuplicateUsername_whenRegister_thenStatus400() throws Exception {
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "example",
                            "password": "nicepassword123",
                            "email": "example@example.com"
                        }
                        """)
        ).andExpect(status().isOk());

        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "example",
                            "password": "nicepassword123",
                            "email": "example2@example.com"
                        }
                        """)
        ).andExpect(status().isBadRequest());
    }

    @Transactional
    @Test
    public void givenDuplicateEmail_whenRegister_thenStatus400() throws Exception {
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "example",
                            "password": "nicepassword123",
                            "email": "example@example.com"
                        }
                        """)
        ).andExpect(status().isOk());

        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "example2",
                            "password": "nicepassword123",
                            "email": "example@example.com"
                        }
                        """)
        ).andExpect(status().isBadRequest());
    }

    @Transactional
    @Test
    public void givenUserAndGoodCredentials_whenLogin_thenStatus200() throws Exception {
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "example",
                            "password": "nicepassword123",
                            "email": "example@example.com"
                        }
                        """)
        ).andExpect(status().isOk());
        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "example",
                            "password": "nicepassword123"
                        }
                        """)
        ).andExpect(status().isOk()
        ).andExpect(jsonPath("$.success").value(true)
        ).andExpect(jsonPath("$.message").value("logged in"));
    }
    @Transactional
    @Test

    public void BadCredentials_whenLogin_thenStatus401() throws Exception {
        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "example",
                            "password": "nicepassword123"
                        }
                        """)
        ).andExpect(status().isUnauthorized()
        ).andExpect(jsonPath("$.success").value(false)
        ).andExpect(jsonPath("$.message").value("Bad credentials"));
    }
}
