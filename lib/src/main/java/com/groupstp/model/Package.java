package com.groupstp.model;

import com.groupstp.Crc;
import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Package implements PackageData {
    protected Byte protocolVersion = 1;
    protected Byte securityKeyID = 0;
    protected Flags flags = new Flags();
    protected Integer headerLength = Integer.valueOf(DEFAULT_HEADER_LEN);
    protected Byte headerEncoding = 0;
    protected Short frameDataLength;
    protected Short packetIdentifier = 1;
    protected Byte packetType;
    protected Short peerAddress;
    protected Short recipientAddress;
    protected Byte timeToLive;
    protected Byte headerCheckSum;
    protected List<PackageData> servicesFrameData = new ArrayList<>();
    protected Short serviceFrameDataCheckSum;

    protected static Byte DEFAULT_HEADER_LEN = 11;

    public Package(Byte packetType) {
        this.packetType = packetType;
    }

    public Package(double lat, double lng, double speed, int dir, int objectId, Date date, boolean isMove, short packetId) {
        this.packetType = 1;
        this.packetIdentifier = packetId;
        servicesFrameData.add(new ServiceDataRecord(lat, lng, speed, dir, objectId, date, isMove));
    }


    @Override
    public PackageData decode(byte[] bytes) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        try {
            this.protocolVersion = inputStream.readNBytes(1)[0];
            this.securityKeyID = inputStream.readNBytes(1)[0];
            String flags = Integer.toBinaryString(inputStream.read());
            char[] flagsArray = flags.toCharArray();
            if (flagsArray.length < 7) {
                while (flagsArray.length < 7) {
                    flagsArray = ArrayUtils.addFirst(flagsArray, '0');
                }
            }
            this.flags.prefix = String.valueOf(flagsArray[0]) + flagsArray[1];
            this.flags.route = String.valueOf(flagsArray[2]);
            this.flags.encryptionAlg = String.valueOf(flagsArray[3]) + flagsArray[4];
            this.flags.compression = String.valueOf(flagsArray[5]);
            this.flags.priority = String.valueOf(flagsArray[6]);
            this.headerLength = inputStream.read();
            this.headerEncoding = inputStream.readNBytes(1)[0];
            this.frameDataLength = ByteBuffer.wrap(inputStream.readNBytes(2)).order(ByteOrder.LITTLE_ENDIAN).getShort();
            this.packetIdentifier = ByteBuffer.wrap(inputStream.readNBytes(2)).order(ByteOrder.LITTLE_ENDIAN).getShort();
            this.packetType = inputStream.readNBytes(1)[0];
            if (this.flags.route.equals("1")) {
                this.peerAddress = ByteBuffer.wrap(inputStream.readNBytes(2)).order(ByteOrder.LITTLE_ENDIAN).getShort();
                this.recipientAddress = ByteBuffer.wrap(inputStream.readNBytes(2)).order(ByteOrder.LITTLE_ENDIAN).getShort();
                this.timeToLive = inputStream.readNBytes(1)[0];
            }
            this.headerCheckSum = inputStream.readNBytes(1)[0];
            servicesFrameData.add(new PtResponse());
            servicesFrameData.get(0).decode(inputStream.readNBytes(this.frameDataLength));
            this.serviceFrameDataCheckSum = ByteBuffer.wrap(inputStream.readNBytes(2)).order(ByteOrder.LITTLE_ENDIAN).getShort();
        } catch (IOException exception) {
            System.out.println("Package decode error " + exception.getMessage());
            return null;
        }
        return this;
    }

    @Override
    public byte[] encode() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(this.protocolVersion);
            outputStream.write(this.securityKeyID);
            outputStream.write(this.flags.encode());
            if (this.flags.route.equals("1")) {
                headerLength += 5;
            }
            outputStream.write(this.headerLength);
            outputStream.write(this.headerEncoding);
            List<Byte> serviceFrameDataArray = new ArrayList<>();
            for (PackageData data : servicesFrameData) {
                serviceFrameDataArray.addAll(List.of(ArrayUtils.toObject(data.encode())));
            }
            this.frameDataLength = (short) serviceFrameDataArray.size();
            //-
            outputStream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(frameDataLength).array());
            //-
            outputStream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(packetIdentifier).array());
            outputStream.write(this.packetType);
            if (this.flags.route.equals("1")) {
                outputStream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(peerAddress).array());
                outputStream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(recipientAddress).array());
                outputStream.write(this.timeToLive);
            }
            outputStream.write(Crc.crc8(outputStream.toByteArray()));
            if (frameDataLength > 0) {
                Byte[] array = serviceFrameDataArray.toArray(new Byte[0]);
                outputStream.write(ArrayUtils.toPrimitive(array));
                outputStream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) Crc.crc16(array)).array());
            }
        } catch (IOException exception) {
            System.out.println("Package encode Error " + exception.getMessage());
            return null;
        }

        return outputStream.toByteArray();
    }

    final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);

    public String toHex() {
        byte[] bytes = this.encode();
        byte[] hexChars = new byte[bytes.length * 2];
        for(int j = 0; j<bytes.length;j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }

    @Override
    public Integer length() {
        return null;
    }
}
