package com.example.pib2.controllers;

import com.example.pib2.models.dtos.FiscalDocumentDto;
import com.example.pib2.models.entities.FiscalDocument;
import com.example.pib2.servicios.FiscalDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fiscal-documents")
public class FiscalDocumentController {

    @Autowired
    private FiscalDocumentService fiscalDocumentService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadExcelFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("El archivo está vacío");
            }

            // Validar tamaño máximo del archivo (512MB = 512 * 1024 * 1024 bytes)
            long maxFileSize = 512L * 1024 * 1024; // 512MB
            if (file.getSize() > maxFileSize) {
                return ResponseEntity.badRequest()
                        .body("El archivo excede el tamaño máximo permitido de 512MB. Tamaño actual: " +
                                String.format("%.2f MB", file.getSize() / (1024.0 * 1024.0)));
            }

            String filename = file.getOriginalFilename();
            if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
                return ResponseEntity.badRequest().body("El archivo debe ser un Excel (.xlsx o .xls)");
            }

            List<FiscalDocument> documents = fiscalDocumentService.processExcelFile(file);
            List<FiscalDocumentDto> dtos = documents.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar el archivo: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<FiscalDocumentDto>> getAllDocuments() {
        List<FiscalDocument> documents = fiscalDocumentService.getAllDocuments();
        List<FiscalDocumentDto> dtos = documents.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FiscalDocumentDto> getDocumentById(@PathVariable Long id) {
        FiscalDocument document = fiscalDocumentService.getDocumentById(id);
        if (document == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(document));
    }

    private FiscalDocumentDto toDto(FiscalDocument document) {
        FiscalDocumentDto dto = new FiscalDocumentDto();
        dto.setId(document.getId());
        dto.setDocumentType(document.getDocumentType());
        dto.setCufeCude(document.getCufeCude());
        dto.setFolio(document.getFolio());
        dto.setPrefix(document.getPrefix());
        dto.setCurrency(document.getCurrency());
        dto.setPaymentForm(document.getPaymentForm());
        dto.setPaymentMethod(document.getPaymentMethod());
        dto.setIssueDate(document.getIssueDate());
        dto.setReceptionDate(document.getReceptionDate());
        dto.setIssuerNit(document.getIssuerNit());
        dto.setIssuerName(document.getIssuerName());
        dto.setReceiverNit(document.getReceiverNit());
        dto.setReceiverName(document.getReceiverName());
        dto.setIva(document.getIva());
        dto.setIca(document.getIca());
        dto.setIc(document.getIc());
        dto.setInc(document.getInc());
        dto.setTimbre(document.getTimbre());
        dto.setIncBags(document.getIncBags());
        dto.setInCarbon(document.getInCarbon());
        dto.setInFuels(document.getInFuels());
        dto.setIcData(document.getIcData());
        dto.setIcl(document.getIcl());
        dto.setInpp(document.getInpp());
        dto.setIbua(document.getIbua());
        dto.setIcui(document.getIcui());
        dto.setReteIva(document.getReteIva());
        dto.setReteRent(document.getReteRent());
        dto.setReteIca(document.getReteIca());
        dto.setTotal(document.getTotal());
        dto.setStatus(document.getStatus());
        dto.setGroupInfo(document.getGroupInfo());
        return dto;
    }
}
