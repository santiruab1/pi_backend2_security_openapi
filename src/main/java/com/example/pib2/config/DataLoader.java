package com.example.pib2.config;

import com.example.pib2.models.entities.User;
import com.example.pib2.models.entities.CostCenter;
import com.example.pib2.repositories.UserRepository;
import com.example.pib2.models.entities.Company;
import com.example.pib2.repositories.CompanyRepository;
import com.example.pib2.repositories.CostCenterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Componente para cargar datos iniciales en la aplicación.
 * 
 * Esta clase se ejecuta al iniciar la aplicación y crea usuarios
 * de prueba con diferentes roles y centros de costo si no existen en la base de datos.
 */
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CostCenterRepository costCenterRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

        @Autowired
        private com.example.pib2.repositories.DocumentRepository documentRepository;

        @Autowired
        private CompanyRepository companyRepository;

    /**
     * Método que se ejecuta al iniciar la aplicación.
     * 
     * Crea usuarios de prueba con roles ADMIN y USER, y centros de costo
     * de ejemplo si no existen en la base de datos.
     */
    @Override
    public void run(String... args) throws Exception {
        // Crear usuario ADMIN si no existe
        if (userRepository.findByIdentification("12345678").isEmpty()) {
            User admin = new User();
            admin.setIdentification("12345678");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setFirstName("John");
            admin.setLastName("Doe");
            admin.setEnabled(true);
            admin.setAccountNonExpired(true);
            admin.setAccountNonLocked(true);
            admin.setCredentialsNonExpired(true);
            
            userRepository.save(admin);
            System.out.println("Usuario ADMIN creado: identification=12345678, password=admin123");
        }

        // Crear usuario USER si no existe
        if (userRepository.findByIdentification("87654321").isEmpty()) {
            User user = new User();
            user.setIdentification("87654321");
            user.setEmail("user@example.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole("USER");
            user.setFirstName("Jane");
            user.setLastName("Smith");
            user.setEnabled(true);
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);
            
            userRepository.save(user);
            System.out.println("Usuario USER creado: identification=87654321, password=user123");
        }

        // Crear otro usuario USER de ejemplo
        if (userRepository.findByIdentification("11223344").isEmpty()) {
            User john = new User();
            john.setIdentification("11223344");
            john.setEmail("john@example.com");
            john.setPassword(passwordEncoder.encode("john123"));
            john.setRole("USER");
            john.setFirstName("John");
            john.setLastName("Johnson");
            john.setEnabled(true);
            john.setAccountNonExpired(true);
            john.setAccountNonLocked(true);
            john.setCredentialsNonExpired(true);
            
            userRepository.save(john);
            System.out.println("Usuario USER creado: identification=11223344, password=john123");
        }

            // Crear datos semillas para Company
            if (!companyRepository.existsByIdentificationNumber("COMP001")) {
                Company company1 = new Company();
                company1.setName("Tech Solutions S.A.");
                company1.setIdentificationNumber("COMP001");
                company1.setAddress("Calle 123, Ciudad");
                company1.setPhone("555-1234");
                company1.setEmail("contacto@techsolutions.com");
                company1.setCreatedAt(java.time.LocalDateTime.now());
                company1.setUpdatedAt(java.time.LocalDateTime.now());
                companyRepository.save(company1);
                System.out.println("Company creada: COMP001");
            }

            if (!companyRepository.existsByIdentificationNumber("COMP002")) {
                Company company2 = new Company();
                company2.setName("Innovatech Ltda.");
                company2.setIdentificationNumber("COMP002");
                company2.setAddress("Avenida 456, Ciudad");
                company2.setPhone("555-5678");
                company2.setEmail("info@innovatech.com");
                company2.setCreatedAt(java.time.LocalDateTime.now());
                company2.setUpdatedAt(java.time.LocalDateTime.now());
                companyRepository.save(company2);
                System.out.println("Company creada: COMP002");
            }

                // Datos semillas para Document
                if (documentRepository.findAll().isEmpty()) {
                    com.example.pib2.models.entities.Document doc1 = new com.example.pib2.models.entities.Document();
                    doc1.setDocumentTypeId(1);
                    doc1.setDocumentDate(java.time.LocalDate.now().minusDays(10));
                    doc1.setDocumentReception(java.time.LocalDate.now().minusDays(9));
                    doc1.setDocumentPrefix("INV");
                    doc1.setDocumentNumber("1001");
                    doc1.setDocumentDueDate(java.time.LocalDate.now().plusDays(20));
                    doc1.setThirdPartyId(1L);
                    documentRepository.save(doc1);
                    System.out.println("Documento de prueba creado: INV-1001");

                    com.example.pib2.models.entities.Document doc2 = new com.example.pib2.models.entities.Document();
                    doc2.setDocumentTypeId(2);
                    doc2.setDocumentDate(java.time.LocalDate.now().minusDays(5));
                    doc2.setDocumentReception(java.time.LocalDate.now().minusDays(4));
                    doc2.setDocumentPrefix("FAC");
                    doc2.setDocumentNumber("2002");
                    doc2.setDocumentDueDate(java.time.LocalDate.now().plusDays(15));
                    doc2.setThirdPartyId(2L);
                    documentRepository.save(doc2);
                    System.out.println("Documento de prueba creado: FAC-2002");
                }
        }

    /**
     * Crea centros de costo de ejemplo si no existen en la base de datos.
     */
    private void createCostCenters() {
        // Centro de costo 1: Administración
        if (costCenterRepository.findByCode("ADM001").isEmpty()) {
            CostCenter adminCenter = new CostCenter();
            adminCenter.setCode("ADM001");
            adminCenter.setName("Administración General");
            adminCenter.setActive(true);
            adminCenter.setCreatedBy("SYSTEM");
            adminCenter.setUpdatedBy("SYSTEM");
            
            costCenterRepository.save(adminCenter);
            System.out.println("Centro de costo creado: ADM001 - Administración General");
        }

        // Centro de costo 2: Ventas
        if (costCenterRepository.findByCode("VTA001").isEmpty()) {
            CostCenter salesCenter = new CostCenter();
            salesCenter.setCode("VTA001");
            salesCenter.setName("Departamento de Ventas");
            salesCenter.setActive(true);
            salesCenter.setCreatedBy("SYSTEM");
            salesCenter.setUpdatedBy("SYSTEM");
            
            costCenterRepository.save(salesCenter);
            System.out.println("Centro de costo creado: VTA001 - Departamento de Ventas");
        }

        // Centro de costo 3: Marketing
        if (costCenterRepository.findByCode("MKT001").isEmpty()) {
            CostCenter marketingCenter = new CostCenter();
            marketingCenter.setCode("MKT001");
            marketingCenter.setName("Marketing y Publicidad");
            marketingCenter.setActive(true);
            marketingCenter.setCreatedBy("SYSTEM");
            marketingCenter.setUpdatedBy("SYSTEM");
            
            costCenterRepository.save(marketingCenter);
            System.out.println("Centro de costo creado: MKT001 - Marketing y Publicidad");
        }

        // Centro de costo 4: Tecnología
        if (costCenterRepository.findByCode("TEC001").isEmpty()) {
            CostCenter techCenter = new CostCenter();
            techCenter.setCode("TEC001");
            techCenter.setName("Tecnología e Innovación");
            techCenter.setActive(true);
            techCenter.setCreatedBy("SYSTEM");
            techCenter.setUpdatedBy("SYSTEM");
            
            costCenterRepository.save(techCenter);
            System.out.println("Centro de costo creado: TEC001 - Tecnología e Innovación");
        }

        // Centro de costo 5: Recursos Humanos
        if (costCenterRepository.findByCode("RRHH001").isEmpty()) {
            CostCenter hrCenter = new CostCenter();
            hrCenter.setCode("RRHH001");
            hrCenter.setName("Recursos Humanos");
            hrCenter.setActive(true);
            hrCenter.setCreatedBy("SYSTEM");
            hrCenter.setUpdatedBy("SYSTEM");
            
            costCenterRepository.save(hrCenter);
            System.out.println("Centro de costo creado: RRHH001 - Recursos Humanos");
        }

        // Centro de costo 6: Finanzas
        if (costCenterRepository.findByCode("FIN001").isEmpty()) {
            CostCenter financeCenter = new CostCenter();
            financeCenter.setCode("FIN001");
            financeCenter.setName("Finanzas y Contabilidad");
            financeCenter.setActive(true);
            financeCenter.setCreatedBy("SYSTEM");
            financeCenter.setUpdatedBy("SYSTEM");
            
            costCenterRepository.save(financeCenter);
            System.out.println("Centro de costo creado: FIN001 - Finanzas y Contabilidad");
        }

        // Centro de costo 7: Operaciones (Inactivo para ejemplo)
        if (costCenterRepository.findByCode("OPR001").isEmpty()) {
            CostCenter operationsCenter = new CostCenter();
            operationsCenter.setCode("OPR001");
            operationsCenter.setName("Operaciones Generales");
            operationsCenter.setActive(false);
            operationsCenter.setCreatedBy("SYSTEM");
            operationsCenter.setUpdatedBy("SYSTEM");
            
            costCenterRepository.save(operationsCenter);
            System.out.println("Centro de costo creado: OPR001 - Operaciones Generales (INACTIVO)");
        }

        // Centro de costo 8: Atención al Cliente
        if (costCenterRepository.findByCode("ATC001").isEmpty()) {
            CostCenter customerCenter = new CostCenter();
            customerCenter.setCode("ATC001");
            customerCenter.setName("Atención al Cliente");
            customerCenter.setActive(true);
            customerCenter.setCreatedBy("SYSTEM");
            customerCenter.setUpdatedBy("SYSTEM");
            
            costCenterRepository.save(customerCenter);
            System.out.println("Centro de costo creado: ATC001 - Atención al Cliente");
        }

        System.out.println("Centros de costo de ejemplo cargados exitosamente.");
    }
}