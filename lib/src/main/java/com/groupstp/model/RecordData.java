package com.groupstp.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;

public class RecordData implements PackageData {
    protected SubRecordType subRecordType;
    protected Short subRecordLength = 0;
    protected PackageData subRecordData;

    public RecordData(double lat, double lng, double speed, int dir, Date date, boolean isMove) {
        subRecordData = new SubRecordData(lat, lng, speed, dir, date, isMove);
    }


    public RecordData() {
    }

    @Override
    public PackageData decode(byte[] bytes) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        try {
            subRecordType = SubRecordType.fromId(inputStream.read());
            subRecordLength = ByteBuffer.wrap(inputStream.readNBytes(2)).order(ByteOrder.LITTLE_ENDIAN).getShort();
            byte[] subRecordBytes = inputStream.readNBytes(subRecordLength);
            subRecordData = new SrResponse();
            subRecordData.decode(subRecordBytes);
        } catch (IOException exception) {
            System.out.println("RecordData decode error " + exception.getMessage());
            return null;
        }
        return this;
    }

    @Override
    public byte[] encode() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            if (subRecordType == null) {
                subRecordType = SubRecordType.POS_Data;
            }

            outputStream.write(subRecordType.getId());

            if (subRecordLength == 0) {
                subRecordLength = subRecordData.length().shortValue();
            }
            outputStream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(subRecordLength).array());
            outputStream.write(subRecordData.encode());
        } catch (IOException exception) {
            System.out.println("RecordData encode error " + exception.getMessage());
            return null;
        }
        return outputStream.toByteArray();
    }

    @Override
    public Integer length() {
        return null;
    }
}
