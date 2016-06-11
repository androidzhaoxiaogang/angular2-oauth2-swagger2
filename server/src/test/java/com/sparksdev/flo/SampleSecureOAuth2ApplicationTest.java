package com.sparksdev.flo;

/**
 * @author bengill
 */

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparksdev.flo.domain.user.User;
import com.sparksdev.flo.domain.user.dto.UserDto;
import com.sparksdev.flo.domain.user.repository.UserRepository;
import com.sparksdev.flo.server.ServerApplication;

import java.util.Map;


/**
 * Series of automated integration tests to verify proper behavior of auto-configured,
 * OAuth2-secured system
 *
 * @author Greg Turnquist
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SampleSecureOAuth2ApplicationTest {

    @Autowired
    WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    FilterChainProxy filterChain;

    private MockMvc mvc;

    private String userName = "auser";
    private String userName2 = "auser2";


    //@Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {
        this.mvc = webAppContextSetup(this.context).addFilters(this.filterChain).build();
        SecurityContextHolder.clearContext();


        userRepository.deleteAll();
        final User user = new User();
        user.setUsername("system");
        user.setPassword("free4all");
        userRepository.save(userRepository.save(user));
       // userRepository.save(userRepository.save(new User(userName)));
       // userRepository.save(userRepository.save(new User(userName2)));
    }

    @Test
    public void everythingIsSecuredByDefault() throws Exception {
        this.mvc.perform(get("/").accept(MediaTypes.HAL_JSON))
                .andExpect(status().isUnauthorized()).andDo(print());
        this.mvc.perform(get("/flights").accept(MediaTypes.HAL_JSON))
                .andExpect(status().isUnauthorized()).andDo(print());
        this.mvc.perform(get("/flights/1").accept(MediaTypes.HAL_JSON))
                .andExpect(status().isUnauthorized()).andDo(print());
        this.mvc.perform(get("/alps").accept(MediaTypes.HAL_JSON))
                .andExpect(status().isUnauthorized()).andDo(print());
    }

    @Test
    @Ignore
    public void accessingRootUriPossibleWithUserAccount() throws Exception {
        String header = "Basic " + new String(Base64.encode("greg:turnquist".getBytes()));
        this.mvc.perform(
                get("/").accept(MediaTypes.HAL_JSON).header("Authorization", header))
                .andExpect(
                        header().string("Content-Type", MediaTypes.HAL_JSON.toString()))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    public void useAppSecretsPlusUserAccountToGetBearerToken() throws Exception {
        String header = "Basic " + new String(Base64.encode("foo:bar".getBytes()));
        MvcResult result = this.mvc
                .perform(post("/oauth/token").header("Authorization", header)
                        .param("grant_type", "password").param("scope", "read")
                        .param("username", "system").param("password", "free4all"))
                .andDo(print()).andExpect(status().isOk()).andReturn();
        Object accessToken = this.objectMapper
                .readValue(result.getResponse().getContentAsString(), Map.class)
                .get("access_token");
        MvcResult usersAction = this.mvc
                .perform(get("/users").accept(MediaTypes.HAL_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(header().string("Content-Type",
                        MediaTypes.HAL_JSON.toString() + ";charset=UTF-8"))
                .andExpect(status().isOk()).andDo(print()).andReturn();

        final String jsonArray = usersAction.getResponse().getContentAsString();

        System.out.println("strValue is :" + jsonArray);


        UserDto[] asList = objectMapper.readValue(jsonArray, new TypeReference<UserDto[]>() {});



        assertThat(asList.length, is(1));
        assertThat(asList[0].getUsername(), is("admin"));
    }

}