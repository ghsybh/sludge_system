package com.sludge_system.repository;

import com.sludge_system.domain.SamplingSite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SamplingSiteRepository extends JpaRepository<SamplingSite, Long> {
    Page<SamplingSite> findBySiteCodeContainingIgnoreCaseOrSiteNameContainingIgnoreCase(
            String siteCode, String siteName, Pageable pageable);

    SamplingSite findBySiteCode(String siteCode);
}
