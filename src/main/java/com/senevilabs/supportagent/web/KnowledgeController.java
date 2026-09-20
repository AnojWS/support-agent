package com.senevilabs.supportagent.web;

import com.senevilabs.supportagent.knowledge.KnowledgeBaseService;
import jakarta.validation.Valid;
import org.springframework.ai.document.Document;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    private final KnowledgeBaseService kb;

    public KnowledgeController(KnowledgeBaseService kb) {
        this.kb = kb;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@Valid @RequestBody AddKnowledgeRequest request) {
        kb.add(request.content());
    }

    @GetMapping("/search")
    public List<KnowledgeResponse> search(@RequestParam String q) {
        return kb.search(q).stream()
                .map(doc -> new KnowledgeResponse(doc.getText(), doc.getScore()))
                .toList();
    }
}
