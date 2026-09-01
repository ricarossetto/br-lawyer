/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.services;

import com.jdimension.jlawyer.domain.legal.model.*;

import javax.ejb.Local;
import java.util.List;

/**
 * Interface EJB Local para Gestão e Triagem de Publicações e Intimações Judiciais Brasileiras.
 *
 * @author BR-LAWYER Team
 */
@Local
public interface BrazilianPublicationServiceLocal {

    PublicationDetailDTO getPublication(String id) throws Exception;

    PublicationDetailDTO savePublication(PublicationDetailDTO dto, String actor) throws Exception;

    List<PublicationOverviewDTO> listPublications(PublicationFilterDTO filter) throws Exception;

    long countPublications(PublicationFilterDTO filter) throws Exception;

    PublicationDetailDTO markRead(String publicationId, boolean read, String user) throws Exception;

    PublicationDetailDTO linkToCase(String publicationId, PublicationLinkRequestDTO request) throws Exception;

    PublicationDetailDTO unlinkFromCase(String publicationId, String user) throws Exception;

    PublicationDetailDTO treatPublication(String publicationId, PublicationTreatRequestDTO request) throws Exception;

    PublicationDetailDTO archivePublication(String publicationId, String user, String reason) throws Exception;

    PublicationDetailDTO assignPublication(String publicationId, String assignedUser, String actor) throws Exception;

    PublicationDetailDTO deduplicateAndIngest(PublicationDetailDTO dto, String actor) throws Exception;

    List<PublicationEventDTO> getPublicationHistory(String publicationId) throws Exception;

    void deletePublication(String publicationId, String user) throws Exception;
}