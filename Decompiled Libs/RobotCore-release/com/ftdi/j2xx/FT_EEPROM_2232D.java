package com.ftdi.j2xx;

public class FT_EEPROM_2232D extends FT_EEPROM {
    public boolean A_FIFO;
    public boolean A_FIFOTarget;
    public boolean A_FastSerial;
    public boolean A_HighIO;
    public boolean A_LoadD2XX;
    public boolean A_LoadVCP;
    public boolean A_UART;
    public boolean B_FIFO;
    public boolean B_FIFOTarget;
    public boolean B_FastSerial;
    public boolean B_HighIO;
    public boolean B_LoadD2XX;
    public boolean B_LoadVCP;
    public boolean B_UART;

    public FT_EEPROM_2232D() {
        this.A_UART = false;
        this.B_UART = false;
        this.A_HighIO = false;
        this.B_HighIO = false;
        this.A_FIFO = false;
        this.B_FIFO = false;
        this.A_FIFOTarget = false;
        this.B_FIFOTarget = false;
        this.A_FastSerial = false;
        this.B_FastSerial = false;
        this.A_LoadVCP = false;
        this.B_LoadVCP = false;
        this.A_LoadD2XX = false;
        this.B_LoadD2XX = false;
    }
}
