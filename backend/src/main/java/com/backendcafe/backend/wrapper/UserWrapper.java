package com.backendcafe.backend.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWrapper{
    private  Integer id;
    private String name;
    private String emil;

    private String contactNumber;
    private String status;
}
