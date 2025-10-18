package com.example.pib2.servicios;

import com.example.pib2.models.entities.Document;
import com.example.pib2.models.dtos.DocumentDto;
import com.example.pib2.repositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentService {

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

    public List<DocumentDto> getAllDocuments() {
        return documentRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public DocumentDto getDocumentById(Long id) {
        return documentRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    public DocumentDto createDocument(DocumentDto dto) {
        Document document = toEntity(dto);
        Document saved = documentRepository.save(document);
        return toDto(saved);
    }

    public DocumentDto updateDocument(Long id, DocumentDto dto) {
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

    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }
}
