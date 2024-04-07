package edu.sjsu.cmpe272.simpleblog.server.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Integer msgId;

    @ManyToOne
    UserDetails author;

    @Lob
    @Column(columnDefinition = "CLOB")
    String message;

    @Lob
    @Column(columnDefinition = "CLOB")
    String attachment;

    Date sentOn;

    @Lob
    @Column(columnDefinition = "CLOB")
    String signature;
}
