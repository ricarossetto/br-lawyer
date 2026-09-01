/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.services;

import com.jdimension.jlawyer.domain.legal.model.TpuClassDTO;
import com.jdimension.jlawyer.domain.legal.model.TpuSubjectDTO;
import com.jdimension.jlawyer.persistence.BrTpuClass;
import com.jdimension.jlawyer.persistence.BrTpuSubject;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementação EJB para Importação e Versionamento de Classes e Assuntos TPU/CNJ.
 *
 * @author BR-LAWYER Team
 */
@Stateless
public class TpuImportService implements TpuImportServiceLocal, TpuImportServiceRemote {

    private static final Logger log = Logger.getLogger(TpuImportService.class.getName());

    @PersistenceContext(unitName = "j-lawyer-server-ejbPU")
    private EntityManager em;

    @Override
    public int importClasses(List<TpuClassDTO> classes, String source, String sourceVersion) throws Exception {
        if (classes == null || classes.isEmpty()) {
            return 0;
        }
        int count = 0;
        Date now = new Date();
        for (TpuClassDTO dto : classes) {
            TypedQuery<BrTpuClass> q = em.createNamedQuery("BrTpuClass.findByCode", BrTpuClass.class);
            q.setParameter("code", dto.getCode());
            List<BrTpuClass> existing = q.getResultList();
            BrTpuClass entity;
            if (!existing.isEmpty()) {
                entity = existing.get(0);
                entity.setName(dto.getName());
                entity.setGlossary(dto.getGlossary());
                entity.setNature(dto.getNature());
                entity.setSource(source != null ? source : "CNJ_TPU");
                entity.setSourceVersion(sourceVersion != null ? sourceVersion : "2026.1");
                entity.setLastUpdatedAt(now);
                em.merge(entity);
            } else {
                entity = new BrTpuClass(UUID.randomUUID().toString(), dto.getCode(), dto.getName());
                entity.setGlossary(dto.getGlossary());
                entity.setNature(dto.getNature());
                entity.setSource(source != null ? source : "CNJ_TPU");
                entity.setSourceVersion(sourceVersion != null ? sourceVersion : "2026.1");
                entity.setImportedAt(now);
                entity.setLastUpdatedAt(now);
                entity.setActive(true);
                em.persist(entity);
            }
            count++;
        }
        log.infof("Importadas/atualizadas %d classes TPU versão %s (%s)", count, sourceVersion, source);
        return count;
    }

    @Override
    public int importSubjects(List<TpuSubjectDTO> subjects, String source, String sourceVersion) throws Exception {
        if (subjects == null || subjects.isEmpty()) {
            return 0;
        }
        int count = 0;
        Date now = new Date();
        for (TpuSubjectDTO dto : subjects) {
            TypedQuery<BrTpuSubject> q = em.createNamedQuery("BrTpuSubject.findByCode", BrTpuSubject.class);
            q.setParameter("code", dto.getCode());
            List<BrTpuSubject> existing = q.getResultList();
            BrTpuSubject entity;
            if (!existing.isEmpty()) {
                entity = existing.get(0);
                entity.setName(dto.getName());
                entity.setParentCode(dto.getParentCode());
                entity.setGlossary(dto.getGlossary());
                entity.setSource(source != null ? source : "CNJ_TPU");
                entity.setSourceVersion(sourceVersion != null ? sourceVersion : "2026.1");
                entity.setLastUpdatedAt(now);
                em.merge(entity);
            } else {
                entity = new BrTpuSubject(UUID.randomUUID().toString(), dto.getCode(), dto.getName());
                entity.setParentCode(dto.getParentCode());
                entity.setGlossary(dto.getGlossary());
                entity.setSource(source != null ? source : "CNJ_TPU");
                entity.setSourceVersion(sourceVersion != null ? sourceVersion : "2026.1");
                entity.setImportedAt(now);
                entity.setLastUpdatedAt(now);
                entity.setActive(true);
                em.persist(entity);
            }
            count++;
        }
        log.infof("Importados/atualizados %d assuntos TPU versão %s (%s)", count, sourceVersion, source);
        return count;
    }

    @Override
    public String getLatestTpuVersion() throws Exception {
        List<String> results = em.createQuery("SELECT DISTINCT c.sourceVersion FROM BrTpuClass c WHERE c.sourceVersion IS NOT NULL ORDER BY c.sourceVersion DESC", String.class)
                .setMaxResults(1)
                .getResultList();
        return results.isEmpty() ? "2026.1" : results.get(0);
    }
}
