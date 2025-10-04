package com.example.pib2.config;

import com.example.pib2.models.entities.User;
import com.example.pib2.models.entities.Warehouse;
import com.example.pib2.models.entities.CostCenter;
import com.example.pib2.repositories.UserRepository;
import com.example.pib2.repositories.WarehouseRepository;
import com.example.pib2.repositories.CostCenterRepository;
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
    }
}