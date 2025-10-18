package com.example.pib2.controllers;

import com.example.pib2.models.dtos.ItemDTO;
import com.example.pib2.models.entities.Item;
import com.example.pib2.servicios.ItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión de items.
 * 
 * Proporciona endpoints para operaciones CRUD sobre items del inventario.
 * Requiere autenticación y permite acceso a usuarios con rol ADMIN o USER.
 */
@RestController
@RequestMapping("/api/items")
@Tag(name = "Items", description = "API para gestión de items del inventario")
@SecurityRequirement(name = "basicAuth")

public class ItemController {
    @Autowired
    private ItemService itemService;

    private ItemDTO toDTO(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setQuantity(item.getQuantity());
        return dto;
    }

    private Item toEntity(ItemDTO dto) {
        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setQuantity(dto.getQuantity());
        return item;
    }



    @GetMapping
    @Operation(
        summary = "Obtener todos los items",
        description = "Retorna una lista de todos los items del inventario. Accesible para usuarios ADMIN y USER."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de items obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ItemDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "No autenticado - Credenciales requeridas"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Acceso denegado - Requiere rol ADMIN o USER"
        )
    })
    public List<ItemDTO> getAll() {
        return itemService.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }



    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getById(@PathVariable Long id) {
        return itemService.findById(id)
                .map(item -> ResponseEntity.ok(toDTO(item)))
                .orElse(ResponseEntity.notFound().build());
    }



    @PostMapping
    public ItemDTO create(@RequestBody ItemDTO itemDTO) {
        Item item = toEntity(itemDTO);
        return toDTO(itemService.save(item));
    }



    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> update(@PathVariable Long id, @RequestBody ItemDTO itemDTO) {
        return itemService.findById(id)
                .map(existing -> {
                    itemDTO.setId(id);
                    Item updated = toEntity(itemDTO);
                    return ResponseEntity.ok(toDTO(itemService.save(updated)));
                })
                .orElse(ResponseEntity.notFound().build());
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (itemService.findById(id).isPresent()) {
            itemService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
