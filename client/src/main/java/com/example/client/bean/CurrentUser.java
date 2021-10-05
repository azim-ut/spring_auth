package com.example.client.bean;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class CurrentUser {
    private String name;
    private String email;
    private boolean enabled;
}
