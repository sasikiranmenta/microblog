package edu.sjsu.cmpe272.simpleblog.server.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
//@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Integer msgId;

    @ManyToOne
    UserDetails author;

    @Lob
    @Column( columnDefinition = "CLOB")
    String message;

    @Lob
    @Column(columnDefinition = "CLOB")
    String attachment;


    Date sentOn;

    @Lob
    @Column(columnDefinition = "CLOB")
    String signature;
}
