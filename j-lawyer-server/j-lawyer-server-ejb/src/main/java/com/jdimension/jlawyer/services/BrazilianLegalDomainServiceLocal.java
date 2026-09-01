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
 * Contrato local EJB para operações de domínio jurídico brasileiro.
 *
 * @author BR-LAWYER Team
 */
@Local
public interface BrazilianLegalDomainServiceLocal {

    List<LawyerRegistrationDTO> getLawyerRegistrations(String contactId) throws Exception;

    LawyerRegistrationDTO saveLawyerRegistration(LawyerRegistrationDTO registration) throws Exception;

    void deleteLawyerRegistration(String registrationId) throws Exception;

    BrazilianCaseDetailsDTO getCaseDetails(String caseId) throws Exception;

    BrazilianCaseDetailsDTO saveCaseDetails(BrazilianCaseDetailsDTO details) throws Exception;

    BrazilianCaseDetailsDTO findCaseByCnjNumber(String cnjNumber) throws Exception;

    List<CaseTpuSubjectDTO> getCaseTpuSubjects(String caseId) throws Exception;

    void setCaseTpuSubjects(String caseId, List<CaseTpuSubjectDTO> subjects) throws Exception;

    List<JudiciaryCourtDTO> listCourts() throws Exception;

    List<JudiciaryCourtDTO> listCourtsBySegment(int justiceSegment) throws Exception;

    JudiciaryCourtDTO getCourtByCode(String courtCode) throws Exception;

    List<TpuClassDTO> listTpuClasses() throws Exception;

    List<TpuClassDTO> searchTpuClasses(String query) throws Exception;

    List<TpuSubjectDTO> listTpuSubjects() throws Exception;

    List<TpuSubjectDTO> searchTpuSubjects(String query) throws Exception;
}
