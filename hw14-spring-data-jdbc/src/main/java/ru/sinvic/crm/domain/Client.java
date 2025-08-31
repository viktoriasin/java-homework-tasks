package ru.sinvic.crm.domain;

import jakarta.annotation.Nonnull;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table("client")
public record Client(
        @Id @Column("id") Long id,
        @Nonnull String name,
        Long addressId,
        @MappedCollection(idColumn = "client_id") List<Phone> phones) {}
