package br.com.knowledgebase.application.usecases;

import br.com.knowledgebase.domain.model.ContentEncoding;
import br.com.knowledgebase.domain.model.FileAsset;
import br.com.knowledgebase.domain.model.Snapshot;
import br.com.knowledgebase.domain.ports.out.FileRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class FileServiceTest {

    @Test
    void getWithoutBase64UsesMetadataSnapshotLookup() {
        FakeFileRepository repo = new FakeFileRepository();
        Snapshot snapshot = new Snapshot(
                142L,
                "doc.pdf",
                "application/pdf",
                ContentEncoding.IDENTITY,
                null,
                3L,
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                null
        );
        repo.snapshotById = Optional.of(snapshot);

        Snapshot result = new FileService(repo).get(142L, false);

        assertThat(result).isEqualTo(snapshot);
        assertThat(repo.findSnapshotByIdCalls).isEqualTo(1);
        assertThat(repo.findByIdCalls).isZero();
    }

    @Test
    void getWithBase64KeepsPayloadLookup() {
        FakeFileRepository repo = new FakeFileRepository();
        byte[] payload = new byte[] {1, 2, 3};
        FileAsset file = new FileAsset(
                142L,
                "doc.pdf",
                "application/pdf",
                ContentEncoding.IDENTITY,
                null,
                3L,
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                payload
        );
        repo.fileById = Optional.of(file);

        Snapshot result = new FileService(repo).get(142L, true);

        assertThat(result.base64()).isEqualTo(Base64.getEncoder().encodeToString(payload));
        assertThat(repo.findByIdCalls).isEqualTo(1);
        assertThat(repo.findSnapshotByIdCalls).isZero();
    }

    private static final class FakeFileRepository implements FileRepositoryPort {
        Optional<FileAsset> fileById = Optional.empty();
        Optional<Snapshot> snapshotById = Optional.empty();
        int findByIdCalls;
        int findSnapshotByIdCalls;

        @Override
        public Long saveUpload(FileAsset file) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<FileAsset> findById(Long id) {
            findByIdCalls++;
            return fileById;
        }

        @Override
        public Optional<Snapshot> findSnapshotById(Long id) {
            findSnapshotByIdCalls++;
            return snapshotById;
        }

        @Override
        public boolean existsById(Long id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteById(Long id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<FileAsset> findAllByIds(List<Long> ids) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Snapshot> findSnapshotsByIds(List<Long> ids) {
            throw new UnsupportedOperationException();
        }
    }
}
