package com.groupstp.model;

import java.io.IOException;

public interface PackageData {
    PackageData decode(byte[] bytes);
    byte[] encode();
    Integer length();
}
