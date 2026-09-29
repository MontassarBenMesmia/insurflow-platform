package com.montassar.insurflow.quote;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quotes")
public class QuoteController {
    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @PostMapping
    ResponseEntity<QuoteResponse> create(@Valid @RequestBody CreateQuoteRequest request, Authentication authentication) {
        QuoteResponse created = quoteService.create(request, authentication.getName());
        return ResponseEntity.created(URI.create("/api/quotes/" + created.id())).body(created);
    }

    @GetMapping
    List<QuoteResponse> findAll() {
        return quoteService.findAll();
    }

    @GetMapping("/{id}")
    QuoteResponse findById(@PathVariable UUID id) {
        return quoteService.findById(id);
    }
}
