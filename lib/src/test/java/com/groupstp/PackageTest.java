package com.groupstp;

import com.groupstp.model.Package;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PackageTest {
    @Test void encodeTest() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, Calendar.DECEMBER, 29, 0, 0, 0);
        Package aPackage = new Package(50, 30, 20, 132, 15, calendar.getTime(), false, (short) 1);
        assertEquals(aPackage.toHex(),
                "0100030B0023000100010418000000190F0000000202101500FF698E168DE3388EAAAAAA2A0114840100000000005117",
                "Package hex string should be equals");
    }

    @Test void decodeTest() {
        byte[] bytes = new byte[] {1, 0, 0, 11, 0, 20, 0, 18, 0, 0, 95, 1, 0, 0, 6, 0, 0, 0, 65, -18, -114, 0, 0, 2, 2, 0, 3, 0, 0, 0, 0, 100, 108};
        Package aPackage = new Package((byte) 0);
        Package decode = (Package) aPackage.decode(bytes);
        assertNotNull(decode);
    }
}
