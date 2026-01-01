package ru.sinvic.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ClientCreateDto {
    private long clientId;
    private String name;
    private String street;
    private String phoneNumber;
}
