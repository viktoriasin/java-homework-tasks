package ru.sinvic.crm.domain;

import org.springframework.data.relational.core.mapping.Table;

@Table("phone")
public record Phone(Long clientId, String number) {}
