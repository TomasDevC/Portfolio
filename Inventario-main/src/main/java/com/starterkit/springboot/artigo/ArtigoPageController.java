package com.starterkit.springboot.artigo;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/artigos")
public class ArtigoPageController {

    private final ArtigoService artigoService;

    @Value("${security.api-key.admin:}")
    private String adminKey;

    public ArtigoPageController(ArtigoService artigoService) { this.artigoService = artigoService; }

    @GetMapping
    public String lista(Model model) {
        model.addAttribute("artigos", artigoService.listAll());
        model.addAttribute("pageScript", "/js/artigos.js");
        return "artigos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("artigoForm", new ArtigoForm());
        model.addAttribute("modo", "novo");
        return "artigos/form";
    }

@GetMapping("/scan")
public String scan(Model model) {
    model.addAttribute("titulo", "Ler QR Code");
    model.addAttribute("pageScript", "/js/artigos.js");
    return "artigos/scan";
}

@GetMapping("/codigo/{codigo}")
public String detalhePorCodigo(@PathVariable String codigo, Model model) {
    Artigo artigo = artigoService.getByCodigo(codigo);
    model.addAttribute("titulo", "Ficha do Artigo");
    model.addAttribute("artigo", artigo);
    model.addAttribute("pageScript", "/js/artigos.js");
    return "artigos/detalhe";
}

    @PostMapping
    public String criar(@Valid ArtigoForm form, BindingResult br, Model model) {
        if (!isValidKey(form.getAdminApiKey())) br.rejectValue("adminApiKey", "error", "Chave Inválida");
        if (br.hasErrors()) return "artigos/form";
        artigoService.create(form);
        return "redirect:/artigos?status=created";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Artigo a = artigoService.getById(id);
        ArtigoForm f = new ArtigoForm();
        copyToForm(a, f);
        model.addAttribute("artigoForm", f);
        model.addAttribute("artigo", a);
        model.addAttribute("modo", "editar");
        return "artigos/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Valid ArtigoForm form, BindingResult br) {
        if (!isValidKey(form.getAdminApiKey())) br.rejectValue("adminApiKey", "error", "Chave Inválida");
        if (br.hasErrors()) return "artigos/form";
        artigoService.update(id, form);
        return "redirect:/artigos?status=updated";
    }

    private boolean isValidKey(String key) { return key != null && key.equals(adminKey); }

    private void copyToForm(Artigo a, ArtigoForm f) {
        f.setConsumoWatts(a.getConsumoWatts());
        f.setTipoLigacao(a.getTipoLigacao());
        f.setSocketProcessador(a.getSocketProcessador());
        f.setSlotsRamTotal(a.getSlotsRamTotal());
        f.setBarramentoPrincipal(a.getBarramentoPrincipal());
        f.setMaterialConstrucao(a.getMaterialConstrucao());
        f.setSuporteWindows11(a.getSuporteWindows11());
        f.setLayoutTeclado(a.getLayoutTeclado());
        f.setTdpMaximo(a.getTdpMaximo());
        f.setSlotsExpansao(a.getSlotsExpansao());
        f.setVersaoBluetooth(a.getVersaoBluetooth());
        f.setCameraPrivacidade(a.getCameraPrivacidade());
        f.setLogsTecnicos(a.getLogsTecnicos());
    }
}