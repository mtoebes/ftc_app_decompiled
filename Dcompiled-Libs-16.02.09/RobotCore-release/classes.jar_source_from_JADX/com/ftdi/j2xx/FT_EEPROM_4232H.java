package com.ftdi.j2xx;

public class FT_EEPROM_4232H extends FT_EEPROM {
    public byte AH_DriveCurrent;
    public boolean AH_LoadD2XX;
    public boolean AH_LoadRI_RS485;
    public boolean AH_LoadVCP;
    public boolean AH_RI_TXDEN;
    public boolean AH_SchmittInput;
    public boolean AH_SlowSlew;
    public byte AL_DriveCurrent;
    public boolean AL_LoadD2XX;
    public boolean AL_LoadRI_RS485;
    public boolean AL_LoadVCP;
    public boolean AL_RI_TXDEN;
    public boolean AL_SchmittInput;
    public boolean AL_SlowSlew;
    public byte BH_DriveCurrent;
    public boolean BH_LoadD2XX;
    public boolean BH_LoadRI_RS485;
    public boolean BH_LoadVCP;
    public boolean BH_RI_TXDEN;
    public boolean BH_SchmittInput;
    public boolean BH_SlowSlew;
    public byte BL_DriveCurrent;
    public boolean BL_LoadD2XX;
    public boolean BL_LoadRI_RS485;
    public boolean BL_LoadVCP;
    public boolean BL_RI_TXDEN;
    public boolean BL_SchmittInput;
    public boolean BL_SlowSlew;
    public int TPRDRV;

    public static final class DRIVE_STRENGTH {
    }

    public FT_EEPROM_4232H() {
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
        this.AL_RI_TXDEN = false;
        this.AH_RI_TXDEN = false;
        this.BL_RI_TXDEN = false;
        this.BH_RI_TXDEN = false;
        this.AL_LoadVCP = false;
        this.AL_LoadD2XX = false;
        this.AL_LoadRI_RS485 = false;
        this.AH_LoadVCP = false;
        this.AH_LoadD2XX = false;
        this.AH_LoadRI_RS485 = false;
        this.BL_LoadVCP = false;
        this.BL_LoadD2XX = false;
        this.BL_LoadRI_RS485 = false;
        this.BH_LoadVCP = false;
        this.BH_LoadD2XX = false;
        this.BH_LoadRI_RS485 = false;
        this.TPRDRV = 0;
    }
}
