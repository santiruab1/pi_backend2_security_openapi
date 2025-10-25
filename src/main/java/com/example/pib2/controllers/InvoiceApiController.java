package com.example.pib2.controllers;

import com.example.pib2.models.dtos.siigo.SalesInvoiceRequestDto;
import com.example.pib2.models.entities.SalesInvoice;
import com.example.pib2.servicios.SiigoApi.SiigoInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceApiController {

    @Autowired
    private SiigoInvoiceService invoiceService;

    @PostMapping("/siigo")
    public ResponseEntity<SalesInvoice> createSiigoInvoice(@RequestBody SalesInvoiceRequestDto invoiceDto) {
        try {
            SalesInvoice localInvoice = invoiceService.createSalesInvoice(invoiceDto);
            return ResponseEntity.ok(localInvoice);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}