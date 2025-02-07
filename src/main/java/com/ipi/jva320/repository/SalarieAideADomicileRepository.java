package com.ipi.jva320.repository;

import com.ipi.jva320.model.SalarieAideADomicile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalarieAideADomicileRepository extends PagingAndSortingRepository<SalarieAideADomicile, Long> {

    SalarieAideADomicile findByNom(String nom);

    @Query("select sum(congesPayesPrisAnneeNMoins1)/sum(congesPayesAcquisAnneeNMoins1) from SalarieAideADomicile")
    Double partCongesPrisTotauxAnneeNMoins1();

    Page<SalarieAideADomicile> findAllByNom(String nom, Pageable pageable);
}
