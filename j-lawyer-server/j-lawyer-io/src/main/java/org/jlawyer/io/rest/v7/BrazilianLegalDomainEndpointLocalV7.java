/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package org.jlawyer.io.rest.v7;

import com.jdimension.jlawyer.domain.legal.model.*;
import javax.ejb.Local;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Contrato local para o endpoint REST v7 de domínio jurídico brasileiro.
 *
 * @author BR-LAWYER Team
 */
@Local
public interface BrazilianLegalDomainEndpointLocalV7 {

    Response getCaseDetails(String caseId);

    Response saveCaseDetails(String caseId, BrazilianCaseDetailsDTO details);

    Response findCaseByCnj(String cnjNumber);

    Response getLawyerRegistrations(String contactId);

    Response saveLawyerRegistration(String contactId, LawyerRegistrationDTO registration);

    Response deleteLawyerRegistration(String registrationId);

    Response listCourts(Integer segment);

    Response getCourtByCode(String code);

    Response listTpuClasses(String query);

    Response listTpuSubjects(String query);
}
