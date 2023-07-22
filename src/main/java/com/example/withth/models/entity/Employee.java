package com.example.withth.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Builder
public class Employee implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String matriculate;
    private String name;
    private LocalDate birthDate;
    private String profilePicture;
    private byte[] content;
    private long size;
    // TODO an employee can have many phone number
    @OneToMany(cascade = CascadeType.ALL)
    private List<Phone> phoneNumbers;
    private String address;
    private String privateMail;
    private String publicMail;
    //    number, date et lieu de deliverance,
    private String cin;
    private String function;
    private Integer childrens;
    private Date entryDate = new Date();
    private Date departureDate;
    private String professionalCategory;
    private String cnaps;

    private enum sex {H, F}

    public String getMatriculate() {
        return "EMP" + this.id;
    }
}

