package com.sludge_system.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "spectrum_sample")
public class SpectrumSample {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private SamplingSite site;

    @Convert(converter = DoubleListJsonConverter.class)
    @Column(name = "bands", nullable = false, columnDefinition = "json")
    private List<Double> bands;

    @Column(name = "band_count", nullable = false)
    private Integer bandCount;

    @Column(name = "source", nullable = false, length = 32)
    private String source;

    @Column(name = "captured_at")
    private LocalDateTime capturedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "qc_status", nullable = false, length = 16)
    private QcStatus qcStatus;

    @Column(name = "qc_message", length = 512)
    private String qcMessage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
