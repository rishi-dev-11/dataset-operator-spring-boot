package com.dataset.operator.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(
        name = "dataset_records",
        indexes = @Index(name = "idx_dataset_records_dataset_id", columnList = "dataset_id")
)
public class DatasetRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dataset_id", nullable = false)
    private DatasetEntity dataset;

    @Column(name = "external_record_id", length = 128)
    private String externalRecordId;

    @Lob
    @Column(name = "payload_json", nullable = false, columnDefinition = "TEXT")
    private String payloadJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected DatasetRecordEntity() {
        // JPA default constructor
    }

    public DatasetRecordEntity(DatasetEntity dataset, String externalRecordId, String payloadJson) {
        this.dataset = dataset;
        this.externalRecordId = externalRecordId;
        this.payloadJson = payloadJson;
    }

    @PrePersist
    void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public DatasetEntity getDataset() {
        return dataset;
    }

    public String getExternalRecordId() {
        return externalRecordId;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
