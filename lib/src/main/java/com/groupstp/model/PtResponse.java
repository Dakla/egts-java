package com.groupstp.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class PtResponse implements PackageData {
    protected Short responsePackageId;
    protected Byte processingResult;
    protected List<PackageData> serviceDataRecord = new ArrayList<>();

    @Override
    public PackageData decode(byte[] bytes) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            this.responsePackageId = ByteBuffer.wrap(inputStream.readNBytes(2)).order(ByteOrder.LITTLE_ENDIAN).getShort();
            this.processingResult = inputStream.readNBytes(1)[0];
            if (inputStream.available() > 0) {
                serviceDataRecord.add(new ServiceDataRecord());
                serviceDataRecord.get(0).decode(inputStream.readNBytes(inputStream.available()));
            }
        } catch (IOException exception) {
            System.out.println("PtResponse decode error " + exception.getMessage());
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
