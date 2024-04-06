package edu.sjsu.cmpe272.simpleblog.server.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
//@Table(name = "user_details")
public class UserDetails {

    @Id
//    @Column(name= "AUTHOR_ID")
    String id;

    @Lob
    @Column(name = "PUBLIC_KEY", columnDefinition = "CLOB")
    String publicKey;



}
