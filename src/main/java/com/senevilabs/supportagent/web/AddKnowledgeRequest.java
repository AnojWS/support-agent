package com.senevilabs.supportagent.web;

import jakarta.validation.constraints.NotBlank;

public record AddKnowledgeRequest(@NotBlank String content) {
}
