package com.example.withth.models.employeeManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    @Enumerated(EnumType.STRING)
    private Sex sex;
    private String profilePicture;
    private byte[] content;
    private long size;
    private String address;
    private String privateMail;
    private String publicMail;
    //    number, date et lieu de deliverance,
    private String cin;
    private String function;
    private Integer childrens;
    @Temporal(TemporalType.DATE)
    private Date entryDate = new Date();
    @Temporal(TemporalType.DATE)
    private Date departureDate;
    private String professionalCategory;
    private String cnaps;
    private String password;

    @OneToMany(mappedBy = "employee", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Phone> phones = new ArrayList<>();

    public void addPhone(Phone phone){
        if (!this.getPhones().contains(phone)){
            this.getPhones().add(phone);
            phone.setEmployee(this);
        }
    }
    public void addAllPhones(List<Phone> phones){
        phones.forEach(this::addPhone);
    }

    public String getMatriculate() {
        return "EMP" + this.id;
    }
}

