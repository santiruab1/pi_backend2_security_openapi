package com.example.pib2.controllers;

import com.example.pib2.models.entities.Document;
import com.example.pib2.models.dtos.DocumentDto;
import com.example.pib2.repositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    private DocumentDto toDto(Document document) {
        DocumentDto dto = new DocumentDto();
        dto.setId(document.getId());
        dto.setDocumentTypeId(document.getDocumentTypeId());
        dto.setDocumentDate(document.getDocumentDate());
        dto.setDocumentReception(document.getDocumentReception());
        dto.setDocumentPrefix(document.getDocumentPrefix());
        dto.setDocumentNumber(document.getDocumentNumber());
        dto.setDocumentDueDate(document.getDocumentDueDate());
        dto.setThirdPartyId(document.getThirdPartyId());
        return dto;
    }

    private Document toEntity(DocumentDto dto) {
        Document document = new Document();
        document.setId(dto.getId());
        document.setDocumentTypeId(dto.getDocumentTypeId());
        document.setDocumentDate(dto.getDocumentDate());
        document.setDocumentReception(dto.getDocumentReception());
        document.setDocumentPrefix(dto.getDocumentPrefix());
        document.setDocumentNumber(dto.getDocumentNumber());
        document.setDocumentDueDate(dto.getDocumentDueDate());
        document.setThirdPartyId(dto.getThirdPartyId());
        return document;
    }

    @GetMapping
    public List<DocumentDto> getAllDocuments() {
        return documentRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public DocumentDto getDocumentById(@PathVariable Long id) {
        return documentRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    @PostMapping
    public DocumentDto createDocument(@RequestBody DocumentDto dto) {
        Document document = toEntity(dto);
        Document saved = documentRepository.save(document);
        return toDto(saved);
    }

    @PutMapping("/{id}")
    public DocumentDto updateDocument(@PathVariable Long id, @RequestBody DocumentDto dto) {
        return documentRepository.findById(id).map(document -> {
            document.setDocumentTypeId(dto.getDocumentTypeId());
            document.setDocumentDate(dto.getDocumentDate());
            document.setDocumentReception(dto.getDocumentReception());
            document.setDocumentPrefix(dto.getDocumentPrefix());
            document.setDocumentNumber(dto.getDocumentNumber());
            document.setDocumentDueDate(dto.getDocumentDueDate());
            document.setThirdPartyId(dto.getThirdPartyId());
            Document updated = documentRepository.save(document);
            return toDto(updated);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteDocument(@PathVariable Long id) {
        documentRepository.deleteById(id);
    }
}
