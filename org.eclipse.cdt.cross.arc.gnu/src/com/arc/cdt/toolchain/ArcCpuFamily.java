package com.arc.cdt.toolchain;

public enum ArcCpuFamily {
    ARCEM("ARC EM"),
    ARCHS("ARC HS"),
    ARC600("ARC 600"),
    ARC700("ARC 700");

    private String name;

    private ArcCpuFamily(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public ArcCpuFamily fromString(String string) {
        for (ArcCpuFamily family : ArcCpuFamily.values()) {
            if (family.toString().endsWith(string.toUpperCase())) {
                return family;
            }
        }
        throw new IllegalArgumentException("String does not correspond to any ARC CPU family.");
    }
}
