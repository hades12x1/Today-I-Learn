package jpa.entity.inheritance.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "computer")
public class Computer extends Devices {
   //Any new object and field here

    @Column(name="type")
    private String type;

}