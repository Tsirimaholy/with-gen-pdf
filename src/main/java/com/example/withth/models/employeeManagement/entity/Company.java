package com.example.withth.models.employeeManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Builder
public class Company implements Serializable {
    @Id
    // only 1 company for now
    private Long id= 1L;

    private String name;
    private String description;
    private String slogan;
    private String address;
    private String emailContact;
    @OneToMany(mappedBy = "company", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Phone> phones = new ArrayList<>();
    private String nif;
    private String stat;
    private String rcs;
    @Lob
    private String logo;
    private byte[] logoB;
}
