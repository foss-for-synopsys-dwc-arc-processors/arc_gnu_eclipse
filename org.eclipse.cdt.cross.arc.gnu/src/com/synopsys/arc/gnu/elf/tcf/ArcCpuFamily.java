package com.synopsys.arc.gnu.elf.tcf;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

enum ArcCpuFamily
{
    ARCEM("ARC EM", Set.of("arcem", "em", "em4", "em4_dmips", "em4_fpus", "em4_fpuda")),
    ARCHS("ARC HS", Set.of("archs", "hs", "hs34", "hs38", "hs38_linux")),
    ARC600("ARC600", Set.of("arc700")),
    ARC700("ARC700", Set.of("arc600"));

    private final String name;
    private final Set<String> mcpuValues;

    ArcCpuFamily(String name, Set<String> mcpuValues)
    {
        this.name = name;
        // If argument is already an immutable set, then no new set is created by the copyOf.
        this.mcpuValues = Set.copyOf(mcpuValues);
    }

    @Override
    public String toString()
    {
        return name;
    }

    /**
     * Return a {@code ArcCpuFamily} that corresponds to a given {@code -mcpu} option.
     */
    public static Optional<ArcCpuFamily> fromMcpuOption(String option)
    {
        if (option == null) {
            return Optional.empty();
        }

        // Trim leading -mcpu=
        var value = option.substring(6);

        return Arrays.stream(ArcCpuFamily.values())
            .filter(cpu -> cpu.mcpuValues.contains(value))
            .findFirst();
    }
}
