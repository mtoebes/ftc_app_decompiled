package com.ftdi.j2xx;

public class FT_EEPROM_2232H extends FT_EEPROM {
    public byte AH_DriveCurrent;
    public boolean AH_SchmittInput;
    public boolean AH_SlowSlew;
    public byte AL_DriveCurrent;
    public boolean AL_SchmittInput;
    public boolean AL_SlowSlew;
    public boolean A_FIFO;
    public boolean A_FIFOTarget;
    public boolean A_FastSerial;
    public boolean A_LoadD2XX;
    public boolean A_LoadVCP;
    public boolean A_UART;
    public byte BH_DriveCurrent;
    public boolean BH_SchmittInput;
    public boolean BH_SlowSlew;
    public byte BL_DriveCurrent;
    public boolean BL_SchmittInput;
    public boolean BL_SlowSlew;
    public boolean B_FIFO;
    public boolean B_FIFOTarget;
    public boolean B_FastSerial;
    public boolean B_LoadD2XX;
    public boolean B_LoadVCP;
    public boolean B_UART;
    public boolean PowerSaveEnable;
    public int TPRDRV;

    public static final class DRIVE_STRENGTH {
    }

    public FT_EEPROM_2232H() {
        this.AL_SlowSlew = false;
        this.AL_SchmittInput = false;
        this.AL_DriveCurrent = (byte) 0;
        this.AH_SlowSlew = false;
        this.AH_SchmittInput = false;
        this.AH_DriveCurrent = (byte) 0;
        this.BL_SlowSlew = false;
        this.BL_SchmittInput = false;
        this.BL_DriveCurrent = (byte) 0;
        this.BH_SlowSlew = false;
        this.BH_SchmittInput = false;
        this.BH_DriveCurrent = (byte) 0;
        this.A_UART = false;
        this.B_UART = false;
        this.A_FIFO = false;
        this.B_FIFO = false;
        this.A_FIFOTarget = false;
        this.B_FIFOTarget = false;
        this.A_FastSerial = false;
        this.B_FastSerial = false;
        this.PowerSaveEnable = false;
        this.A_LoadVCP = false;
        this.B_LoadVCP = false;
        this.A_LoadD2XX = false;
        this.B_LoadD2XX = false;
        this.TPRDRV = 0;
    }
}
