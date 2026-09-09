package com.starterkit.springboot.equipamento;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.http.HttpStatus;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EquipamentoService {

    private final EquipamentoRepository repo;
    private final Path equipamentosUploadDir;

    public EquipamentoService(EquipamentoRepository repo, @Value("${app.upload-dir:./uploads}") String uploadDir) {
        this.repo = repo;
        this.equipamentosUploadDir = Paths.get(uploadDir).toAbsolutePath().normalize().resolve("equipamentos");
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(equipamentosUploadDir);
        } catch (IOException ex) {
            throw new IllegalStateException("Nao foi possivel criar a pasta de uploads", ex);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void fillMissingCodigoUnico() {
        List<Equipamento> equipamentos = repo.findAll();
        boolean changed = false;
        for (Equipamento equipamento : equipamentos) {
            if (!StringUtils.hasText(equipamento.getCodigoUnico())) {
                equipamento.setCodigoUnico(UUID.randomUUID().toString());
                changed = true;
            }
        }
        if (changed) {
            repo.saveAll(equipamentos);
        }
    }

    public List<Equipamento> listAll() {
        return repo.findAll();
    }

    public Equipamento getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipamento nao encontrado"));
    }

    public Equipamento getByCodigo(String codigo) {
        return repo.findByCodigoUnico(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipamento nao encontrado"));
    }

    public Equipamento create(EquipamentoRequest request) {
        Equipamento equipamento = new Equipamento();
        applyRequest(equipamento, request);
        return repo.save(equipamento);
    }

    public Equipamento create(EquipamentoForm form) {
        Equipamento equipamento = new Equipamento();
        applyRequest(equipamento, form);
        equipamento.setImagemPath(storeImage(form.getImagem(), null));
        return repo.save(equipamento);
    }

    public Equipamento update(Long id, EquipamentoRequest request) {
        Equipamento equipamento = getById(id);
        applyRequest(equipamento, request);
        return repo.save(equipamento);
    }

    public Equipamento update(Long id, EquipamentoForm form) {
        Equipamento equipamento = getById(id);
        applyRequest(equipamento, form);
        equipamento.setImagemPath(storeImage(form.getImagem(), equipamento.getImagemPath()));
        return repo.save(equipamento);
    }

    public void delete(Long id) {
        Equipamento equipamento = getById(id);
        deleteStoredImage(equipamento.getImagemPath());
        repo.delete(equipamento);
    }

    private void applyRequest(Equipamento equipamento, EquipamentoRequest request) {
        equipamento.setCategoria(request.getCategoria());
        equipamento.setDataCompra(request.getDataCompra());
        equipamento.setModelo(request.getModelo());
        equipamento.setMarca(request.getMarca());
        equipamento.setNumeroSerie(request.getNumeroSerie());
        equipamento.setLocal(request.getLocal());
        equipamento.setGarantia(Boolean.TRUE.equals(request.getGarantia()));
        equipamento.setSeguro(Boolean.TRUE.equals(request.getSeguro()));
    }

    private String storeImage(MultipartFile imagem, String currentImagePath) {
        if (imagem == null || imagem.isEmpty()) {
            return currentImagePath;
        }

        String originalName = StringUtils.cleanPath(imagem.getOriginalFilename());
        String extension = getExtension(originalName);
        if (!isAllowedImageExtension(extension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de imagem nao suportado");
        }

        String generatedName = UUID.randomUUID().toString() + extension;
        Path destination = equipamentosUploadDir.resolve(generatedName).normalize();
        if (!destination.startsWith(equipamentosUploadDir)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome de ficheiro invalido");
        }

        try (InputStream inputStream = imagem.getInputStream()) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Falha ao guardar a imagem");
        }

        deleteStoredImage(currentImagePath);
        return "equipamentos/" + generatedName;
    }

    private void deleteStoredImage(String imagePath) {
        if (!StringUtils.hasText(imagePath)) {
            return;
        }

        String relativePath = imagePath.replace('/', java.io.File.separatorChar);
        Path filePath = equipamentosUploadDir.getParent().resolve(relativePath).normalize();
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Falha ao remover a imagem");
        }
    }

    private String getExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index < 0) {
            return "";
        }
        return fileName.substring(index).toLowerCase(Locale.ROOT);
    }

    private boolean isAllowedImageExtension(String extension) {
        return ".png".equals(extension)
                || ".jpg".equals(extension)
                || ".jpeg".equals(extension)
                || ".webp".equals(extension)
                || ".gif".equals(extension);
    }
}
