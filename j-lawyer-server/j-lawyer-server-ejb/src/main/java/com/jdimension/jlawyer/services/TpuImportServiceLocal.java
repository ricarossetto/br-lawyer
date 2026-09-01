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

import javax.ejb.Local;
import java.util.List;

/**
 * Interface EJB Local para importação e versionamento de Classes e Assuntos TPU/CNJ.
 *
 * @author BR-LAWYER Team
 */
@Local
public interface TpuImportServiceLocal {

    int importClasses(List<TpuClassDTO> classes, String source, String sourceVersion) throws Exception;

    int importSubjects(List<TpuSubjectDTO> subjects, String source, String sourceVersion) throws Exception;

    String getLatestTpuVersion() throws Exception;
}
