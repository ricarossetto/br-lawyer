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

import javax.ejb.Remote;
import java.util.List;

/**
 * Interface EJB Remote para importação e versionamento de Classes e Assuntos TPU/CNJ.
 * Permite atualização dinâmica a partir de dados abertos/APIs do CNJ sem alteração manual de migrations.
 *
 * @author BR-LAWYER Team
 */
@Remote
public interface TpuImportServiceRemote {

    /**
     * Importa ou atualiza em lote uma lista de Classes Processuais TPU.
     */
    int importClasses(List<TpuClassDTO> classes, String source, String sourceVersion) throws Exception;

    /**
     * Importa ou atualiza em lote uma lista de Assuntos Processuais TPU.
     */
    int importSubjects(List<TpuSubjectDTO> subjects, String source, String sourceVersion) throws Exception;

    /**
     * Retorna a versão mais recente das tabelas TPU importadas no sistema.
     */
    String getLatestTpuVersion() throws Exception;
}
