/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.legal.model;

import java.io.Serializable;

/**
 * DTO para Classes Processuais TPU/CNJ.
 *
 * @author BR-LAWYER Team
 */
public class TpuClassDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private int code;
    private String name;
    private String glossary;
    private String nature;
    private boolean active;

    public TpuClassDTO() {
    }

    public TpuClassDTO(int code, String name) {
        this.code = code;
        this.name = name;
        this.active = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGlossary() {
        return glossary;
    }

    public void setGlossary(String glossary) {
        this.glossary = glossary;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getFormatted() {
        return code + " - " + name;
    }

    @Override
    public String toString() {
        return getFormatted();
    }
}
