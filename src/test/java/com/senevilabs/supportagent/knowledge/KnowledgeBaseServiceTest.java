package com.senevilabs.supportagent.knowledge;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KnowledgeBaseServiceTest {

    private final VectorStore vectorStore = mock(VectorStore.class);
    private final KnowledgeBaseService service = new KnowledgeBaseService(vectorStore);

    @Test
    void addDelegatesToVectorStore() {
        service.add("Password reset docs");

        verify(vectorStore).add(argThat(docs ->
                docs.size() == 1
                        && docs.get(0).getText().equals("Password reset docs")));
    }

    @Test
    void searchRequestsTopThreeDocuments() {
        ArgumentCaptor<SearchRequest> captor = ArgumentCaptor.forClass(SearchRequest.class);

        service.search("reset");

        verify(vectorStore).similaritySearch(captor.capture());
        assertThat(captor.getValue().getQuery()).isEqualTo("reset");
        assertThat(captor.getValue().getTopK()).isEqualTo(3);
    }

    @Test
    void searchReturnsDocumentsFromVectorStore() {
        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(List.of(
                        new Document("Doc A"),
                        new Document("Doc B")));

        List<Document> results = service.search("reset");

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getText()).isEqualTo("Doc A");
    }
}