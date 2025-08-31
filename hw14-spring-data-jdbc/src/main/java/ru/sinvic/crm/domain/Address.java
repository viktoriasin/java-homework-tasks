package ru.sinvic.crm.domain;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table("address")
public record Address(
        @Id @Column("id") Long id, String street, @MappedCollection(keyColumn = "address_id") List<Client> clients) {}
