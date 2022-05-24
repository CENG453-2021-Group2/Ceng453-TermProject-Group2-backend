package group2.monopoly.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import group2.monopoly.auth.entity.User;
import group2.monopoly.auth.payload.SignUpDto;
import group2.monopoly.auth.service.UserPasswordResetService;
import group2.monopoly.auth.service.UserService;
import group2.monopoly.handlers.AuthExceptionHandler;
import group2.monopoly.handlers.RestResponseEntityExceptionHandler;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.entry;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Auth controller and validation logic tests using mock services.
 * <br>
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private static User user;
    private static SignUpDto signUpDto;

    static {
        signUpDto = new SignUpDto();
        signUpDto.setUsername("username");
        signUpDto.setEmail("example@example.com");
        signUpDto.setPassword("password123");
        signUpDto.setConfirmPassword("password123");
        user = new User("username", "example@example.com", "password123");
        user.setId(1L);
    }

    private MockMvc mvc;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private UserPasswordResetService resetService;

    @InjectMocks
    private AuthController controller;

    private JacksonTester<User> jsonUser;
    private JacksonTester<SignUpDto> jsonSignupDto;

    @BeforeEach
    public void setUp() {
        JacksonTester.initFields(this, new ObjectMapper());
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new AuthExceptionHandler())
                .setControllerAdvice(new RestResponseEntityExceptionHandler())
                .build();
    }

    @Test
    public void Should_RegisterUser_WhenValidRequest() throws Exception {
        given(userService.createUser(signUpDto))
                .willReturn(Either.right(user));
//        assertThatJson
        MockHttpServletResponse response = mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSignupDto.write(signUpDto).getJson()))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

        assertThatJson(response.getContentAsString())
                .and(
                        o -> o.isObject().contains(
                                entry("username", "username"),
                                entry("email", "example@example.com"),
                                entry("id", 1L)
                        ).doesNotContainKey("password"),
                        o -> o.node("roles").isArray().containsExactlyInAnyOrder("USER")
                );

    }

    @Test
    public void ShouldNot_RegisterUser_WhenEmailMissing() throws Exception {
        SignUpDto dto = new SignUpDto();
        dto.setUsername(signUpDto.getUsername());
        dto.setPassword(signUpDto.getPassword());
        dto.setConfirmPassword(signUpDto.getConfirmPassword());


        MockHttpServletResponse response = mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonSignupDto.write(dto).getJson())).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThatJson(response.getContentAsString())
                .and(
                        o -> o.node("message").asString().isEqualTo("One or more fields could not be validated."),
                        o -> o.node("errors").node("email").isArray().containsExactlyInAnyOrder("must not be null")
                );
    }

    @Test
    public void ShouldNot_RegisterUser_WhenEmailInvalid() throws Exception {
        SignUpDto dto = new SignUpDto();
        dto.setUsername(signUpDto.getUsername());
        dto.setPassword(signUpDto.getPassword());
        dto.setConfirmPassword(signUpDto.getConfirmPassword());
        dto.setEmail("invalid email");


        MockHttpServletResponse response = mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonSignupDto.write(dto).getJson())).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThatJson(response.getContentAsString())
                .and(
                        o -> o.node("message").asString().isEqualTo("One or more fields could not be validated."),
                        o -> o.node("errors").node("email").isArray().containsExactlyInAnyOrder("must be a well-formed email address")
                );
    }

    @Test
    public void ShouldNot_RegisterUser_WhenUsernameMissing() throws Exception {
        SignUpDto dto = new SignUpDto();
        dto.setEmail(signUpDto.getEmail());
        dto.setPassword(signUpDto.getPassword());
        dto.setConfirmPassword(signUpDto.getConfirmPassword());


        MockHttpServletResponse response = mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonSignupDto.write(dto).getJson())).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThatJson(response.getContentAsString())
                .and(
                        o -> o.node("message").asString().isEqualTo("One or more fields could not be validated."),
                        o -> o.node("errors").node("username").isArray().containsExactlyInAnyOrder("must not be null")
                );
    }

    @Test
    public void ShouldNot_RegisterUser_WhenUsernameInvalid() throws Exception {
        SignUpDto dto = new SignUpDto();
        dto.setEmail(signUpDto.getEmail());
        dto.setPassword(signUpDto.getPassword());
        dto.setConfirmPassword(signUpDto.getConfirmPassword());
        dto.setUsername("foo");


        MockHttpServletResponse response = mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonSignupDto.write(dto).getJson())).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThatJson(response.getContentAsString())
                .and(
                        o -> o.node("message").asString().isEqualTo("One or more fields could not be validated."),
                        o -> o.node("errors").node("username").isArray().containsExactlyInAnyOrder("username must be an alphanumeric string of length between 4 and 16")
                );
    }

    @Test
    public void ShouldNot_RegisterUser_WhenPasswordsNotMatching() throws Exception {
        SignUpDto dto = new SignUpDto();
        dto.setUsername(signUpDto.getUsername());
        dto.setEmail(signUpDto.getEmail());
        dto.setPassword(signUpDto.getPassword());
        dto.setConfirmPassword("hehehehe1");


        MockHttpServletResponse response = mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonSignupDto.write(dto).getJson())).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThatJson(response.getContentAsString())
                .and(
                        o -> o.node("message").asString().isEqualTo("One or more fields could not be validated."),
                        o -> o.node("errors").node("password").isArray().containsExactlyInAnyOrder("passwords should be matching")
                );
    }
}