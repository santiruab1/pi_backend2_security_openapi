package com.example.pib2.servicios;

import com.example.pib2.models.entities.FiscalDocument;
import com.example.pib2.repositories.FiscalDocumentRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class FiscalDocumentService {

    @Autowired
    private FiscalDocumentRepository fiscalDocumentRepository;

    // Formato específico para Fecha Emisión: dd-MM-yyyy (ej: 30-10-2025)
    private static final DateTimeFormatter ISSUE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    
    // Formato específico para Fecha Recepción: dd-MM-yyyy HH:mm:ss (ej: 30-10-2025 14:30:45)
    private static final DateTimeFormatter RECEPTION_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    
    private static final DateTimeFormatter[] DATE_FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd")
    };

    // Nombres esperados de las columnas en el orden correcto
    private static final List<String> EXPECTED_COLUMNS = Arrays.asList(
            "Tipo de Documento",
            "CUFE/CUDE",
            "Folio",
            "Prefijo",
            "Divisa",
            "Forma de Pago",
            "Medio de Pago",
            "Fecha Emisión",
            "Fecha Recepción",
            "NIT Emisor",
            "Nombre Emisor",
            "NIT Receptor",
            "Nombre Receptor",
            "IVA",
            "ICA",
            "IC",
            "INC",
            "Timbre",
            "INC Bolsas",
            "IN Carbono",
            "IN Combustibles",
            "IC Datos",
            "ICL",
            "INPP",
            "IBUA",
            "ICUI",
            "Rete IVA",
            "Rete Renta",
            "Rete ICA",
            "Total",
            "Estado",
            "Grupo"
    );

    @Transactional
    public List<FiscalDocument> processExcelFile(MultipartFile file) throws Exception {
        List<FiscalDocument> documents = new ArrayList<>();

        String filename = file.getOriginalFilename();
        Workbook workbook;

        // Determinar el tipo de archivo Excel
        if (filename != null && filename.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(file.getInputStream());
        } else if (filename != null && filename.endsWith(".xls")) {
            workbook = new HSSFWorkbook(file.getInputStream());
        } else {
            throw new IllegalArgumentException("El archivo debe ser un Excel (.xlsx o .xls)");
        }

        try (workbook) {
            Sheet sheet = workbook.getSheetAt(0);

            // Validar los nombres de las columnas en el encabezado
            validateHeader(sheet);

            // Saltar la primera fila (encabezados)
            int startRow = 1;

            for (int rowIndex = startRow; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }

                FiscalDocument document = mapRowToDocument(row);
                if (document != null) {
                    documents.add(document);
                }
            }

            // Guardar todos los documentos en la base de datos
            return fiscalDocumentRepository.saveAll(documents);
        }
    }

    /**
     * Valida que los nombres de las columnas en el encabezado coincidan con los esperados
     * @param sheet La hoja de Excel a validar
     * @throws IllegalArgumentException Si las columnas no coinciden
     */
    private void validateHeader(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new IllegalArgumentException("El archivo Excel no contiene encabezados en la primera fila");
        }

        List<String> actualColumns = new ArrayList<>();
        for (int i = 0; i < EXPECTED_COLUMNS.size(); i++) {
            Cell cell = headerRow.getCell(i);
            String columnName = getCellValueAsString(cell);
            if (columnName == null || columnName.trim().isEmpty()) {
                columnName = "";
            } else {
                columnName = columnName.trim();
            }
            actualColumns.add(columnName);
        }

        // Comparar columnas esperadas con las actuales
        List<String> missingColumns = new ArrayList<>();
        List<String> incorrectColumns = new ArrayList<>();

        for (int i = 0; i < EXPECTED_COLUMNS.size(); i++) {
            String expected = EXPECTED_COLUMNS.get(i);
            String actual = actualColumns.size() > i ? actualColumns.get(i) : "";

            if (actual.isEmpty()) {
                missingColumns.add(String.format("Columna %d: '%s'", i + 1, expected));
            } else if (!expected.equalsIgnoreCase(actual)) {
                incorrectColumns.add(String.format("Columna %d: Se esperaba '%s' pero se encontró '%s'", 
                    i + 1, expected, actual));
            }
        }

        // Si hay errores, construir mensaje de error descriptivo
        if (!missingColumns.isEmpty() || !incorrectColumns.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder(
                "El archivo Excel no tiene la estructura correcta. Errores encontrados:\n\n");
            
            if (!incorrectColumns.isEmpty()) {
                errorMessage.append("Columnas con nombres incorrectos:\n");
                for (String error : incorrectColumns) {
                    errorMessage.append("  - ").append(error).append("\n");
                }
                errorMessage.append("\n");
            }

            if (!missingColumns.isEmpty()) {
                errorMessage.append("Columnas faltantes o vacías:\n");
                for (String error : missingColumns) {
                    errorMessage.append("  - ").append(error).append("\n");
                }
                errorMessage.append("\n");
            }

            errorMessage.append("Columnas esperadas en orden:\n");
            for (int i = 0; i < EXPECTED_COLUMNS.size(); i++) {
                errorMessage.append(String.format("  %d. %s\n", i + 1, EXPECTED_COLUMNS.get(i)));
            }

            throw new IllegalArgumentException(errorMessage.toString());
        }
    }

    private FiscalDocument mapRowToDocument(Row row) {
        try {
            FiscalDocument document = new FiscalDocument();

            document.setDocumentType(getCellValueAsString(row.getCell(0)));
            document.setCufeCude(getCellValueAsString(row.getCell(1)));
            document.setFolio(getCellValueAsString(row.getCell(2)));
            document.setPrefix(getCellValueAsString(row.getCell(3)));
            document.setCurrency(getCellValueAsString(row.getCell(4)));
            document.setPaymentForm(getCellValueAsString(row.getCell(5)));
            document.setPaymentMethod(getCellValueAsString(row.getCell(6)));
            document.setIssueDate(getCellValueAsIssueDate(row.getCell(7)));
            document.setReceptionDate(getCellValueAsReceptionDate(row.getCell(8)));
            document.setIssuerNit(getCellValueAsString(row.getCell(9)));
            document.setIssuerName(getCellValueAsString(row.getCell(10)));
            document.setReceiverNit(getCellValueAsString(row.getCell(11)));
            document.setReceiverName(getCellValueAsString(row.getCell(12)));
            document.setIva(getCellValueAsBigDecimal(row.getCell(13)));
            document.setIca(getCellValueAsBigDecimal(row.getCell(14)));
            document.setIc(getCellValueAsBigDecimal(row.getCell(15)));
            document.setInc(getCellValueAsBigDecimal(row.getCell(16)));
            document.setTimbre(getCellValueAsBigDecimal(row.getCell(17)));
            document.setIncBags(getCellValueAsBigDecimal(row.getCell(18)));
            document.setInCarbon(getCellValueAsBigDecimal(row.getCell(19)));
            document.setInFuels(getCellValueAsBigDecimal(row.getCell(20)));
            document.setIcData(getCellValueAsBigDecimal(row.getCell(21)));
            document.setIcl(getCellValueAsBigDecimal(row.getCell(22)));
            document.setInpp(getCellValueAsBigDecimal(row.getCell(23)));
            document.setIbua(getCellValueAsBigDecimal(row.getCell(24)));
            document.setIcui(getCellValueAsBigDecimal(row.getCell(25)));
            document.setReteIva(getCellValueAsBigDecimal(row.getCell(26)));
            document.setReteRent(getCellValueAsBigDecimal(row.getCell(27)));
            document.setReteIca(getCellValueAsBigDecimal(row.getCell(28)));
            document.setTotal(getCellValueAsBigDecimal(row.getCell(29)));
            document.setStatus(getCellValueAsString(row.getCell(30)));
            document.setGroupInfo(getCellValueAsString(row.getCell(31)));

            return document;
        } catch (Exception e) {
            // Log error y retornar null para esta fila
            System.err.println("Error procesando fila " + row.getRowNum() + ": " + e.getMessage());
            return null;
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Convertir número a string sin decimales si es entero
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    /**
     * Parsea la Fecha Emisión en formato dd-MM-yyyy (ej: 30-10-2025)
     */
    private LocalDate getCellValueAsIssueDate(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {
            switch (cell.getCellType()) {
                case STRING:
                    String dateString = cell.getStringCellValue().trim();
                    if (dateString.isEmpty()) {
                        return null;
                    }
                    // Intentar parsear con el formato específico dd-MM-yyyy
                    try {
                        return LocalDate.parse(dateString, ISSUE_DATE_FORMATTER);
                    } catch (DateTimeParseException e) {
                        // Si falla, intentar con otros formatos como fallback
                        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
                            try {
                                return LocalDate.parse(dateString, formatter);
                            } catch (DateTimeParseException ex) {
                                // Continuar con el siguiente formato
                            }
                        }
                        System.err.println("No se pudo parsear la Fecha Emisión: " + dateString);
                        return null;
                    }
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate();
                    } else {
                        // Intentar parsear como número de días desde 1900 (formato Excel)
                        double numericValue = cell.getNumericCellValue();
                        return org.apache.poi.ss.usermodel.DateUtil.getJavaDate(numericValue).toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate();
                    }
                default:
                    return null;
            }
        } catch (Exception e) {
            System.err.println("Error parseando Fecha Emisión: " + e.getMessage());
            return null;
        }
    }

    /**
     * Parsea la Fecha Recepción en formato dd-MM-yyyy HH:mm:ss (ej: 30-10-2025 14:30:45)
     * Extrae solo la parte de la fecha para almacenarla como LocalDate
     */
    private LocalDate getCellValueAsReceptionDate(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {
            switch (cell.getCellType()) {
                case STRING:
                    String dateString = cell.getStringCellValue().trim();
                    if (dateString.isEmpty()) {
                        return null;
                    }
                    // Intentar parsear con el formato específico dd-MM-yyyy HH:mm:ss
                    try {
                        LocalDateTime dateTime = LocalDateTime.parse(dateString, RECEPTION_DATE_FORMATTER);
                        return dateTime.toLocalDate();
                    } catch (DateTimeParseException e) {
                        // Si falla, intentar solo con la fecha dd-MM-yyyy
                        try {
                            return LocalDate.parse(dateString, ISSUE_DATE_FORMATTER);
                        } catch (DateTimeParseException e2) {
                            // Si falla, intentar con otros formatos como fallback
                            for (DateTimeFormatter formatter : DATE_FORMATTERS) {
                                try {
                                    return LocalDate.parse(dateString, formatter);
                                } catch (DateTimeParseException ex) {
                                    // Continuar con el siguiente formato
                                }
                            }
                            System.err.println("No se pudo parsear la Fecha Recepción: " + dateString);
                            return null;
                        }
                    }
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate();
                    } else {
                        // Intentar parsear como número de días desde 1900 (formato Excel)
                        double numericValue = cell.getNumericCellValue();
                        return org.apache.poi.ss.usermodel.DateUtil.getJavaDate(numericValue).toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate();
                    }
                default:
                    return null;
            }
        } catch (Exception e) {
            System.err.println("Error parseando Fecha Recepción: " + e.getMessage());
            return null;
        }
    }

    private LocalDate getCellValueAsDate(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {
            switch (cell.getCellType()) {
                case STRING:
                    String dateString = cell.getStringCellValue().trim();
                    if (dateString.isEmpty()) {
                        return null;
                    }
                    // Intentar parsear con diferentes formatos
                    for (DateTimeFormatter formatter : DATE_FORMATTERS) {
                        try {
                            return LocalDate.parse(dateString, formatter);
                        } catch (DateTimeParseException e) {
                            // Continuar con el siguiente formato
                        }
                    }
                    // Si ninguno funcionó, retornar null
                    System.err.println("No se pudo parsear la fecha: " + dateString);
                    return null;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate();
                    } else {
                        // Intentar parsear como número de días desde 1900 (formato Excel)
                        double numericValue = cell.getNumericCellValue();
                        return org.apache.poi.ss.usermodel.DateUtil.getJavaDate(numericValue).toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate();
                    }
                default:
                    return null;
            }
        } catch (Exception e) {
            System.err.println("Error parseando fecha: " + e.getMessage());
            return null;
        }
    }

    private BigDecimal getCellValueAsBigDecimal(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return BigDecimal.valueOf(cell.getNumericCellValue());
                case STRING:
                    String stringValue = cell.getStringCellValue().trim();
                    if (stringValue.isEmpty()) {
                        return null;
                    }
                    // Remover espacios
                    stringValue = stringValue.replaceAll(" ", "");
                    // Manejar diferentes formatos de número
                    // Si tiene punto y coma: formato colombiano (1.234.567,89)
                    if (stringValue.contains(".") && stringValue.contains(",")) {
                        stringValue = stringValue.replace(".", "").replace(",", ".");
                    }
                    // Si solo tiene coma como separador decimal
                    else if (stringValue.contains(",") && !stringValue.matches(".*\\..*")) {
                        stringValue = stringValue.replace(",", ".");
                    }
                    // Si solo tiene punto, asumir que es decimal si hay más de un punto o si es
                    // formato americano
                    else if (stringValue.matches(".*\\d\\.\\d.*")) {
                        // Es un decimal con punto
                        // Si hay múltiples puntos, remover todos excepto el último
                        if (stringValue.chars().filter(ch -> ch == '.').count() > 1) {
                            int lastDotIndex = stringValue.lastIndexOf('.');
                            stringValue = stringValue.substring(0, lastDotIndex).replace(".", "") +
                                    stringValue.substring(lastDotIndex);
                        }
                    }
                    return new BigDecimal(stringValue);
                case FORMULA:
                    // Evaluar fórmula
                    FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper()
                            .createFormulaEvaluator();
                    CellValue cellValue = evaluator.evaluate(cell);
                    if (cellValue.getCellType() == CellType.NUMERIC) {
                        return BigDecimal.valueOf(cellValue.getNumberValue());
                    }
                    return null;
                default:
                    return null;
            }
        } catch (Exception e) {
            System.err.println("Error parseando BigDecimal: " + e.getMessage());
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<FiscalDocument> getAllDocuments() {
        return fiscalDocumentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public FiscalDocument getDocumentById(Long id) {
        return fiscalDocumentRepository.findById(id).orElse(null);
    }
}
