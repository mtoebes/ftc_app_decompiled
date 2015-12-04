package com.ftdi.j2xx.ft4222;

public class FT_4222_Defines {
    public static final int CHIPTOP_DEBUG_REQUEST = 255;
    public static final int CHIPTOP_DEBUG_SET_CHIPTOP_REG = 1;
    public static final int CHIPTOP_DEBUG_SET_OTP_REG = 3;
    public static final int CHIPTOP_DEBUG_SET_SFR = 4;
    public static final int CHIPTOP_DEBUG_SET_USB_REG = 2;
    public static final int DEBUG_REQ_CHIP_STATUS = 0;
    public static final int DEBUG_REQ_READ_CHIPTOP_REG = 1;
    public static final int DEBUG_REQ_READ_OTP_DATA = 5;
    public static final int DEBUG_REQ_READ_OTP_REG = 3;
    public static final int DEBUG_REQ_READ_SFR = 4;
    public static final int DEBUG_REQ_READ_USB_REG = 2;

    public class CHIPTOP_CMD {
        public static final int CHIPTOP_DISABLE_BCD_DECTION = 8;
        public static final int CHIPTOP_ENABLE_REMOTE_WAKE_UP = 6;
        public static final int CHIPTOP_ENABLE_SUSPEND_OUT = 7;
        public static final int CHIPTOP_GET_CHIPTOP_REG = 177;
        public static final int CHIPTOP_GET_CLK = 4;
        public static final int CHIPTOP_GET_DESC_STRING = 176;
        public static final int CHIPTOP_GET_MODE = 3;
        public static final int CHIPTOP_GET_OTP_REG = 179;
        public static final int CHIPTOP_GET_OTP_TEST_BYTE = 192;
        public static final int CHIPTOP_GET_SFR = 180;
        public static final int CHIPTOP_GET_STATUS = 1;
        public static final int CHIPTOP_GET_USB_REG = 178;
        public static final int CHIPTOP_GET_USER_DATA_STATUS = 2;
        public static final int CHIPTOP_GET_VERSION = 0;
        public static final int CHIPTOP_RESET = 128;
        public static final int CHIPTOP_SELECT_FUNCTION = 5;
        public static final int CHIPTOP_SET_BCD_DECTION_POLARITY = 9;
        public static final int CHIPTOP_SET_CLK = 4;
        public static final int CHIPTOP_SET_CLK30K_TRIM_REG = 166;
        public static final int CHIPTOP_SET_DS_CTL0_REG = 160;
        public static final int CHIPTOP_SET_DS_CTL1_REG = 161;
        public static final int CHIPTOP_SET_DS_CTL2_REG = 162;
        public static final int CHIPTOP_SET_INTERRUPT_LEVEL = 16;
        public static final int CHIPTOP_SET_OSC_TRIM0_REG = 167;
        public static final int CHIPTOP_SET_OSC_TRIM1_REG = 168;
        public static final int CHIPTOP_SET_PHY_ODT_REG = 170;
        public static final int CHIPTOP_SET_PHY_TXCTL_REG = 169;
        public static final int CHIPTOP_SET_REG_TRIM_REG = 165;
        public static final int CHIPTOP_SET_SR_CTL0_REG = 163;
        public static final int CHIPTOP_SET_SR_CTL1_REG = 164;
        public static final int CHIPTOP_WRITE_OTP_TEST_BYTE = 192;
        final /* synthetic */ FT_4222_Defines f49a;

        public CHIPTOP_CMD(FT_4222_Defines fT_4222_Defines) {
            this.f49a = fT_4222_Defines;
        }
    }

    public class FT4222_ClockRate {
        public static final int SYS_CLK_24 = 1;
        public static final int SYS_CLK_48 = 2;
        public static final int SYS_CLK_60 = 0;
        public static final int SYS_CLK_80 = 3;
        final /* synthetic */ FT_4222_Defines f50a;

        public FT4222_ClockRate(FT_4222_Defines fT_4222_Defines) {
            this.f50a = fT_4222_Defines;
        }
    }

    public class FT4222_FUNCTION {
        public static final int FT4222_I2C_MASTER = 1;
        public static final int FT4222_I2C_SLAVE = 2;
        public static final int FT4222_SPI_MASTER = 3;
        public static final int FT4222_SPI_SLAVE = 4;
        final /* synthetic */ FT_4222_Defines f51a;

        public FT4222_FUNCTION(FT_4222_Defines fT_4222_Defines) {
            this.f51a = fT_4222_Defines;
        }
    }

    public class FT4222_SPICPHA {
        public static final int CLK_LEADING = 0;
        public static final int CLK_TRAILING = 1;
        final /* synthetic */ FT_4222_Defines f52a;

        public FT4222_SPICPHA(FT_4222_Defines fT_4222_Defines) {
            this.f52a = fT_4222_Defines;
        }
    }

    public class FT4222_SPICPOL {
        public static final int CLK_ACTIVE_HIGH = 1;
        public static final int CLK_ACTIVE_LOW = 0;
        final /* synthetic */ FT_4222_Defines f53a;

        public FT4222_SPICPOL(FT_4222_Defines fT_4222_Defines) {
            this.f53a = fT_4222_Defines;
        }
    }

    public class FT4222_SPIClock {
        public static final int CLK_DIV_128 = 7;
        public static final int CLK_DIV_16 = 4;
        public static final int CLK_DIV_2 = 1;
        public static final int CLK_DIV_256 = 8;
        public static final int CLK_DIV_32 = 5;
        public static final int CLK_DIV_4 = 2;
        public static final int CLK_DIV_512 = 9;
        public static final int CLK_DIV_64 = 6;
        public static final int CLK_DIV_8 = 3;
        public static final int CLK_NONE = 0;
        final /* synthetic */ FT_4222_Defines f54a;

        public FT4222_SPIClock(FT_4222_Defines fT_4222_Defines) {
            this.f54a = fT_4222_Defines;
        }
    }

    public class FT4222_SPIMode {
        public static final int SPI_IO_DUAL = 2;
        public static final int SPI_IO_NONE = 0;
        public static final int SPI_IO_QUAD = 4;
        public static final int SPI_IO_SINGLE = 1;
        final /* synthetic */ FT_4222_Defines f55a;

        public FT4222_SPIMode(FT_4222_Defines fT_4222_Defines) {
            this.f55a = fT_4222_Defines;
        }
    }

    public class FT4222_STATUS {
        public static final int FT4222_CLK_NOT_SUPPORTED = 1001;
        public static final int FT4222_DEVICE_LIST_NOT_READY = 19;
        public static final int FT4222_DEVICE_NOT_FOUND = 2;
        public static final int FT4222_DEVICE_NOT_OPENED = 3;
        public static final int FT4222_DEVICE_NOT_OPENED_FOR_ERASE = 8;
        public static final int FT4222_DEVICE_NOT_OPENED_FOR_WRITE = 9;
        public static final int FT4222_DEVICE_NOT_SUPPORTED = 1000;
        public static final int FT4222_EEPROM_ERASE_FAILED = 13;
        public static final int FT4222_EEPROM_NOT_PRESENT = 14;
        public static final int FT4222_EEPROM_NOT_PROGRAMMED = 15;
        public static final int FT4222_EEPROM_READ_FAILED = 11;
        public static final int FT4222_EEPROM_WRITE_FAILED = 12;
        public static final int FT4222_EVENT_NOT_SUPPORTED = 1021;
        public static final int FT4222_EXCEEDED_MAX_TRANSFER_SIZE = 1010;
        public static final int FT4222_FAILED_TO_READ_DEVICE = 1011;
        public static final int FT4222_FAILED_TO_WRITE_DEVICE = 10;
        public static final int FT4222_GPIO_EXCEEDED_MAX_PORTNUM = 1014;
        public static final int FT4222_GPIO_INPUT_NOT_SUPPORTED = 1020;
        public static final int FT4222_GPIO_NOT_SUPPORTED_IN_THIS_MODE = 1013;
        public static final int FT4222_GPIO_OPENDRAIN_INVALID_IN_OUTPUTMODE = 1018;
        public static final int FT4222_GPIO_PULLDOWN_INVALID_IN_INPUTMODE = 1017;
        public static final int FT4222_GPIO_PULLUP_INVALID_IN_INPUTMODE = 1016;
        public static final int FT4222_GPIO_WRITE_NOT_SUPPORTED = 1015;
        public static final int FT4222_I2C_NOT_SUPPORTED_IN_THIS_MODE = 1012;
        public static final int FT4222_INSUFFICIENT_RESOURCES = 5;
        public static final int FT4222_INTERRUPT_NOT_SUPPORTED = 1019;
        public static final int FT4222_INVAILD_FUNCTION = 1008;
        public static final int FT4222_INVALID_ARGS = 16;
        public static final int FT4222_INVALID_BAUD_RATE = 7;
        public static final int FT4222_INVALID_HANDLE = 1;
        public static final int FT4222_INVALID_PARAMETER = 6;
        public static final int FT4222_INVALID_POINTER = 1009;
        public static final int FT4222_IO_ERROR = 4;
        public static final int FT4222_IS_NOT_I2C_MODE = 1004;
        public static final int FT4222_IS_NOT_SPI_MODE = 1003;
        public static final int FT4222_IS_NOT_SPI_MULTI_MODE = 1006;
        public static final int FT4222_IS_NOT_SPI_SINGLE_MODE = 1005;
        public static final int FT4222_NOT_SUPPORTED = 17;
        public static final int FT4222_OK = 0;
        public static final int FT4222_OTHER_ERROR = 18;
        public static final int FT4222_VENDER_CMD_NOT_SUPPORTED = 1002;
        public static final int FT4222_WRONG_I2C_ADDR = 1007;
        final /* synthetic */ FT_4222_Defines f56a;

        public FT4222_STATUS(FT_4222_Defines fT_4222_Defines) {
            this.f56a = fT_4222_Defines;
        }
    }

    public class GPIO_Dir {
        public static final int GPIO_INPUT = 0;
        public static final int GPIO_OUTPUT = 1;
        final /* synthetic */ FT_4222_Defines f57a;

        public GPIO_Dir(FT_4222_Defines fT_4222_Defines) {
            this.f57a = fT_4222_Defines;
        }
    }

    public class GPIO_Output {
        public static final int GPIO_OUTPUT_HIGH = 1;
        public static final int GPIO_OUTPUT_LOW = 0;
        final /* synthetic */ FT_4222_Defines f58a;

        public GPIO_Output(FT_4222_Defines fT_4222_Defines) {
            this.f58a = fT_4222_Defines;
        }
    }

    public class GPIO_Port {
        public static final int GPIO_PORT0 = 0;
        public static final int GPIO_PORT1 = 1;
        public static final int GPIO_PORT2 = 2;
        public static final int GPIO_PORT3 = 3;
        final /* synthetic */ FT_4222_Defines f59a;

        public GPIO_Port(FT_4222_Defines fT_4222_Defines) {
            this.f59a = fT_4222_Defines;
        }
    }

    public class GPIO_Tigger {
        public static final int GPIO_TRIGGER_FALLING = 2;
        public static final int GPIO_TRIGGER_LEVEL_HIGH = 4;
        public static final int GPIO_TRIGGER_LEVEL_LOW = 8;
        public static final int GPIO_TRIGGER_RISING = 1;
        final /* synthetic */ FT_4222_Defines f60a;

        public GPIO_Tigger(FT_4222_Defines fT_4222_Defines) {
            this.f60a = fT_4222_Defines;
        }
    }

    public class I2C_CMD {
        public static final int I2C_MASTER_GET_HS = 83;
        public static final int I2C_MASTER_GET_I2CMTP = 82;
        public static final int I2C_MASTER_GET_STATUS = 80;
        public static final int I2C_MASTER_RESET = 81;
        public static final int I2C_MASTER_SET_HS = 83;
        public static final int I2C_MASTER_SET_I2CMTP = 82;
        public static final int I2C_SLAVE_GET_I2CSADR = 92;
        public static final int I2C_SLAVE_GET_STATUS = 90;
        public static final int I2C_SLAVE_RESET = 91;
        public static final int I2C_SLAVE_SET_I2CSADR = 92;
        final /* synthetic */ FT_4222_Defines f61a;

        public I2C_CMD(FT_4222_Defines fT_4222_Defines) {
            this.f61a = fT_4222_Defines;
        }
    }

    public class SPI_CMD {
        public static final int SPI_GET_CONFIG = 64;
        public static final int SPI_GET_STATUS = 65;
        public static final int SPI_SET_CLK = 68;
        public static final int SPI_SET_CPHA = 70;
        public static final int SPI_SET_CPOL = 69;
        public static final int SPI_SET_IO = 66;
        public static final int SPI_SET_RESET_TRANSACTION = 73;
        public static final int SPI_SET_RESTART_CONTROLLER = 74;
        public static final int SPI_SET_SLAVE_MAP = 72;
        public static final int SPI_SET_SS = 67;
        public static final int SPI_SET_TURBO = 71;
        final /* synthetic */ FT_4222_Defines f62a;

        public SPI_CMD(FT_4222_Defines fT_4222_Defines) {
            this.f62a = fT_4222_Defines;
        }
    }

    public class SPI_DrivingStrength {
        public static final int DS_12MA = 2;
        public static final int DS_16MA = 3;
        public static final int DS_4MA = 0;
        public static final int DS_8MA = 1;
        final /* synthetic */ FT_4222_Defines f63a;

        public SPI_DrivingStrength(FT_4222_Defines fT_4222_Defines) {
            this.f63a = fT_4222_Defines;
        }
    }

    public class SPI_SLAVE_CMD {
        public static final int FT4222_SPI_SLAVE_SYNC_WORD = 90;
        public static final int SPI_ACK = 132;
        public static final int SPI_MASTER_TRANSFER = 128;
        public static final int SPI_SHART_SLAVE_TRANSFER = 131;
        public static final int SPI_SHORT_MASTER_TRANSFER = 130;
        public static final int SPI_SLAVE_TRANSFER = 129;
        final /* synthetic */ FT_4222_Defines f64a;

        public SPI_SLAVE_CMD(FT_4222_Defines fT_4222_Defines) {
            this.f64a = fT_4222_Defines;
        }
    }

    public class SPI_SS {
        public static final int SPI_SS_NEGTIVE = 0;
        public static final int SPI_SS_POSITIVE = 1;
        final /* synthetic */ FT_4222_Defines f65a;

        public SPI_SS(FT_4222_Defines fT_4222_Defines) {
            this.f65a = fT_4222_Defines;
        }
    }
}
