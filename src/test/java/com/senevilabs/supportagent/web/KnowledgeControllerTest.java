package com.senevilabs.supportagent.web;

import com.senevilabs.supportagent.knowledge.KnowledgeBaseService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KnowledgeController.class)
class KnowledgeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    KnowledgeBaseService kbService;

    @Test
    void postKnowledge_returns201() throws Exception {
        mockMvc.perform(post("/api/knowledge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content":"To reset password, go to Settings > Security."}
                                """))
                .andExpect(status().isCreated());

        verify(kbService).add("To reset password, go to Settings > Security.");
    }

    @Test
    void postKnowledge_rejectsBlankContent() throws Exception {
        mockMvc.perform(post("/api/knowledge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content":"  "}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchKnowledge_returnsResults() throws Exception {
        when(kbService.search("password")).thenReturn(List.of(
                Document.builder().text("Reset via Settings > Security").score(0.92).build()));

        mockMvc.perform(get("/api/knowledge/search").param("q", "password"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Reset via Settings > Security"))
                .andExpect(jsonPath("$[0].score").value(0.92));
    }
}