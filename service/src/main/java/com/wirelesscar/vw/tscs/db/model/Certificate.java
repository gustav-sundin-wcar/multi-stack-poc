package com.wirelesscar.vw.tscs.db.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@DynamoDBTable(tableName = "#TABLENAME_OVERRIDDEN_IN_REPOSITORY_CLASS#")
public class Certificate {
  private String id;
  private String certificateData;
  private static final Logger LOG = LoggerFactory.getLogger(Certificate.class);

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date createdAt;
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date updatedAt;

  public Certificate(String jsonString) throws JsonProcessingException {
    this(jsonString, false);
  }

  public Certificate(String jsonString, boolean ignoreGeneratedFields) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    Certificate tempCertificate = mapper.readValue(jsonString, Certificate.class);
    id = tempCertificate.getId();
    certificateData = tempCertificate.getCertificateData().replace("\n", "");
    if (!ignoreGeneratedFields) {
      createdAt = tempCertificate.getCreatedAt();
      updatedAt = tempCertificate.getUpdatedAt();
    }
  }

  public Certificate() {

  }

  public Certificate(String id, String certificateData) {
    this.id = id;
    this.certificateData = certificateData;
    createdAt = new Date();
    updatedAt = new Date();
  }

  @DynamoDBHashKey(attributeName = "id")
  public String getId() {
    return id;
  }

  @DynamoDBAttribute(attributeName = "certificate")
  public String getCertificateData() {
    return certificateData;
  }

  @DynamoDBAutoGeneratedTimestamp(strategy = DynamoDBAutoGenerateStrategy.CREATE)
  public Date getCreatedAt() {
    return createdAt;
  }

  @DynamoDBAutoGeneratedTimestamp(strategy = DynamoDBAutoGenerateStrategy.ALWAYS)
  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setCertificateData(String certificateData) {
    this.certificateData = certificateData;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }


  public String toJSON() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(this);
  }

  public boolean idIsValid() {
    return this.id != null && !this.id.isEmpty();
  }


  public static boolean containsOnce(final String s, final CharSequence substring) {
    Pattern pattern = Pattern.compile(substring.toString());
    Matcher matcher = pattern.matcher(s);
    if (matcher.find()) {
      return !matcher.find();
    }
    return false;
  }
}

