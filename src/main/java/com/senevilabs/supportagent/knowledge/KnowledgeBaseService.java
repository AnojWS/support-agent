package com.senevilabs.supportagent.knowledge;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KnowledgeBaseService {

    private final VectorStore vectorStore;

    public KnowledgeBaseService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void add(String content) {
        vectorStore.add(List.of(new Document(content)));
    }

    public List<Document> search(String query) {
        return vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(3).build());
    }
}
