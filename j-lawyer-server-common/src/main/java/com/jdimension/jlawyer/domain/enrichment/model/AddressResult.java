/*
 *                     GNU AFFERO GENERAL PUBLIC LICENSE
 *                        Version 3, 19 November 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.jdimension.jlawyer.domain.enrichment.model;

import java.io.Serializable;

/**
 * Modelo canônico de resultado de consulta de endereço brasileiro.
 *
 * @author BR-LAWYER Team
 */
public class AddressResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cep;
    private String street;          // Logradouro (ex: Av. Paulista)
    private String streetType;      // Tipo de logradouro (ex: Avenida, Rua, Praça)
    private String number;          // Número
    private String complement;      // Complemento (ex: Sala 142)
    private String neighborhood;    // Bairro
    private String city;            // Município
    private String state;           // UF (ex: SP)
    private String ibgeCityCode;    // Código IBGE de 7 dígitos (ex: 3550308)
    private String giaCode;         // Código GIA estadual
    private String siafiCode;       // Código SIAFI
    private String ddd;             // Código de discagem (ex: 11)
    private Double latitude;        // Coordenada geográfica
    private Double longitude;
    private RegistryProvenance provenance;

    public AddressResult() {
        this.provenance = new RegistryProvenance();
    }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getStreetType() { return streetType; }
    public void setStreetType(String streetType) { this.streetType = streetType; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getComplement() { return complement; }
    public void setComplement(String complement) { this.complement = complement; }

    public String getNeighborhood() { return neighborhood; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getIbgeCityCode() { return ibgeCityCode; }
    public void setIbgeCityCode(String ibgeCityCode) { this.ibgeCityCode = ibgeCityCode; }

    public String getGiaCode() { return giaCode; }
    public void setGiaCode(String giaCode) { this.giaCode = giaCode; }

    public String getSiafiCode() { return siafiCode; }
    public void setSiafiCode(String siafiCode) { this.siafiCode = siafiCode; }

    public String getDdd() { return ddd; }
    public void setDdd(String ddd) { this.ddd = ddd; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public RegistryProvenance getProvenance() { return provenance; }
    public void setProvenance(RegistryProvenance provenance) { this.provenance = provenance; }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (street != null && !street.isEmpty()) sb.append(street);
        if (number != null && !number.isEmpty()) sb.append(", ").append(number);
        if (complement != null && !complement.isEmpty()) sb.append(" - ").append(complement);
        if (neighborhood != null && !neighborhood.isEmpty()) sb.append(", ").append(neighborhood);
        if (city != null && !city.isEmpty()) sb.append(", ").append(city);
        if (state != null && !state.isEmpty()) sb.append(" - ").append(state);
        if (cep != null && !cep.isEmpty()) sb.append(", CEP ").append(cep);
        return sb.toString();
    }
}
