/*
 * Copyright (C) 2026 Jens Kutschke / BR-LAWYER Team
 *
 * This file is part of j-lawyer.org.
 *
 * j-lawyer.org is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package org.jlawyer.io.rest.v8;

import com.jdimension.jlawyer.domain.legal.model.PublicationDetailDTO;
import com.jdimension.jlawyer.domain.legal.model.PublicationLinkRequestDTO;
import org.jlawyer.io.rest.v8.pojo.RestfulPublicationTreatRequestV8;
import javax.ejb.Local;
import javax.ws.rs.core.Response;

@Local
public interface PublicationsEndpointLocalV8 {

    Response listPublications(String status, String readStatus, String treatmentStatus, String courtCode,
                              String processId, String cnjNumber, String assignedUser, String lawyerOab,
                              String searchText, String fromDate, String toDate, int limit);

    Response getPublicationPage(String status, String readStatus, String treatmentStatus, String courtCode,
                                String processId, String cnjNumber, String assignedUser, String lawyerOab,
                                String searchText, String fromDate, String toDate, int page, int pageSize);

    Response getPublication(String id);

    Response createPublication(PublicationDetailDTO dto);

    Response updatePublication(String id, PublicationDetailDTO dto);

    Response assignPublication(String id, String assignedUser);

    Response linkCase(String id, PublicationLinkRequestDTO request);

    Response unlinkCase(String id);

    Response markRead(String id, boolean read);

    Response treatPublication(String id, RestfulPublicationTreatRequestV8 request);

    Response archivePublication(String id, String reason);

    Response deletePublication(String id);
}