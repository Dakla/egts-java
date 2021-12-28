package com.groupstp.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SrResponse implements PackageData {
    protected Short confirmedRecordNumber;
    protected Byte recordStatus;

    @Override
    public PackageData decode(byte[] bytes) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        try {
            confirmedRecordNumber = ByteBuffer.wrap(inputStream.readNBytes(2)).order(ByteOrder.LITTLE_ENDIAN).getShort();
            recordStatus = inputStream.readNBytes(1)[0];
        } catch (IOException exception) {
            System.out.println("SrResponse decode error " + exception.getMessage());
            return null;
        }
        return this;
    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }

    @Override
    public Integer length() {
        return null;
    }
}
