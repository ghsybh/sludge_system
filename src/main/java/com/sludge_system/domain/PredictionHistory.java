package com.sludge_system.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "prediction_history")
public class PredictionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "operator_id")
    private Long operatorId;

    @Column(name = "task_type", nullable = false, length = 16)
    private String taskType;

    @Column(name = "model_type", nullable = false, length = 32)
    private String modelType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "input_sample_id")
    private SpectrumSample inputSample;

    @Convert(converter = DoubleListJsonConverter.class)
    @Column(name = "input_bands", columnDefinition = "json")
    private List<Double> inputBands;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pred_site_id")
    private SamplingSite predSite;

    @Column(name = "pred_site_code", length = 64)
    private String predSiteCode;

    @Column(name = "pred_organic")
    private Double predOrganic;

    @Column(name = "pred_tn")
    private Double predTn;

    @Column(name = "pred_tp")
    private Double predTp;

    @Convert(converter = MapJsonConverter.class)
    @Column(name = "metrics", columnDefinition = "json")
    private Map<String, Object> metrics;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
