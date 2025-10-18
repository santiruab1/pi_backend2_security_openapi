package com.example.pib2.servicios.SiigoApi;

import com.example.pib2.models.dtos.siigo.SalesInvoiceRequestDto;
import com.example.pib2.models.entities.SalesInvoice;
import com.example.pib2.repositories.SalesInvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SiigoInvoiceService {
    
    @Autowired
    private SiigoAuthService authService;
    
    @Autowired
    private SalesInvoiceRepository invoiceRepository;

    public SalesInvoice createSalesInvoice(SalesInvoiceRequestDto invoiceDto) {
        String token = authService.getAccessToken();
        
        // 1. Lógica para construir y enviar la petición POST a /v1/invoices.
        
        System.out.println("Enviando factura a Siigo con Token: " + token.substring(0, 10) + "...");
        
        // 2. Simulación de la respuesta de Siigo
        String siigoInvoiceId = "GUID-FACTURA-" + System.currentTimeMillis(); 
        String invoiceNumber = "FV-000" + (invoiceRepository.count() + 1);
        
        // 3. Crear y guardar la referencia local (Entidad)
        SalesInvoice localInvoice = new SalesInvoice();
        localInvoice.setSiigoId(siigoInvoiceId);
        localInvoice.setDocumentNumber(invoiceNumber);
        localInvoice.setInvoiceDate(invoiceDto.getDate());
        
        // Calcular el total de la factura a partir de los ítems del DTO
        double total = invoiceDto.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getPrice())
                .sum();
        localInvoice.setTotalAmount(total);

        // 4. Persistir en la base de datos
        return invoiceRepository.save(localInvoice);
    }
}