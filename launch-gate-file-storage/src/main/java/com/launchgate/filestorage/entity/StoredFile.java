package com.launchgate.filestorage.entity;

import com.launchgate.identity.entity.UserAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;

/**
 * Файл.
 */
@Entity
@Table(schema = "file_storage", name = "stored_files")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoredFile {

    /**
     * Уникальный идентификатор.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Пользователь, загрузивший файл.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserAccount owner;

    /**
     * Корзина.
     */
    @Column(nullable = false, length = 120)
    private String bucket;

    /**
     * Ключ файла в MinIO.
     */
    @Column(name = "object_key", nullable = false, unique = true, length = 500)
    private String objectKey;

    /**
     * Имя файла.
     */
    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    /**
     * MIME тип файла.
     */
    @Column(name = "content_type", length = 120)
    private String contentType;

    /**
     * Размер файла в байтах.
     */
    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    /**
     * Дата и время создания файла.
     */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public StoredFile(UserAccount owner, String bucket, String objectKey, String originalFilename,
                      String contentType, long sizeBytes, Instant createdAt) {
        this.owner = owner;
        this.bucket = bucket;
        this.objectKey = objectKey;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.createdAt = createdAt;
    }

    public Long getOwnerId() {
        return owner.getId();
    }

}
