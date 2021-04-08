package jpa.entity.inheritance.model;

import javax.persistence.*;

@MappedSuperclass
public abstract class Devices {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name="brand")
    private String brand;

    @Column(name="name")
    private String name;

}
