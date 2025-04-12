package com.rooster.sistema.service;

import jakarta.transaction.Transactional;
import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;

@Service
public class LargeObjectService {
    @Transactional
    public Long saveLargeObject(Connection conn, byte[] data) throws SQLException {
        LargeObjectManager lom = conn.unwrap(PGConnection.class).getLargeObjectAPI();
        Long oid = lom.createLO(LargeObjectManager.READWRITE);

        try (LargeObject lo = lom.open(oid, LargeObjectManager.WRITE)) {
            lo.write(data);
        }
        return oid;
    }


    public byte[] readLargeObject(Connection conn, Long oid) throws SQLException {
        if (oid == null) return null;

        LargeObjectManager lom = conn.unwrap(PGConnection.class).getLargeObjectAPI();
        try (LargeObject lo = lom.open(oid, LargeObjectManager.READ)) {
            return lo.read(lo.size());
        }
    }
    @Transactional
    public void deleteLargeObject(Connection conn, Long oid) throws SQLException {
        if (oid == null) return;

        LargeObjectManager lom = conn.unwrap(PGConnection.class).getLargeObjectAPI();
        lom.delete(oid);
    }
}