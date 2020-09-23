/*
 *  This file is part of Player Analytics (Plan).
 *
 *  Plan is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License v3 as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Plan is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Plan. If not, see <https://www.gnu.org/licenses/>.
 */
package com.djrapitops.plan.delivery.formatting.time;

import com.djrapitops.plan.delivery.formatting.Formatter;
import com.djrapitops.plan.settings.config.PlanConfig;
import com.djrapitops.plan.settings.config.paths.FormatSettings;
import org.apache.commons.lang3.StringUtils;

/**
 * Formatter for time amount in milliseconds.
 *
 * @author Rsl1122
 */
public class HourAmountFormatter implements Formatter<Long> {

    // Placeholders for the config settings
    private static final String ZERO_PH = "%zero%";
    private static final String SECONDS_PH = "%seconds%";
    private static final String MINUTES_PH = "%minutes%";
    private static final String HOURS_PH = "%hours%";
    private static final String DAYS_PH = "%days%";
    private static final String MONTHS_PH = "%months%";
    private static final String YEARS_PH = "%years%";

    private final PlanConfig config;

    public HourAmountFormatter(PlanConfig config) {
        this.config = config;
    }

    @Override
    public String apply(Long ms) {
        if (ms == null || ms < 0) {
            return "-";
        }
        StringBuilder builder = new StringBuilder();
        long x = ms / 1000;
        x /= 3600;
        long hours = x;

        String hourFormat = config.get(FormatSettings.HOURS);

        appendHours(builder, hours, hourFormat);

        String formattedTime = StringUtils.remove(builder.toString(), ZERO_PH);
        if (formattedTime.isEmpty()) {
            return config.get(FormatSettings.ZERO_SECONDS);
        }
        return formattedTime;
    }

    private void appendHours(StringBuilder builder, long hours, String fHours) {
        if (hours != 0) {
            String h = fHours.replace(HOURS_PH, String.valueOf(hours));
            if (h.contains(ZERO_PH) && String.valueOf(hours).length() == 1) {
                builder.append('0');
            }
            builder.append(h);
        }
    }

}
