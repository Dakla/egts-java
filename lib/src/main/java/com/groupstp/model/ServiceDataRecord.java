package com.groupstp.model;

import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.lang.System.err;
import static java.lang.System.in;

public class ServiceDataRecord implements PackageData {
    protected Short recordLength = 0;
    protected Short recordNumber = 0;
    protected String sourceServiceOnDevice = "";
    protected String recipientServiceOnDevice = "0";
    protected String group = "0";
    protected String recordProcessingPriority = "11";
    protected String timeFieldExists = "0";
    protected String eventIDFieldExists = "0";
    protected String objectIDFieldExists = "1";
    protected Integer objectIdentifier;
    protected Integer eventIdentifier;
    protected Integer time;
    protected Byte sourceServiceType = 2;
    protected Byte recipientServiceType = 2;
    protected List<PackageData> recordDataSet = new ArrayList<>();

    public ServiceDataRecord(double lat, double lng, double speed, int dir, int objectId, Date date, boolean isMove) {
        objectIdentifier = objectId;
        recordDataSet.add(new RecordData(lat, lng, speed, dir, date, isMove));
    }

    public ServiceDataRecord() {
        Class<?> aClass = getClass();
        Object cast = aClass.cast(this);
    }

    @Override
    public PackageData decode(byte[] bytes) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        try {
            recordLength = ByteBuffer.wrap(inputStream.readNBytes(2)).order(ByteOrder.LITTLE_ENDIAN).getShort();

            recordNumber = ByteBuffer.wrap(inputStream.readNBytes(2)).order(ByteOrder.LITTLE_ENDIAN).getShort();
            String flags = Integer.toBinaryString(inputStream.read());
            char[] flagsArray = flags.toCharArray();
            if (flagsArray.length < 8) {
                while (flagsArray.length < 8) {
                    flagsArray = ArrayUtils.addFirst(flagsArray, '0');
                }
            }
            sourceServiceOnDevice = String.valueOf(flagsArray[0]);
            recipientServiceOnDevice = String.valueOf(flagsArray[1]);
            group = String.valueOf(flagsArray[2]);
            recordProcessingPriority = String.valueOf(flagsArray[3]) + flagsArray[4];
            timeFieldExists = String.valueOf(flagsArray[5]);
            eventIDFieldExists = String.valueOf(flagsArray[6]);
            objectIDFieldExists = String.valueOf(flagsArray[7]);

            if (objectIDFieldExists.equals("1")) {
                objectIdentifier = ByteBuffer.wrap(inputStream.readNBytes(4)).order(ByteOrder.LITTLE_ENDIAN).getInt();
            }

            if (eventIDFieldExists.equals("1")) {
                eventIdentifier = ByteBuffer.wrap(inputStream.readNBytes(4)).order(ByteOrder.LITTLE_ENDIAN).getInt();
            }

            if (timeFieldExists.equals("1")) {
                time = ByteBuffer.wrap(inputStream.readNBytes(4)).order(ByteOrder.LITTLE_ENDIAN).getInt();
            }

            sourceServiceType = inputStream.readNBytes(1)[0];
            recipientServiceType = inputStream.readNBytes(1)[0];

            if (inputStream.available() != 0) {
                byte[] rds = new byte[inputStream.available()];
                inputStream.read(rds);
                recordDataSet.add(new RecordData());
                recordDataSet.get(0).decode(rds);
            }
        } catch (IOException exception) {
            System.out.println("ServiceDataRecord decode error " + exception.getMessage());
            return null;
        }
        return this;
    }

    @Override
    public byte[] encode() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            List<Byte> recordDataArray = new ArrayList<>();
            for (PackageData packageData : recordDataSet) {
                recordDataArray.addAll(List.of(ArrayUtils.toObject(packageData.encode())));
            }

            if (recordLength == 0) {
                recordLength = (short) recordDataArray.size();
            }
            outputStream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(recordLength).array());
            outputStream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(recordNumber).array());
            //составной байт
            String flagsBits = sourceServiceOnDevice + recipientServiceOnDevice + group + recordProcessingPriority +
                    timeFieldExists + eventIDFieldExists + objectIDFieldExists;
            outputStream.write(Integer.parseInt(flagsBits, 2));
            if (objectIDFieldExists.equals("1")) {
                outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(objectIdentifier).array());
            }

            if (eventIDFieldExists.equals("1")) {
                outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(eventIdentifier).array());
            }

            if (timeFieldExists.equals("1")) {
                outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(time).array());
            }
            outputStream.write(sourceServiceType);
            outputStream.write(recipientServiceType);

            Byte[] array = recordDataArray.toArray(new Byte[0]);
            outputStream.write(ArrayUtils.toPrimitive(array));
        } catch (IOException exception) {
            System.out.println("ServiceDataRecord encode error " + exception.getMessage());
            return null;
        }
        return outputStream.toByteArray();
    }

    @Override
    public Integer length() {
        return null;
    }
}
