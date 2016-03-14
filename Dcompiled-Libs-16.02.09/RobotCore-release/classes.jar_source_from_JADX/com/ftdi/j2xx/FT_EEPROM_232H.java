package com.ftdi.j2xx;

public class FT_EEPROM_232H extends FT_EEPROM {
    public byte AL_DriveCurrent;
    public boolean AL_SchmittInput;
    public boolean AL_SlowSlew;
    public byte BL_DriveCurrent;
    public boolean BL_SchmittInput;
    public boolean BL_SlowSlew;
    public byte CBus0;
    public byte CBus1;
    public byte CBus2;
    public byte CBus3;
    public byte CBus4;
    public byte CBus5;
    public byte CBus6;
    public byte CBus7;
    public byte CBus8;
    public byte CBus9;
    public boolean FIFO;
    public boolean FIFOTarget;
    public boolean FT1248;
    public boolean FT1248ClockPolarity;
    public boolean FT1248FlowControl;
    public boolean FT1248LSB;
    public boolean FastSerial;
    public boolean LoadD2XX;
    public boolean LoadVCP;
    public boolean PowerSaveEnable;
    public boolean UART;

    public static final class CBUS {
    }

    public static final class DRIVE_STRENGTH {
    }

    public FT_EEPROM_232H() {
        this.AL_SlowSlew = false;
        this.AL_SchmittInput = false;
        this.AL_DriveCurrent = (byte) 0;
        this.BL_SlowSlew = false;
        this.BL_SchmittInput = false;
        this.BL_DriveCurrent = (byte) 0;
        this.CBus0 = (byte) 0;
        this.CBus1 = (byte) 0;
        this.CBus2 = (byte) 0;
        this.CBus3 = (byte) 0;
        this.CBus4 = (byte) 0;
        this.CBus5 = (byte) 0;
        this.CBus6 = (byte) 0;
        this.CBus7 = (byte) 0;
        this.CBus8 = (byte) 0;
        this.CBus9 = (byte) 0;
        this.UART = false;
        this.FIFO = false;
        this.FIFOTarget = false;
        this.FastSerial = false;
        this.FT1248 = false;
        this.FT1248ClockPolarity = false;
        this.FT1248LSB = false;
        this.FT1248FlowControl = false;
        this.PowerSaveEnable = false;
        this.LoadVCP = false;
        this.LoadD2XX = false;
    }
}
