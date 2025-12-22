package br.com.knowledgebase.application.usecases;

import br.com.knowledgebase.domain.exception.ResourceNotFoundException;
import br.com.knowledgebase.domain.ports.in.ActivityFilterParams;
import br.com.knowledgebase.domain.ports.in.ActivityPageResult;
import br.com.knowledgebase.domain.model.Activity;
import br.com.knowledgebase.domain.ports.in.ActivityUseCase;
import br.com.knowledgebase.domain.ports.out.ActivityRepositoryPort;

import br.com.knowledgebase.domain.ports.out.FileRepositoryPort;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ActivityUseCaseImpl implements ActivityUseCase {

    private final ActivityRepositoryPort repo;
    private final FileRepositoryPort fileRepo;

    public ActivityUseCaseImpl(ActivityRepositoryPort repo, FileRepositoryPort fileRepo) {
        this.repo = repo;
        this.fileRepo = fileRepo;
    }

    @Override
    public Activity create(Activity a) {
        return repo.save(a);
    }

    @Override
    public Optional<Activity> get(Long id) {
        return repo.findById(id);
    }

    @Override
    public Activity update(Long id, Activity a) {
        final var now = LocalDateTime.now();
        return repo.findById(id).map(exist -> {
            exist.updateFrom(a, now);
            return repo.save(exist);
        }).orElseThrow(() -> new NoSuchElementException("Atividade não encontrada"));
    }

    /**
     * Tudo-ou-nada:
     * - Se link NÃO existe => 404, nada é removido
     * - Se file NÃO existe => 404, nada é removido
     * - (opcional) Valida relacionamento link->file se seu modelo expõe essa info
     */
    @Transactional
    @Override
    public void deleteLinkAndFiles(Long linkId, List<Long> fileIds) {
        if (!repo.existsById(linkId)) {
            throw new ResourceNotFoundException("Link não encontrado: " + linkId);
        }
        if (fileIds != null) {
            for (Long fid : fileIds) {
                if (fid == null) continue;
                if (!fileRepo.existsById(fid)) {
                    throw new ResourceNotFoundException("File não encontrado: " + fid);
                }
            }
        }

        // Ordem já utilizada no projeto: primeiro o Link, depois os Files (rollback se falhar)
        repo.deleteById(linkId);

        if (fileIds != null) {
            for (Long fid : fileIds) {
                if (fid == null) continue;
                fileRepo.deleteById(fid);
            }
        }
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<Activity> list(int page, int size) {
        return repo.list(page, size);
    }

    @Override
    public List<Activity> search(String term, int page, int size) {
        return repo.search(term, page, size);
    }

    @Override
    public ActivityPageResult listWithFilters(ActivityFilterParams filterParams, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);

        Pageable pageable = PageRequest.of(
                safePage,
                safeSize,
                Sort.by("createdAt").descending()
        );

        Page<Activity> pageResult = repo.findWithFilters(
                filterParams,
                pageable
        );

        List<Activity> activities = pageResult.getContent();

        long total = pageResult.getTotalElements();  // total real de registros filtrados no banco

        return new ActivityPageResult(
                activities,
                total,
                pageResult.getNumber(),
                pageResult.getSize()
        );
    }

    @Override
    public List<String> listarCategorias(List<String> excessao) {
        final var excNorm = toNormalizedSet(excessao);

        return repo.findDistinctCategorias().stream()
                .filter(Objects::nonNull)
                .map(String::strip)
                .filter(Predicate.not(String::isBlank))
                .filter(cat -> {
                    String norm = ActivityFilterParams.normalize(cat);
                    return excNorm.isEmpty() || !excNorm.contains(norm);
                })
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    @Override
    public List<String> listarTags(List<String> excessao) {
        final var excNorm = toNormalizedSet(excessao);

        return repo.findDistinctTags().stream()
                .filter(Objects::nonNull)
                .map(String::strip)
                .filter(Predicate.not(String::isBlank))
                .filter(cat -> {
                    String norm = ActivityFilterParams.normalize(cat);
                    return excNorm.isEmpty() || !excNorm.contains(norm);
                })
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    private Set<String> toNormalizedSet(List<String> items) {
        if (items == null) return Set.of();
        return items.stream()
                .filter(Objects::nonNull)
                .map(String::strip)
                .filter(Predicate.not(String::isBlank))
                .map(ActivityFilterParams::normalize)
                .collect(Collectors.toUnmodifiableSet());
    }

}
