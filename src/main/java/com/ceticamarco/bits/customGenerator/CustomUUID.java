package com.ceticamarco.bits.customGenerator;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.util.UUID;
public class CustomUUID implements IdentifierGenerator {
    @Override
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        return UUID.randomUUID().toString().substring(0, 6);
    }
}
