package com.omninet.core;

public final class OmniConstitution {

    private OmniConstitution() {}

    // ─── AĞ KİMLİĞİ ───────────────────────────────────────
    public static final String COUNTRY_CODE     = "+777";
    public static final String NETWORK_PREFIX   = "3543";
    public static final String FOUNDER_NICK     = "bas3543";

    // Kurucu numaraları
    public static final String MASTER_ALPHA     = "+77735431077";
    public static final String MASTER_BETA      = "+77735431081";

    // ─── EKONOMİ ───────────────────────────────────────────
    public static final long   TOTAL_SUPPLY     = 1_000_000_000_000L;
    public static final double TICK_EMISSION    = 3170.979198;
    public static final long   EMISSION_END     = 2082758400000L;
    public static final int    MIN_BATTERY      = 15;

    // ─── ÖDÜL AĞIRLIKLARI ──────────────────────────────────
    public static final double WEIGHT_RELAY     = 0.45;
    public static final double WEIGHT_UPTIME    = 0.30;
    public static final double WEIGHT_TRUST     = 0.15;
    public static final double WEIGHT_GATEWAY   = 0.10;

    // ─── AĞ PARAMETRELERİ ──────────────────────────────────
    public static final int    MAX_HOPS         = 10;
    public static final double MAX_RANGE_KM     = 360.0;
    public static final double MIN_TX_AMOUNT    = 0.000001;
    public static final double TX_FEE_RATE      = 0.001;
}
