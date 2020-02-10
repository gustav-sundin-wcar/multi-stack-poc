package com.wirelesscar.vw.tscs.rest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelesscar.vw.tscs.db.model.Certificate;

import java.util.Date;

public class ResponseCertificate {
  private String id;
  private String certificateData;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date createdAt;
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date updatedAt;

  public ResponseCertificate(Certificate dbCertificate) {
    id = dbCertificate.getId();
    certificateData = dbCertificate.getCertificateData();
    createdAt = dbCertificate.getCreatedAt();
    updatedAt = dbCertificate.getUpdatedAt();
  }

  public ResponseCertificate() {

  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCertificateData() {
    return certificateData;
  }

  public void setCertificateData(String certificateData) {
    this.certificateData = certificateData;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public static ResponseCertificate fromJson(String jsonString) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(jsonString, ResponseCertificate.class);
  }

  public String toJSON() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(this);
  }
}
