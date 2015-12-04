package com.ftdi.j2xx;

public class FT_EEPROM_X_Series extends FT_EEPROM {
    public byte AC_DriveCurrent;
    public boolean AC_SchmittInput;
    public boolean AC_SlowSlew;
    public byte AD_DriveCurrent;
    public boolean AD_SchmittInput;
    public boolean AD_SlowSlew;
    public short A_DeviceTypeValue;
    public boolean A_LoadD2XX;
    public boolean A_LoadVCP;
    public boolean BCDDisableSleep;
    public boolean BCDEnable;
    public boolean BCDForceCBusPWREN;
    public byte CBus0;
    public byte CBus1;
    public byte CBus2;
    public byte CBus3;
    public byte CBus4;
    public byte CBus5;
    public byte CBus6;
    public boolean FT1248ClockPolarity;
    public boolean FT1248FlowControl;
    public boolean FT1248LSB;
    public int I2CDeviceID;
    public boolean I2CDisableSchmitt;
    public int I2CSlaveAddress;
    public boolean InvertCTS;
    public boolean InvertDCD;
    public boolean InvertDSR;
    public boolean InvertDTR;
    public boolean InvertRI;
    public boolean InvertRTS;
    public boolean InvertRXD;
    public boolean InvertTXD;
    public boolean PowerSaveEnable;
    public boolean RS485EchoSuppress;

    public static final class CBUS {
    }

    public static final class DRIVE_STRENGTH {
    }

    public FT_EEPROM_X_Series() {
        this.A_DeviceTypeValue = (short) 0;
        this.A_LoadVCP = false;
        this.A_LoadD2XX = false;
        this.BCDEnable = false;
        this.BCDForceCBusPWREN = false;
        this.BCDDisableSleep = false;
        this.CBus0 = (byte) 0;
        this.CBus1 = (byte) 0;
        this.CBus2 = (byte) 0;
        this.CBus3 = (byte) 0;
        this.CBus4 = (byte) 0;
        this.CBus5 = (byte) 0;
        this.CBus6 = (byte) 0;
        this.FT1248ClockPolarity = false;
        this.FT1248LSB = false;
        this.FT1248FlowControl = false;
        this.InvertTXD = false;
        this.InvertRXD = false;
        this.InvertRTS = false;
        this.InvertCTS = false;
        this.InvertDTR = false;
        this.InvertDSR = false;
        this.InvertDCD = false;
        this.InvertRI = false;
        this.I2CSlaveAddress = 0;
        this.I2CDeviceID = 0;
        this.I2CDisableSchmitt = false;
        this.AD_SlowSlew = false;
        this.AD_SchmittInput = false;
        this.AD_DriveCurrent = (byte) 0;
        this.AC_SlowSlew = false;
        this.AC_SchmittInput = false;
        this.AC_DriveCurrent = (byte) 0;
        this.RS485EchoSuppress = false;
        this.PowerSaveEnable = false;
    }
}
