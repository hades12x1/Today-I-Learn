package jpa.entity.inheritance.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "mobile_phone")
public class MobilePhone extends Devices{
   //Any new object and field here

    @Column(name="price")
    private Float price;
}