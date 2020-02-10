package com.wirelesscar.vw.tscs.rest.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class ResponseCertificates {
  private List<ResponseCertificate> certficates = new ArrayList<>();

  public ResponseCertificates() {
  }

  public ResponseCertificates(List<ResponseCertificate> certficates) {
    this.certficates = certficates;
  }

  public List<ResponseCertificate> getCertficates() {
    return certficates;
  }

  public void setCertficates(List<ResponseCertificate> certficates) {
    this.certficates = certficates;
  }

  public static ResponseCertificates fromJson(String jsonString) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(jsonString, ResponseCertificates.class);
  }

  public String toJSON() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(this);
  }
}
