package org.gradle;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WebTest {

    @Autowired
    private MockMvc mockMvc;
    /*
     * Functions https://docs.spring.io/spring-security/site/docs/current/reference/html/test-mockmvc.html
     */
    @Test
    public void shouldReturnDefaultMessage() throws Exception {
        this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("it worked!")));
    }
    
    @Test
    public void expectFailGettingPage() throws Exception {
    	this.mockMvc.perform(get("/blahblah")).andExpect(status().is4xxClientError());
    }
    
    @Test
    public void webCouldAccessDatabase() throws Exception {
        this.mockMvc.perform(get("/employees")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Robert")));
    }
}