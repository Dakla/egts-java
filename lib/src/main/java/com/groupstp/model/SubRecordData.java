package com.groupstp.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.Date;

public class SubRecordData implements PackageData {
    protected Date navigationTime;
    protected Double latitude;
    protected Double longitude;
    //    наличие поля ALT в подзаписи
    protected String alte = "0";
    //    полушарие долготы
    protected String lohs = "0";
    //    полушарие широты
    protected String lahs = "0";
    //    признак движения
    protected String mv;
    //    признак отправки данных из памяти
    protected String bb = "0";
    //    тип определения координат
    protected String cs = "0";
    //    тип используемой системы
    protected String fix = "0";
    //    признак "валидности" координатных данных
    protected String vld = "1";
    //    старший бит (8) параметра DIR
    protected Integer directionHighestBit;
    //    определяет высоту относительно уровня моря
    protected Integer altitudeSign = 0;
    protected double speed;
    protected Integer direction;
    protected Long odometer = 1L;
    //    битовые флаги, определяют состояние основных дискретных входов
    protected Byte digitalInputs = 0;
    //    определяет источник (событие), инициировавший посылку данной навигационной информации
    protected Byte source = 0;
    //    высота над уровнем моря необязательно
    protected Integer altitude;
    //    данные, характеризующие источник (событие) из поля SRC необязательно
    protected Integer sourceData;

    public SubRecordData(double lat, double lng, double speed, int dir, Date date, boolean isMove) {
        this.latitude = lat;
        this.longitude = lng;
        this.speed = speed;
        this.direction = dir;
        this.directionHighestBit = Integer.highestOneBit(dir);
        this.navigationTime = date;
        mv = isMove ? "1" : "0";
    }

    public SubRecordData() {
    }

    @Override
    public PackageData decode(byte[] bytes) {
        return null;
    }

    @Override
    public byte[] encode() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2010, Calendar.JANUARY, 1, 0, 0, 0);
        Date date = calendar.getTime();
        outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt((int) ((navigationTime.getTime() - date.getTime()) / 1000)).array());

        byte[] latBufferLong = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong((long) (Math.abs(latitude) / 90 * Long.parseLong("FFFFFFFF", 16))).array();
        byte[] latBuffer = new byte[4];
        System.arraycopy(latBufferLong, 0, latBuffer, 0, 4);
        outputStream.write(latBuffer);

        outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt((int) (Math.abs(longitude) / 180 * Long.parseLong("FFFFFFFF", 16))).array());

        outputStream.write(Integer.parseInt(alte + lohs + lahs + mv + bb + cs + fix + vld, 2));

            int intSpeed = (int) (speed * 10);
            outputStream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) (intSpeed & ((1 << 14) - 1))).array());

        outputStream.write(direction);

        byte[] odometerTmp = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(odometer).array();
        byte[] odometerArray = new byte[3];
        System.arraycopy(odometerTmp, 0, odometerArray, 0, 3);
        outputStream.write(odometerArray);
        outputStream.write(digitalInputs);
        outputStream.write(source);
        } catch (IOException exception) {
            System.out.println("SubRecordData encode error " + exception.getMessage());
            return null;
        }
        return outputStream.toByteArray();
    }

    @Override
    public Integer length() {
        return encode().length;
    }
}
