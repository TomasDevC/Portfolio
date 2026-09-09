package com.starterkit.springboot.equipamento;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/equipamentos")
public class EquipamentoPageController {

    private final EquipamentoService equipamentoService;

    @Value("${security.api-key.admin:}")
    private String adminKey;

    public EquipamentoPageController(EquipamentoService equipamentoService) {
        this.equipamentoService = equipamentoService;
    }

    @GetMapping
    public String lista(Model model) {
        model.addAttribute("titulo", "Gestao de Equipamentos");
        model.addAttribute("equipamentos", equipamentoService.listAll());
        model.addAttribute("pageScript", "/js/equipamentos.js");
        return "equipamentos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("titulo", "Novo Equipamento");
        model.addAttribute("equipamento", null);
        model.addAttribute("equipamentoForm", new EquipamentoForm());
        model.addAttribute("modo", "novo");
        model.addAttribute("pageScript", "/js/equipamentos.js");
        return "equipamentos/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Equipamento equipamento = equipamentoService.getById(id);
        EquipamentoForm form = new EquipamentoForm();
        copyEquipamentoToForm(equipamento, form);
        model.addAttribute("titulo", "Editar Equipamento");
        model.addAttribute("equipamento", equipamento);
        model.addAttribute("equipamentoForm", form);
        model.addAttribute("modo", "editar");
        model.addAttribute("pageScript", "/js/equipamentos.js");
        return "equipamentos/form";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        Equipamento equipamento = equipamentoService.getById(id);
        model.addAttribute("titulo", "Ficha do Equipamento");
        model.addAttribute("equipamento", equipamento);
        model.addAttribute("pageScript", "/js/equipamentos.js");
        return "equipamentos/detalhe";
    }

    @GetMapping("/codigo/{codigo}")
    public String detalhePorCodigo(@PathVariable String codigo, Model model) {
        Equipamento equipamento = equipamentoService.getByCodigo(codigo);
        model.addAttribute("titulo", "Ficha do Equipamento");
        model.addAttribute("equipamento", equipamento);
        model.addAttribute("pageScript", "/js/equipamentos.js");
        return "equipamentos/detalhe";
    }

    @GetMapping("/scan")
    public String scan(Model model) {
        model.addAttribute("titulo", "Ler QR Code");
        model.addAttribute("pageScript", "/js/equipamentos.js");
        return "equipamentos/scan";
    }

    @PostMapping
    public String criar(@Valid EquipamentoForm equipamentoForm, BindingResult bindingResult, Model model) {
        if (!isValidAdminKey(equipamentoForm.getAdminApiKey())) {
            bindingResult.rejectValue("adminApiKey", "adminApiKey.invalid", "Chave de administrador invalida.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("titulo", "Novo Equipamento");
            model.addAttribute("equipamento", null);
            model.addAttribute("modo", "novo");
            model.addAttribute("pageScript", "/js/equipamentos.js");
            return "equipamentos/form";
        }

        equipamentoService.create(equipamentoForm);
        return "redirect:/equipamentos?status=created";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Valid EquipamentoForm equipamentoForm, BindingResult bindingResult,
            Model model) {
        Equipamento equipamento = equipamentoService.getById(id);
        if (!isValidAdminKey(equipamentoForm.getAdminApiKey())) {
            bindingResult.rejectValue("adminApiKey", "adminApiKey.invalid", "Chave de administrador invalida.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("titulo", "Editar Equipamento");
            model.addAttribute("modo", "editar");
            model.addAttribute("equipamento", equipamento);
            model.addAttribute("pageScript", "/js/equipamentos.js");
            return "equipamentos/form";
        }

        equipamentoService.update(id, equipamentoForm);
        return "redirect:/equipamentos?status=updated";
    }

    private boolean isValidAdminKey(String providedKey) {
        return providedKey != null && !providedKey.trim().isEmpty() && providedKey.equals(adminKey);
    }

    private void copyEquipamentoToForm(Equipamento equipamento, EquipamentoForm form) {
        form.setCategoria(equipamento.getCategoria());
        form.setDataCompra(equipamento.getDataCompra());
        form.setModelo(equipamento.getModelo());
        form.setMarca(equipamento.getMarca());
        form.setNumeroSerie(equipamento.getNumeroSerie());
        form.setLocal(equipamento.getLocal());
        form.setGarantia(equipamento.getGarantia());
        form.setSeguro(equipamento.getSeguro());
    }
}