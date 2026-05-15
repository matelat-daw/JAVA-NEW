package com.miapp.controller;

import com.miapp.model.Contacto;
import com.miapp.service.ContactoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tienda")
public class ContactoController {
    
    @Autowired
    private ContactoService contactoService;
    
    @GetMapping("/contacto")
    public String mostrarFormularioContacto(Model model) {
        model.addAttribute("contacto", new Contacto());
        return "contacto";
    }
    
    @PostMapping("/contacto/enviar")
    public String enviarContacto(@ModelAttribute Contacto contacto, Model model) {
        contactoService.guardarContacto(contacto);
        model.addAttribute("mensajeExito", "¡Mensaje enviado con éxito!");
        return "contacto";
    }
}
