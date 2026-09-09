package com.starterkit.springboot.artigo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.starterkit.springboot.equipamento.Equipamento;

@Service
public class ArtigoService {

    private final ArtigoRepository repo;
    private final Path artigosUploadDir;

    public ArtigoService(ArtigoRepository repo, @Value("${app.upload-dir:./uploads}") String uploadDir) {
        this.repo = repo;
        this.artigosUploadDir = Paths.get(uploadDir).toAbsolutePath().normalize().resolve("artigos");
    }

    @PostConstruct
    public void init() {
        try { Files.createDirectories(artigosUploadDir); } 
        catch (IOException ex) { throw new IllegalStateException("Erro ao criar pasta de uploads", ex); }
    }

    public List<Artigo> listAll() { return repo.findAll(); }

    public Artigo getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Artigo getByCodigo(String codigo) {
        return repo.findByCodigoUnico(codigo).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public Artigo create(ArtigoRequest request) {
        Artigo artigo = new Artigo();
        copyData(request, artigo);
        if (request.getImagem() != null && !request.getImagem().isEmpty()) {
            artigo.setImagemPath(saveImage(request.getImagem(), null));
        }
        return repo.save(artigo);
    }

    @Transactional
    public Artigo update(Long id, ArtigoForm form) {
        Artigo artigo = getById(id);
        copyData(form, artigo);
        if (form.getImagem() != null && !form.getImagem().isEmpty()) {
            artigo.setImagemPath(saveImage(form.getImagem(), artigo.getImagemPath()));
        }
        return repo.save(artigo);
    }

    @Transactional
    public void delete(Long id) {
        Artigo artigo = getById(id);
        deleteStoredImage(artigo.getImagemPath());
        repo.delete(artigo);
    }

    private void copyData(ArtigoRequest src, Artigo dest) {
        dest.setConsumoWatts(src.getConsumoWatts());
        dest.setTipoLigacao(src.getTipoLigacao());
        dest.setSocketProcessador(src.getSocketProcessador());
        dest.setSlotsRamTotal(src.getSlotsRamTotal());
        dest.setBarramentoPrincipal(src.getBarramentoPrincipal());
        dest.setMaterialConstrucao(src.getMaterialConstrucao());
        dest.setSuporteWindows11(src.getSuporteWindows11());
        dest.setLayoutTeclado(src.getLayoutTeclado());
        dest.setTdpMaximo(src.getTdpMaximo());
        dest.setSlotsExpansao(src.getSlotsExpansao());
        dest.setVersaoBluetooth(src.getVersaoBluetooth());
        dest.setCameraPrivacidade(src.getCameraPrivacidade());
        dest.setLogsTecnicos(src.getLogsTecnicos());
    }

    private String saveImage(MultipartFile imagem, String currentImagePath) {
        String extension = getExtension(imagem.getOriginalFilename());
        String generatedName = UUID.randomUUID().toString() + extension;
        Path destination = artigosUploadDir.resolve(generatedName);

        try (InputStream is = imagem.getInputStream()) {
            Files.copy(is, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) { throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao salvar imagem"); }

        deleteStoredImage(currentImagePath);
        return "artigos/" + generatedName;
    }

    private void deleteStoredImage(String path) {
        if (!StringUtils.hasText(path)) return;
        try { Files.deleteIfExists(artigosUploadDir.getParent().resolve(path.replace('/', java.io.File.separatorChar)).normalize()); } 
        catch (IOException ignored) {}
    }

    private String getExtension(String fn) {
        return fn.substring(fn.lastIndexOf('.')).toLowerCase(Locale.ROOT);
    }
    
}