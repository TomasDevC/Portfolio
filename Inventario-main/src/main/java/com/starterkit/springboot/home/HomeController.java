package com.starterkit.springboot.home;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.starterkit.springboot.artigo.ArtigoRepository;
import com.starterkit.springboot.equipamento.EquipamentoRepository;
import com.starterkit.springboot.fornecedor.Fornecedor;
import com.starterkit.springboot.fornecedor.FornecedorRepository;
import com.starterkit.springboot.user.UserRepository;



@Controller
public class HomeController {
 
    // Repositório injetado no controller para aceder à base de dados
    private final EquipamentoRepository EquipRepo;
    private final FornecedorRepository FornRepo;
    private final UserRepository UserRepo;
    private final ArtigoRepository ArtigoRepo;
    // Construtor do controller.
    // O Spring injeta automaticamente uma instância de EquipamentoRepository.
    public HomeController(EquipamentoRepository EquipRepo, 
                          FornecedorRepository FornRepo, 
                          ArtigoRepository ArtigoRepo,
                          UserRepository UserRepo) {
        this.EquipRepo = EquipRepo;
        this.FornRepo = FornRepo;
        this.UserRepo = UserRepo;
        this.ArtigoRepo = ArtigoRepo;
    }

  // Mapeia pedidos HTTP GET para o caminho "/"
  // Quando alguém abre a página inicial, este método é executado.
  @GetMapping("/")
  public String home(Model model) {

      model.addAttribute("titulo", "Página Principal nº2");
      model.addAttribute("cenas", "montes de cenas");

      // Vai buscar todos os equipamentos à base de dados
      // e coloca a lista no Model com o nome "equipamentos"
      // para ficar disponível na view index.html
      model.addAttribute("equipamentos", EquipRepo.findAll());
      model.addAttribute("fornecedores", FornRepo.findAll());
      model.addAttribute("Utilizadores", UserRepo.findAll());

      // Devolve o nome da view a renderizar: "index"
      // O Spring vai procurar o ficheiro index.html
      return "index";
  }


        @GetMapping("/fornecedores/{id}")
        public String fornecedorPorId(@PathVariable Long id, Model model) {

            Fornecedor fornecedor = FornRepo.findById(id).orElse(null);

            if (fornecedor == null) {
                return "notfound";
            }

            model.addAttribute("titulo", "Detalhe do fornecedor");
            model.addAttribute("fornecedores", fornecedor);
            return "fornecedores";
        }


   @GetMapping("/fornecedores/")
  public String fornecedores(Model model) {

      model.addAttribute("titulo", "Página Principal nº2");
      model.addAttribute("fornecedores", FornRepo.findAll());

      // Devolve o nome da view a renderizar: "index"
      // O Spring vai procurar o ficheiro index.html
      return "fornecedores";
  }
}

