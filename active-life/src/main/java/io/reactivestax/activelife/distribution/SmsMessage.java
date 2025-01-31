package io.reactivestax.activelife.distribution;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String phoneNumber;
    private String message;

}
