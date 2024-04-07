package edu.sjsu.cmpe272.simpleblog.server.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class UserDetails {

    @Id
    String id;

    @Lob
    @Column(name = "PUBLIC_KEY", columnDefinition = "CLOB")
    String publicKey;

}
