package com.example.pib2.config;

import com.example.pib2.models.entities.User;
import com.example.pib2.models.entities.Warehouse;
import com.example.pib2.models.entities.CostCenter;
import com.example.pib2.models.entities.ThirdParty;
import com.example.pib2.repositories.UserRepository;
import com.example.pib2.repositories.WarehouseRepository;
import com.example.pib2.repositories.CostCenterRepository;
import com.example.pib2.repositories.ThirdPartyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;
    
    @Autowired
    private CostCenterRepository costCenterRepository;

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // ... (Tu código para crear usuarios, sin cambios) ...
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

        // --- Código combinado para crear almacenes y centros de costo ---

        // Crear almacenes de ejemplo si no existen
        if (warehouseRepository.count() == 0) {
            Warehouse warehouse1 = new Warehouse();
            warehouse1.setName("Almacén Principal");
            warehouse1.setLocation("Bogotá");

            Warehouse warehouse2 = new Warehouse();
            warehouse2.setName("Almacén Secundario");
            warehouse2.setLocation("Medellín");

            warehouseRepository.save(warehouse1);
            warehouseRepository.save(warehouse2);

            System.out.println("Almacenes de prueba creados.");
        }

        // Crear centros de costo de ejemplo si no existen
        // Reutiliza el método para evitar código duplicado
        createCostCenters();

        System.out.println("\n=== CREDENCIALES DE PRUEBA ===");
        System.out.println("ADMIN: identification=12345678, password=admin123");
        System.out.println("USER: identification=87654321, password=user123");
        System.out.println("USER: identification=11223344, password=john123");
        System.out.println("================================\n");
    }

    private void createCostCenters() {
        // ... (Tu método para crear centros de costo, sin cambios) ...
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
        
        // Cargar terceros de ejemplo
        loadThirdParties();
    }
    
    /**
     * Carga terceros de ejemplo en la base de datos.
     * 
     * Estructura de datos:
     * - documentType: Número que identifica el tipo de documento
     *   * 1-99: Cédula de ciudadanía
     *   * 100-999: Cédula de extranjería  
     *   * 900000000+: NIT de empresa
     * - documentNumber: Número de identificación como string
     * - isSupplier/isClient: Booleanos que indican el rol del tercero
     * - businessName: Para empresas (opcional si hay name+surname)
     * - name+surname: Para personas naturales (opcional si hay businessName)
     */
    private void loadThirdParties() {
        System.out.println("Cargando terceros de ejemplo...");
        
        // ===== EMPRESAS =====
        
        // Empresa proveedora (solo businessName)
        if (thirdPartyRepository.findByMail("proveedor@empresa.com").isEmpty()) {
            ThirdParty proveedor = new ThirdParty();
            proveedor.setBusinessName("Tecnología Avanzada S.A.S");
            proveedor.setDocumentType(900123456); // NIT de empresa
            proveedor.setDocumentNumber("900123456-1");
            proveedor.setIsSupplier(true);
            proveedor.setIsClient(false);
            proveedor.setAddress("Calle 100 #15-30");
            proveedor.setPhoneNumber("+57 1 234-5678");
            proveedor.setCity("Bogotá");
            proveedor.setMail("proveedor@empresa.com");
            
            thirdPartyRepository.save(proveedor);
            System.out.println("✓ Empresa PROVEEDORA creada: proveedor@empresa.com");
        }
        
        // Empresa cliente (solo businessName)
        if (thirdPartyRepository.findByMail("cliente@empresa.com").isEmpty()) {
            ThirdParty cliente = new ThirdParty();
            cliente.setBusinessName("Comercializadora del Norte Ltda");
            cliente.setDocumentType(900789012); // NIT de empresa
            cliente.setDocumentNumber("900789012-2");
            cliente.setIsSupplier(false);
            cliente.setIsClient(true);
            cliente.setAddress("Carrera 50 #25-80");
            cliente.setPhoneNumber("+57 4 567-8901");
            cliente.setCity("Medellín");
            cliente.setMail("cliente@empresa.com");
            
            thirdPartyRepository.save(cliente);
            System.out.println("✓ Empresa CLIENTE creada: cliente@empresa.com");
        }
        
        // Empresa que es tanto proveedor como cliente
        if (thirdPartyRepository.findByMail("empresa.mixta@ejemplo.com").isEmpty()) {
            ThirdParty empresaMixta = new ThirdParty();
            empresaMixta.setBusinessName("Servicios Integrales S.A.S");
            empresaMixta.setDocumentType(900555666); // NIT de empresa
            empresaMixta.setDocumentNumber("900555666-3");
            empresaMixta.setIsSupplier(true);
            empresaMixta.setIsClient(true);
            empresaMixta.setAddress("Avenida 68 #25-40");
            empresaMixta.setPhoneNumber("+57 1 555-6666");
            empresaMixta.setCity("Bogotá");
            empresaMixta.setMail("empresa.mixta@ejemplo.com");
            
            thirdPartyRepository.save(empresaMixta);
            System.out.println("✓ Empresa MIXTA (Proveedor y Cliente) creada: empresa.mixta@ejemplo.com");
        }
        
        // ===== PERSONAS NATURALES =====
        
        // Persona natural que es tanto proveedor como cliente
        if (thirdPartyRepository.findByMail("contratista@empresa.com").isEmpty()) {
            ThirdParty contratista = new ThirdParty();
            contratista.setName("Roberto");
            contratista.setSurname("Silva");
            contratista.setDocumentType(12345678); // Cédula de ciudadanía
            contratista.setDocumentNumber("12345678");
            contratista.setIsSupplier(true);
            contratista.setIsClient(true);
            contratista.setAddress("Avenida 30 #45-12");
            contratista.setPhoneNumber("+57 2 345-6789");
            contratista.setCity("Cali");
            contratista.setMail("contratista@empresa.com");
            
            thirdPartyRepository.save(contratista);
            System.out.println("✓ Persona MIXTA (Proveedor y Cliente) creada: contratista@empresa.com");
        }
        
        // Cliente persona natural
        if (thirdPartyRepository.findByMail("persona@ejemplo.com").isEmpty()) {
            ThirdParty persona = new ThirdParty();
            persona.setName("Ana");
            persona.setSurname("Martínez");
            persona.setDocumentType(87654321); // Cédula de ciudadanía
            persona.setDocumentNumber("87654321");
            persona.setIsSupplier(false);
            persona.setIsClient(true);
            persona.setAddress("Calle 80 #12-45");
            persona.setPhoneNumber("+57 5 123-4567");
            persona.setCity("Barranquilla");
            persona.setMail("persona@ejemplo.com");
            
            thirdPartyRepository.save(persona);
            System.out.println("✓ Persona CLIENTE creada: persona@ejemplo.com");
        }
        
        // Proveedor persona natural
        if (thirdPartyRepository.findByMail("proveedor.persona@ejemplo.com").isEmpty()) {
            ThirdParty proveedorPersona = new ThirdParty();
            proveedorPersona.setName("Carlos");
            proveedorPersona.setSurname("Mendoza");
            proveedorPersona.setDocumentType(11223344); // Cédula de ciudadanía
            proveedorPersona.setDocumentNumber("11223344");
            proveedorPersona.setIsSupplier(true);
            proveedorPersona.setIsClient(false);
            proveedorPersona.setAddress("Carrera 15 #25-30");
            proveedorPersona.setPhoneNumber("+57 3 456-7890");
            proveedorPersona.setCity("Bucaramanga");
            proveedorPersona.setMail("proveedor.persona@ejemplo.com");
            
            thirdPartyRepository.save(proveedorPersona);
            System.out.println("✓ Persona PROVEEDORA creada: proveedor.persona@ejemplo.com");
        }
        
        // Cliente extranjero
        if (thirdPartyRepository.findByMail("extranjero@ejemplo.com").isEmpty()) {
            ThirdParty extranjero = new ThirdParty();
            extranjero.setName("John");
            extranjero.setSurname("Smith");
            extranjero.setDocumentType(123); // Cédula de extranjería
            extranjero.setDocumentNumber("CE123456789");
            extranjero.setIsSupplier(false);
            extranjero.setIsClient(true);
            extranjero.setAddress("Carrera 7 #32-16");
            extranjero.setPhoneNumber("+57 1 987-6543");
            extranjero.setCity("Bogotá");
            extranjero.setMail("extranjero@ejemplo.com");
            
            thirdPartyRepository.save(extranjero);
            System.out.println("✓ Extranjero CLIENTE creado: extranjero@ejemplo.com");
        }
        
        // ===== RESUMEN =====
        long totalTerceros = thirdPartyRepository.count();
        long proveedores = thirdPartyRepository.findByIsSupplier(true).size();
        long clientes = thirdPartyRepository.findByIsClient(true).size();
        long empresas = thirdPartyRepository.findAll().stream()
                .filter(tp -> tp.getBusinessName() != null && !tp.getBusinessName().isEmpty())
                .count();
        long personas = totalTerceros - empresas;
        
        System.out.println("");
        System.out.println("=== RESUMEN DE TERCEROS CARGADOS ===");
        System.out.println("Total de terceros: " + totalTerceros);
        System.out.println("Proveedores: " + proveedores);
        System.out.println("Clientes: " + clientes);
        System.out.println("Empresas: " + empresas);
        System.out.println("Personas naturales: " + personas);
        System.out.println("Terceros de ejemplo cargados exitosamente.");
    }
}