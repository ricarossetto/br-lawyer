/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.services;

import com.jdimension.jlawyer.domain.legal.model.WorkflowDashboardDTO;

import javax.ejb.Local;

/**
 * Interface EJB Local para consolidação de métricas do Dashboard Operacional do Workflow Brasileiro.
 *
 * @author BR-LAWYER Team
 */
@Local
public interface BrazilianWorkflowDashboardServiceLocal {

    WorkflowDashboardDTO getDashboard(String currentUser) throws Exception;
}